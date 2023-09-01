package ru.bogatov.buymetal.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.bogatov.buymetal.entity.User;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    User user;
    String refreshToken;
    String accessToken;
}
