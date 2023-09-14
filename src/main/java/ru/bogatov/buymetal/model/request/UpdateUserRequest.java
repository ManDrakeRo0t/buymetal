package ru.bogatov.buymetal.model.request;

import lombok.Data;



@Data
public class UpdateUserRequest {
    private String fullName;
    private String companyName;
    private String companyAddress;
    private String tin;
    private String email;
    private String phone;
}
