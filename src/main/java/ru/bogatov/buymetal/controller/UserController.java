package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.entity.Payment;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.model.request.UpdateUserRequest;
import ru.bogatov.buymetal.service.PaymentService;
import ru.bogatov.buymetal.service.UserService;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(RouteConstants.API_V1 + RouteConstants.USER)
public class UserController {

    private final UserService userService;

    private final PaymentService paymentService;

    @PreAuthorize("@customSecurityRules.isUserRequest(#id)")
    @GetMapping("/{id}/payments")
    public ResponseEntity<Set<Payment>> getUserPayments(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.findAllCustomerPayments(id));
    }
    @PreAuthorize("@customSecurityRules.isUserRequest(#id)")
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @PreAuthorize("@customSecurityRules.isUserRequest(#id)")
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody @Validated UpdateUserRequest body) {
        return ResponseEntity.ok(userService.updateUser(id, body));
    }
}
