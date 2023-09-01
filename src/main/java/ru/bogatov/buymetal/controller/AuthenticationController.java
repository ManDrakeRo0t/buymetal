package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.model.request.AuthorizationRequest;
import ru.bogatov.buymetal.model.request.RegistrationRequest;
import ru.bogatov.buymetal.model.response.AuthenticationResponse;
import ru.bogatov.buymetal.service.AuthenticationService;

@RestController
@RequestMapping(RouteConstants.API_V1 + RouteConstants.AUTH)
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> registerNewUser(@RequestBody RegistrationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(body));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthorizationRequest body) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.login(body));
    }

    @PostMapping("/refresh/{refreshToken}")
    public ResponseEntity<AuthenticationResponse> refreshToken(@PathVariable String refreshToken){
        return ResponseEntity.ok(authenticationService.refreshTokenPair(refreshToken));
    }


}
