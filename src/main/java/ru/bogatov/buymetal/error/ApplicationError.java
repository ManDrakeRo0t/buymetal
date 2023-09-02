package ru.bogatov.buymetal.error;

import org.springframework.http.HttpStatus;

public enum ApplicationError {
    REQUEST_PARAMS_ERROR("Something went wrong", "BM-0001", HttpStatus.BAD_REQUEST),
    COMMON_ERROR("Something went wrong", "BM-0000", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final String code;
    private final HttpStatus status;

    ApplicationError(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
