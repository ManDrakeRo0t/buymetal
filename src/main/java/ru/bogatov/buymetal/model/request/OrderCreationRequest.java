package ru.bogatov.buymetal.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class OrderCreationRequest {

    @NotNull(message = "Заявка не должна быть пустой")
    private UUID applicationId;

    @NotNull(message = "Ответ на заявку должен быть пустым")
    private UUID applicationResponseId;

}
