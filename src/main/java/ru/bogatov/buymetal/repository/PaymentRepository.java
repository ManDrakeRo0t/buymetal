package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bogatov.buymetal.entity.Payment;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Set<Payment> findAllByCustomerId(UUID customerId);

    Optional<Payment> findPaymentByRelatedOrderId(UUID orderId);
}
