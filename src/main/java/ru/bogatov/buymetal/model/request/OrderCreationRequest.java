package ru.bogatov.buymetal.model.request;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderCreationRequest {

    private UUID applicationId;

    private UUID applicationResponseId;

}
