package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.ApplicationStatus;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class ApplicationSearchRequest {

    @NotNull(message = "Поле должно быть заполнено")
    Set<ApplicationStatus> statuses;

}
