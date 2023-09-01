package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.entity.Application;
import ru.bogatov.buymetal.entity.ApplicationResponse;
import ru.bogatov.buymetal.model.request.ApplicationCreationRequest;
import ru.bogatov.buymetal.model.request.ApplicationSearchRequest;
import ru.bogatov.buymetal.service.ApplicationResponseService;
import ru.bogatov.buymetal.service.ApplicationService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(RouteConstants.API_V1 + RouteConstants.APPLICATION)
@AllArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    private final ApplicationResponseService applicationResponseService;

    @PostMapping()
    public ResponseEntity<Application> createApplication(@RequestBody ApplicationCreationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(body));
    }

    @PostMapping("/search")
    public ResponseEntity<Set<Application>> search(@RequestBody ApplicationSearchRequest body) {
        return ResponseEntity.ok(applicationService.search(body));
    }

    @GetMapping("/{id}/responses")
    public ResponseEntity<Set<ApplicationResponse>> getResponsesByApplicationId(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationResponseService.getApplicationResponseByApplicationId(id));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Set<Application>> getCustomerApplications(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getCustomerApplications(id));
    }

}
