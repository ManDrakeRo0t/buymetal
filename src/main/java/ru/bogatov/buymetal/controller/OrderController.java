package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.entity.Order;
import ru.bogatov.buymetal.model.request.OrderCreationRequest;
import ru.bogatov.buymetal.model.request.OrderSearchRequest;
import ru.bogatov.buymetal.model.request.UpdateOrderStatusRequest;
import ru.bogatov.buymetal.service.OrderService;
import ru.bogatov.buymetal.service.UserService;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(RouteConstants.API_V1 + RouteConstants.ORDER)
public class OrderController {

    OrderService orderService;

    @PreAuthorize("@customSecurityRules.isApplicationOwnerRequest(#body.applicationId)")
    @PostMapping()
    public ResponseEntity<Order> createOrder(@RequestBody @Validated OrderCreationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(body));
    }
    @PreAuthorize("@customSecurityRules.isOrderOwnersRequest(#id)")
    @PostMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestBody @Validated UpdateOrderStatusRequest body) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, body));
    }

    @PreAuthorize("@customSecurityRules.isOrderOwnersRequest(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<Set<Order>> search(@RequestBody @Validated OrderSearchRequest body) {
        return ResponseEntity.ok(orderService.search(body));
    }

}
