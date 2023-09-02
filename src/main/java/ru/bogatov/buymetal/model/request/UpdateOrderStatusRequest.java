package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.OrderStatus;

import java.util.UUID;

@Data
public class UpdateOrderStatusRequest {
    OrderStatus targetStatus;
    UUID initiatorId;
}
