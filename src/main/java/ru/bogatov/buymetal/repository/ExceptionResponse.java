package ru.bogatov.buymetal.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponse {
    String code;
    String message;
    String defaultMessage;
}