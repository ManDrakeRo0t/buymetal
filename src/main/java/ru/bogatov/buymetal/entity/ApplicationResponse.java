package ru.bogatov.buymetal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="application_response")
public class ApplicationResponse extends ApplicationBaseParams {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private LocalDateTime deliverDate;

    private boolean inStock;

    private boolean isSimilar;

    private double price;

    private double fullPrice;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private User supplier;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

}
