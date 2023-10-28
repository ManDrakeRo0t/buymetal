package ru.bogatov.buymetal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.bogatov.buymetal.model.enums.OrderStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID customerId;

    private UUID supplierId;

    private LocalDate creationTime;

    private LocalDate agreementDate;

    private LocalDate startDeliveryDate;

    private LocalDate paymentDate;

    private LocalDate completeDate;

    private LocalDate rejectDate;

    private LocalDate updateDate;

    private boolean canceledByCustomer;

    @OneToOne(fetch = FetchType.EAGER)
    private Application application;

    @OneToOne(fetch = FetchType.EAGER)
    private ApplicationResponse response;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    private File document;

}
