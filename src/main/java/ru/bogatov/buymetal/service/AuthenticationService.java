package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.bogatov.buymetal.config.security.JwtProvider;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.request.AuthorizationRequest;
import ru.bogatov.buymetal.model.request.LoginForm;
import ru.bogatov.buymetal.model.request.RegistrationRequest;
import ru.bogatov.buymetal.model.response.AuthenticationResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;

    private final JwtProvider jwtProvider;

    private final VerificationService verificationService;

    public AuthenticationResponse registerUser(RegistrationRequest body) {
        User user = userService.createUser(body);
        verificationService.deleteRecord(user.getPhone());
        Pair<String, String> tokens = getTokens(user);
        return AuthenticationResponse.builder()
                .user(user)
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }

    public AuthenticationResponse login(AuthorizationRequest body) {
        User user = null;
        if (body.getEmail() != null && !body.getEmail().isBlank()) {
            user = userService.findUserByEmailAndPassword(body);
        } else if (body.getPhone() != null && !body.getPhone().isBlank()){
            user = userService.findUserByPhoneAndPassword(body);
        } else {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Номер телефона или почта обязательны");
        }
        Pair<String, String> tokens = getTokens(user);
        return AuthenticationResponse.builder()
                .user(user)
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }

    public AuthenticationResponse refreshTokenPair(String token) {
        Pair<String, String> tokens = refreshTokens(token);
        return AuthenticationResponse.builder()
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }


    public Pair<String, String> getTokens(User user) {
        try {
            String refresh = jwtProvider.generateRefreshForUser(user);
            String token = jwtProvider.generateTokenForUser(user);
            return Pair.of(token, refresh);
        } catch (Exception ex) {
            throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR, ex , "Ошибка генерации токенов");
        }
    }

    public Pair<String, String> refreshTokens(String token) {

        if (!jwtProvider.validateToken(token)) {
            log.warn("not able to validate refresh token : "  + token);
            throw ErrorUtils.buildException(ApplicationError.AUTH_ERROR, "Токен устарел");
        }
        String userId = jwtProvider.getUserIdFromToken(token);
        User user = userService.findById(UUID.fromString(userId));
        if (user.getRefresh() != null && token.equals(user.getRefresh())) {
            String refresh = jwtProvider.generateRefreshForUser(user);
            String accessToken = jwtProvider.generateTokenForUser(user);
            return Pair.of(accessToken, refresh);
        }
        throw ErrorUtils.buildException(ApplicationError.AUTH_ERROR, "Токены не совпадают");
    }

    public AuthenticationResponse resetPassword(LoginForm body) {
        User user = userService.findUserAndResetPassword(body);
        Pair<String, String> tokens = getTokens(user);
        return AuthenticationResponse.builder()
                .user(user)
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }

}
