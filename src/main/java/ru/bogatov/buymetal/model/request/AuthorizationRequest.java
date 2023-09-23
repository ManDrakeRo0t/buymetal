package ru.bogatov.buymetal.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthorizationRequest {

    String email;
    String phone;
    @NotBlank(message = "Поле должно быть заполнено")
    String password;

}
