package ru.bogatov.buymetal.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
public class UpdateUserRequest {

    @NotBlank(message = "Поле обязательно ")
    private String fullName;

    @NotBlank(message = "Поле обязательно")
    private String companyName;

    @NotBlank(message = "Поле обязательно")
    private String companyAddress;

    @NotBlank(message = "Поле обязательно")
    @Pattern(regexp = "^[0-9]*$", message = "ИНН должен состоять из цифр")
    @Size(min = 9, max = 11, message = "Длина ИНН должна быть между 9 и 11 символами")
    private String tin;

    @NotBlank(message = "Поле обязательно")
    @Email(regexp="^(.+)@(.+)$", message="Почта введена неверно")
    private String email;

    @NotBlank(message = "Поле обязательно")
    private String phone;
}
