package ru.bogatov.buymetal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.bogatov.buymetal.model.enums.MetalForm;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
@Getter
@Setter
@MappedSuperclass
public class ApplicationBaseParams {

    @Enumerated(EnumType.STRING)
    private MetalForm rolledForm;

    private String rolledType;

    private String rolledSize;

    private String rolledParams;

    private String rolledGost;

    private String materialBrand;

    private String materialParams;

    private String materialGost;

    private double amount;

    private LocalDateTime creationDate;
}
