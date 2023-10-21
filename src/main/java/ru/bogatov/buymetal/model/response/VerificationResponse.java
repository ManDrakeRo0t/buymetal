package ru.bogatov.buymetal.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationResponse {

    private String step;
    private boolean isSuccess;
    private String message;

}
