package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bogatov.buymetal.entity.*;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.CustomUserDetails;
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

    private final DocumentGenerationService documentGenerationService;

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
        CustomUserDetails userDetails = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserPosition whoUpdates = userDetails.getUserPosition();

//        if (whoUpdates.equals(UserPosition.CUSTOMER) && !userDetails.getUserId().equals(order.getApplication().getCustomer().getId())) {
//            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Пользователь не является заказчиком данного заказа");
//        }
//
//        if (whoUpdates.equals(UserPosition.SUPPLIER) && !userDetails.getUserId().equals(order.getResponse().getSupplier().getId())) {
//            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Пользователь не является поставщиком данного заказа");
//        }

        if (!statusUpdatePosition.get(body.getTargetStatus()).contains(whoUpdates)) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, whoUpdates.name() + " Не может менять статус заказа на " + body.getTargetStatus().getValue());
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

        UUID idFromToken = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        if (!idFromToken.equals(customer.getId())) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Только создатель заявки может создать заказ");
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

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
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
                documentGenerationService.generateDocument(order.getId());
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
        CustomUserDetails userDetails = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userDetails.getUserPosition().equals(UserPosition.CUSTOMER)) {
            return orderRepository.searchCustomerOrders(userDetails.getUserId(), statuses, body.getLimit(), body.getOffset());
        }

        if (userDetails.getUserPosition().equals(UserPosition.SUPPLIER)) {
            return orderRepository.searchSupplierOrders(userDetails.getUserId(), statuses, body.getLimit(), body.getOffset());
        }
        return Set.of();
    }

    public void validateUser(User user) {
        if (user.isBlocked()) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Пользователь заблокирован");
        }
    }

    public Map<String ,Set<Order>> getOrdersForStatistic(UUID userId, LocalDate from, LocalDate to) {
        return Map.of(
                "completed", orderRepository.searchCompetedOrdersForStatistic(userId, from, to),
                "rejected", orderRepository.searchRejectedOrdersForStatistic(userId, from, to),
                "created", orderRepository.searchCreatedOrdersForStatistic(userId, from, to)
        );
    }
}
