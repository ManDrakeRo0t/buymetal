package ru.bogatov.buymetal.error;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.bogatov.buymetal.repository.ExceptionResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorUtils {

    //@Value("${error.response.stacktrace}")
    private static boolean isStackTraceEnabled = false;

    public static ApplicationException buildException(ApplicationError error) {
        return ApplicationException.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .defaultMessage(error.getMessage())
                .status(error.getStatus())
                .build();
    }

    public static ApplicationException buildException(ApplicationError error, String message) {
        return ApplicationException.builder()
                .code(error.getCode())
                .message(message)
                .defaultMessage(error.getMessage())
                .status(error.getStatus())
                .build();
    }

    public static ApplicationException buildException(ApplicationError error, Exception initial, String message) {
        ApplicationException applicationException = ApplicationException.builder()
                .code(error.getCode())
                .message(message)
                .defaultMessage(error.getMessage())
                .status(error.getStatus())
                .build();
        applicationException.setStackTrace(initial.getStackTrace());
        return applicationException;
    }

    public static ExceptionResponse buildResponse(ApplicationException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .defaultMessage(ex.getLocalizedMessage()).build();
        return response;
    }

    public static ExceptionResponse buildResponse(RuntimeException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .code(ApplicationError.COMMON_ERROR.getCode())
                .message(ex.getMessage())
                .defaultMessage(ex.getLocalizedMessage()).build();
        return response;
    }

    public static ExceptionResponse buildResponse(MethodArgumentNotValidException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .code(ApplicationError.REQUEST_PARAMS_ERROR.getCode())
                .message(ex.getMessage())
                .defaultMessage(ApplicationError.REQUEST_PARAMS_ERROR.getMessage())
                .errors(getErrorField(ex)).build();
        return response;
    }

    private static List<ExceptionResponse.ErrorField> getErrorField(MethodArgumentNotValidException ex) {
        return ex.getFieldErrors().stream().map(ErrorUtils::springErrorToCustom).collect(Collectors.toList());
    }

    private static ExceptionResponse.ErrorField springErrorToCustom(FieldError fieldError) {
        ExceptionResponse.ErrorField errorField = new ExceptionResponse.ErrorField();
        errorField.setField(fieldError.getField());
        errorField.setMessage(fieldError.getDefaultMessage());
        return errorField;
    }


}
