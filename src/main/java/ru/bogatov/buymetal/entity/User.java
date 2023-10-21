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

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "is_email_confirmed")
    private boolean isMailConfirmed;

    private boolean isPhoneConfirmed;

    private boolean isBlocked;

    @JsonIgnore
    private String password;

    private String companyName;

    private String companyAddress;

    @Column(unique = true)
    private String tin;

    @Enumerated(EnumType.STRING)
    private UserPosition position;

    @Column(name = "phone",unique = true)
    private String phone;

    private String fullName;
    @Column(name = "refresh")
    @JsonIgnore
    private String refresh;

    private LocalDate registrationDate;

}
