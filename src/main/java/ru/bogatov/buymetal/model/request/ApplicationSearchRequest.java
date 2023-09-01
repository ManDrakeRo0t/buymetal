package ru.bogatov.buymetal.model.request;

import lombok.Data;
import ru.bogatov.buymetal.model.enums.ApplicationStatus;

import java.util.Set;

@Data
public class ApplicationSearchRequest {

    Set<ApplicationStatus> statuses;

}
