package ru.bogatov.buymetal.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginForm {
    @NotBlank(message = "Поле обязательно")
    @Pattern(regexp = "^[0-9]*$", message = "Номер должен состоять из цифр")
    @Size(min = 11, max = 11, message = "Длина номера 11 символов")
    private String phoneNumber;
    @NotBlank(message = "Поле обязательно")
    @Size(min = 8, max = 40, message = "Длина пароля должна быть между 8 и 40 символами")
    private String password;
}
