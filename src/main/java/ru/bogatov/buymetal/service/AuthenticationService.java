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

    public AuthenticationResponse registerUser(RegistrationRequest body) {
        User user = userService.createUser(body);
        Pair<String, String> tokens = getTokens(user);
        return AuthenticationResponse.builder()
                .user(user)
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }

    public AuthenticationResponse login(AuthorizationRequest body) {
        User user = userService.findUserByEmailAndPassword(body);
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
            throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR, ex ,"Error during token generation");
        }
    }

    public Pair<String, String> refreshTokens(String token) {

        if (!jwtProvider.validateToken(token)) {
            log.warn("not able to validate refresh token : "  + token);
            throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR, "Not able to validate refresh token");
        }
        String userId = jwtProvider.getUserIdFromToken(token);
        User user = userService.findById(UUID.fromString(userId));
        if (user.getRefresh() != null && token.equals(user.getRefresh())) {
            String refresh = jwtProvider.generateRefreshForUser(user);
            String accessToken = jwtProvider.generateTokenForUser(user);
            return Pair.of(accessToken, refresh);
        }
        throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR, "Refresh tokens not same");
    }

}
