package ru.bogatov.buymetal.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationResponseCreationRequest extends ApplicationBaseParams {

    @FutureOrPresent(message = "Дата не должна быть в прошлом")
    private Date deliverDate;

    private boolean inStock;

    private boolean isSimilar;

    @Positive(message = "Поле должно быть положительным")
    private double price;

    private double fullPrice;

    @NotNull(message = "Поле должно быть заполнено")
    UUID userID;

    @NotNull(message = "Поле должно быть заполнено")
    UUID applicationId;

}
