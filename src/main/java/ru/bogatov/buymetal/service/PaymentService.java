package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bogatov.buymetal.entity.Payment;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.enums.PaymentStatus;
import ru.bogatov.buymetal.repository.PaymentRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment findById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.COMMON_ERROR));
    }

    public Payment createPaymentUnderCustomer(UUID customerId, UUID orderId, double amount) {
        Payment payment = new Payment();
        payment.setCustomerId(customerId);
        payment.setRelatedOrderId(orderId);
        payment.setPaymentDueDate(LocalDate.now().plusWeeks(1));
        payment.setAmount(amount);
        return paymentRepository.save(payment);
    }

    public Set<Payment> findAllCustomerPayments(UUID customerId) {
        return paymentRepository.findAllByCustomerId(customerId);
    }

    public Payment getByOrderId(UUID orderId) {
        return paymentRepository.findPaymentByRelatedOrderId(orderId).orElseThrow(() -> ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Платеж для заказа не найден"));
    }

    public Payment findByOrderId(UUID orderId) {
        return paymentRepository.findPaymentByRelatedOrderId(orderId).orElse(null);
    }

    public void updatePaymentStatus(UUID id,PaymentStatus status) {
        Payment payment = findById(id);
        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
