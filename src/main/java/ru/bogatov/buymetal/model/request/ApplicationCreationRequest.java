package ru.bogatov.buymetal.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationCreationRequest extends ApplicationBaseParams {

    @NotNull(message = "Id пользователя должно быть указано")
    UUID userId;

}
