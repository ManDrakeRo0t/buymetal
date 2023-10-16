package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bogatov.buymetal.entity.*;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.enums.ApplicationStatus;
import ru.bogatov.buymetal.model.enums.OrderStatus;
import ru.bogatov.buymetal.model.enums.PaymentStatus;
import ru.bogatov.buymetal.model.enums.UserPosition;
import ru.bogatov.buymetal.model.request.OrderCreationRequest;
import ru.bogatov.buymetal.model.request.OrderSearchRequest;
import ru.bogatov.buymetal.model.request.UpdateOrderStatusRequest;
import ru.bogatov.buymetal.repository.OrderRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final ApplicationService applicationService;

    private final ApplicationResponseService applicationResponseService;

    private final OrderRepository orderRepository;

    private final PaymentService paymentService;

    private final UserService userService;

    private final Set<String> statusTransitions = Set.of(
            OrderStatus.OPEN.getValue() + OrderStatus.REJECTED.getValue(),
            OrderStatus.OPEN.getValue() + OrderStatus.AGREED.getValue(),
            OrderStatus.AGREED.getValue() + OrderStatus.REJECTED.getValue(),
            OrderStatus.AGREED.getValue() + OrderStatus.WAITING_PAYMENT.getValue(),
            OrderStatus.WAITING_PAYMENT.getValue() + OrderStatus.REJECTED.getValue(),
            OrderStatus.WAITING_PAYMENT.getValue() + OrderStatus.DELIVERY.getValue(),
            OrderStatus.DELIVERY.getValue() + OrderStatus.REJECTED.getValue(),
            OrderStatus.DELIVERY.getValue() + OrderStatus.COMPLETED.getValue()
    );

    private final Map<OrderStatus, Set<UserPosition>> statusUpdatePosition = new HashMap<>();
    {
        statusUpdatePosition.put(OrderStatus.REJECTED, Set.of(UserPosition.SUPPLIER));
        statusUpdatePosition.put(OrderStatus.AGREED, Set.of(UserPosition.SUPPLIER));
        statusUpdatePosition.put(OrderStatus.WAITING_PAYMENT, Set.of(UserPosition.SUPPLIER));
        statusUpdatePosition.put(OrderStatus.DELIVERY, Set.of(UserPosition.CUSTOMER));
        statusUpdatePosition.put(OrderStatus.COMPLETED, Set.of(UserPosition.CUSTOMER));
    }
    public Order createOrder(OrderCreationRequest body) {
        Application application = applicationService.findById(body.getApplicationId());
        ApplicationResponse response = applicationResponseService.findById(body.getApplicationResponseId());
        User customer = application.getCustomer();
        validateOrderCreation(application, response, customer);

        Order order = new Order();
        order.setCreationTime(LocalDate.now());
        order.setUpdateDate(LocalDate.now());
        order.setStatus(OrderStatus.OPEN);
        order.setApplication(application);
        order.setResponse(response);
        order.setCustomerId(application.getCustomer().getId());
        order.setSupplierId(response.getSupplier().getId());

        order = orderRepository.save(order);
        application.setStatus(ApplicationStatus.RESERVED);
        applicationService.save(application);

        return order;
    }
    @Transactional
    public Order updateOrderStatus(UUID orderId, UpdateOrderStatusRequest body) {
        Order order = findById(orderId);
        UserPosition whoUpdates = order.getCustomerId().equals(body.getInitiatorId()) ? UserPosition.CUSTOMER : UserPosition.SUPPLIER;

        if (!statusUpdatePosition.get(body.getTargetStatus()).contains(whoUpdates)) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, whoUpdates.name() + "Не может менять статус заказа на" + body.getTargetStatus().getValue());
        }

        if (!statusTransitions.contains(order.getStatus().getValue() + body.getTargetStatus().getValue())) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Такое обновление статуса недопустимо");
        }

        validateUser(userService.findById(order.getCustomerId()));

        processOrderStatusUpdate(order, body.getTargetStatus());
        order.setStatus(body.getTargetStatus());
        order.setUpdateDate(LocalDate.now());
        if (body.getTargetStatus() == OrderStatus.REJECTED) {
            order.setCanceledByCustomer(whoUpdates == UserPosition.CUSTOMER);
        }

        return orderRepository.save(order);
    }

    public void validateOrderCreation(Application application, ApplicationResponse response, User customer) {
        if (application.getStatus() != ApplicationStatus.OPEN) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Заявка должна быть в статусе OPEN");
        }

        validateUser(customer);

        application.getApplicationResponses()
                .stream()
                .filter(resp -> resp.getId().equals(response.getId())).findFirst()
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Ответ на завяку не найден"));
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR, "Заказ не найден"));
    }

    private void processOrderStatusUpdate(Order order, OrderStatus targetStatus) {
        switch (targetStatus) {
            case AGREED:
                order.setAgreementDate(LocalDate.now());
                break;
            case WAITING_PAYMENT:
                order.setStartDeliveryDate(LocalDate.now());
                double paymentAmount = order.getResponse().getFullPrice() * 0.01;
                paymentService.createPaymentUnderCustomer(order.getCustomerId(), order.getId(), paymentAmount);
                break;
            case DELIVERY:
                order.setPaymentDate(LocalDate.now());
                Payment payment = paymentService.getByOrderId(order.getId());
                payment.setStatus(PaymentStatus.PAID);
                break;
            case COMPLETED:
                order.setCompleteDate(LocalDate.now());
                Application application = order.getApplication();
                application.setStatus(ApplicationStatus.ARCHIVED);
                applicationService.save(application);
                //todo serviceToSendDocs
                break;
            case REJECTED:
                order.setRejectDate(LocalDate.now());
                Application orderApplication = order.getApplication();
                orderApplication.setStatus(ApplicationStatus.OPEN);
                applicationService.save(orderApplication);
                Payment orderPayment = paymentService.findByOrderId(order.getId());
                if (orderPayment != null) {
                    if (orderPayment.getStatus() == PaymentStatus.WAITING) {
                        orderPayment.setStatus(PaymentStatus.CANCELLED);
                        paymentService.savePayment(orderPayment);
                    }
                }
                break;
        }
    }

    public Set<Order> search(OrderSearchRequest body) {
        List<String> statuses = body.getStatuses()
                .stream().map(OrderStatus::getValue).collect(Collectors.toList());
        if (body.getCustomerId() != null) {
            return orderRepository.searchCustomerOrders(body.getCustomerId(), statuses, body.getLimit(), body.getOffset());
        }
        return orderRepository.searchSupplierOrders(body.getSupplierId(), statuses, body.getLimit(), body.getOffset());
    }

    public void validateUser(User user) {
        if (user.isBlocked()) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Пользователь заблокирован");
        }
    }
}
