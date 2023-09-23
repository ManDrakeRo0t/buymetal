package ru.bogatov.buymetal.repository;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExceptionResponse {
    String code;
    String message;
    String defaultMessage;
    List<ErrorField> errors;

    @Data
    public static class ErrorField {
        String field;
        String message;
    }
}