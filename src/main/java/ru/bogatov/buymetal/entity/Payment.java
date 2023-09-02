package ru.bogatov.buymetal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.bogatov.buymetal.model.enums.PaymentStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID customerId;

    private UUID relatedOrderId;

    private double amount;

    private LocalDate paymentDueDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

}
