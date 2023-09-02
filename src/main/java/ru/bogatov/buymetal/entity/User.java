package ru.bogatov.buymetal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.bogatov.buymetal.model.enums.UserPosition;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="usr")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String email;

    private boolean isMailConfirmed;

    private boolean isBlocked;

    @JsonIgnore
    private String password;

    private String companyName;

    private String companyAddress;

    @Column(unique = true)
    private String tin;

    @Enumerated(EnumType.STRING)
    private UserPosition position;

    @Column(unique = true)
    private String phone;

    private String fullName;
    @Column(name = "refresh")
    private String refresh;

    private LocalDate registrationDate;

}
