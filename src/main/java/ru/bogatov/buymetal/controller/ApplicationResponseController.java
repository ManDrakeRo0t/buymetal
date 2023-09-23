package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.entity.ApplicationResponse;
import ru.bogatov.buymetal.model.request.ApplicationResponseCreationRequest;
import ru.bogatov.buymetal.service.ApplicationResponseService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(RouteConstants.API_V1 + RouteConstants.APPLICATION_RESPONSE)
@AllArgsConstructor
public class ApplicationResponseController {

    private final ApplicationResponseService applicationResponseService;
    @PostMapping()
    public ResponseEntity<ApplicationResponse> create(@RequestBody @Validated ApplicationResponseCreationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationResponseService.createApplicationResponse(body));
    }

    @GetMapping("/supplier/{id}")
    public ResponseEntity<Set<ApplicationResponse>> getSupplierResponses(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationResponseService.getSupplierResponses(id));
    }

}
