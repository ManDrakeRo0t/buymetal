package ru.bogatov.buymetal.model.request;

import lombok.Data;

@Data
public class AuthorizationRequest {

    String email;
    String password;

}
