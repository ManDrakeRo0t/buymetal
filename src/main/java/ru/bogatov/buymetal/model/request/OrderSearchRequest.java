package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.OrderStatus;

import java.util.Set;
import java.util.UUID;

@Data
public class OrderSearchRequest {

    UUID customerId;
    UUID supplierId;
    Set<OrderStatus> statuses;
    int limit;
    int offset;

}
