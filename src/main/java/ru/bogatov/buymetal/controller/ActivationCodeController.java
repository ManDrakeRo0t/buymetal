package ru.bogatov.buymetal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.model.request.VerificationBody;
import ru.bogatov.buymetal.model.response.VerificationResponse;
import ru.bogatov.buymetal.service.VerificationService;

@RestController
@RequestMapping(RouteConstants.API_V1 + RouteConstants.AUTH + RouteConstants.CODE)
public class ActivationCodeController {

    private final VerificationService verificationService;

    public ActivationCodeController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<VerificationResponse> sendActivationCode(@RequestBody VerificationBody body) {
        return ResponseEntity.ok(verificationService.startVerification(body));
    }

    @PostMapping("/confirm")
    public ResponseEntity<VerificationResponse> confirmNumber(@RequestBody VerificationBody body) {
        return ResponseEntity.ok(verificationService.confirmVerification(body));
    }

}
