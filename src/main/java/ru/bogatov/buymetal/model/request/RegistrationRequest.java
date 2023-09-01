package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.UserPosition;
@Data
public class RegistrationRequest {
    private String email;
    private String password;
    private String companyName;
    private String companyAddress;
    private String tin;
    private UserPosition position;
    private String phone;
    private String fullName;
}
