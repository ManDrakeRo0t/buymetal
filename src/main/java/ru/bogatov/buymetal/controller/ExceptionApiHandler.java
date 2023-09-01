package ru.bogatov.buymetal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bogatov.buymetal.error.ApplicationException;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.repository.ExceptionResponse;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionResponse> applicationException(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ErrorUtils.buildResponse(ex));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> RuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorUtils.buildResponse(ex));
    }


}
