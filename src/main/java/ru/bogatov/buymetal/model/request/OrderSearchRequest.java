package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.OrderStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;
import java.util.UUID;

@Data
public class OrderSearchRequest {

    UUID customerId;
    UUID supplierId;
    @NotNull(message = "Поле должно быть заполнено")
    Set<OrderStatus> statuses;
    @Positive(message = "Должно быть больше 0")
    int limit;
    @Positive(message = "Должно быть больше 0")
    int offset;

}
