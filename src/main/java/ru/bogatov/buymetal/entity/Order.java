package ru.bogatov.buymetal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.bogatov.buymetal.model.enums.OrderStatus;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    private Application application;

    @OneToOne(fetch = FetchType.EAGER)
    private ApplicationResponse response;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
