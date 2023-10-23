package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.OrderStatus;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Статус должен быть указан")
    OrderStatus targetStatus;

}
