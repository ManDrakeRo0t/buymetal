package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.model.request.VerificationBody;
import ru.bogatov.buymetal.model.response.VerificationResponse;
import ru.bogatov.buymetal.service.DocumentGenerationService;
import ru.bogatov.buymetal.service.VerificationService;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(RouteConstants.API_V1 + RouteConstants.AUTH + RouteConstants.CODE)
public class ActivationCodeController {

    private final VerificationService verificationService;

    private final DocumentGenerationService documentGenerationService;


    @PostMapping("/send")
    public ResponseEntity<VerificationResponse> sendActivationCode(@RequestBody VerificationBody body) {
        return ResponseEntity.ok(verificationService.startVerification(body));
    }

    @PostMapping("/confirm")
    public ResponseEntity<VerificationResponse> confirmNumber(@RequestBody VerificationBody body) {
        return ResponseEntity.ok(verificationService.confirmVerification(body));
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<Void> testDoc(@PathVariable UUID id) {
        return ResponseEntity.ok(documentGenerationService.generateDocument(id));
    }

}
