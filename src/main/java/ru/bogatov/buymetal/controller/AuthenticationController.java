package ru.bogatov.buymetal.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.model.request.AuthorizationRequest;
import ru.bogatov.buymetal.model.request.LoginForm;
import ru.bogatov.buymetal.model.request.RegistrationRequest;
import ru.bogatov.buymetal.model.response.AuthenticationResponse;
import ru.bogatov.buymetal.repository.ExceptionResponse;
import ru.bogatov.buymetal.service.AuthenticationService;

@RestController
@RequestMapping(RouteConstants.API_V1 + RouteConstants.AUTH)
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @ApiOperation(value = "Регистрация нового пользователя")
    @ApiResponses(
        value = {
                @ApiResponse(code = 201, message = "Успешная регистрация", response = AuthenticationResponse.class),
                @ApiResponse(code = 400, message = "Валидационная ошибка", response = ExceptionResponse.class)
        }
    )
    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> registerNewUser(@RequestBody @Validated RegistrationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(body));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Validated AuthorizationRequest body) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.login(body));
    }

    @PostMapping("/refresh/{refreshToken}")
    public ResponseEntity<AuthenticationResponse> refreshToken(@PathVariable String refreshToken){
        return ResponseEntity.ok(authenticationService.refreshTokenPair(refreshToken));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<AuthenticationResponse> resetPassword(@RequestBody @Validated LoginForm resetForm) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationService.resetPassword(resetForm));
    }


}
