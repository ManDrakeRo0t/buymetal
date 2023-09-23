package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.UserPosition;

import javax.validation.constraints.*;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Поле обязательно")
    @Email(regexp="^(.+)@(.+)$", message="Почта введена неверно")
    private String email;

    @NotBlank(message = "Поле обязательно")
    @Size(min = 8, max = 40, message = "Длина пароля должна быть между 8 и 40 символами")
    private String password;

    @NotBlank(message = "Поле обязательно")
    private String companyName;

    @NotBlank(message = "Поле обязательно")
    private String companyAddress;

    @NotBlank(message = "Поле обязательно")
    @Pattern(regexp = "^[0-9]*$", message = "ИНН должен состоять из цифр")
    @Size(min = 1, max = 11, message = "Длина ИНН должна быть между 9 и 11 символами")
    private String tin;

    @NotNull(message = "Поле обязательно")
    private UserPosition position;

    @NotBlank(message = "Поле обязательно")
    @Pattern(regexp = "^[0-9]*$", message = "Номер должен состоять из цифр")
    @Size(min = 11, max = 11, message = "Длина номера 11 символов")
    private String phone;

    @NotBlank(message = "Поле обязательно")
    private String fullName;
}
