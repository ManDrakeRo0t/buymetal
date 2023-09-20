package ru.bogatov.buymetal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.bogatov.buymetal.model.enums.ApplicationStatus;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="application")
public class Application extends ApplicationBaseParams {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private User customer;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<ApplicationResponse> applicationResponses;
}
