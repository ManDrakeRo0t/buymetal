package ru.bogatov.buymetal.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;

import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationResponseCreationRequest extends ApplicationBaseParams {

    private Date deliverDate;

    private boolean inStock;

    private boolean isSimilar;

    private double price;

    private double fullPrice;

    UUID userID;

    UUID applicationId;

}
