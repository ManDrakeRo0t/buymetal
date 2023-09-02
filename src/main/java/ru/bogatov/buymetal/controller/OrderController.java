package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.entity.Order;
import ru.bogatov.buymetal.model.request.OrderCreationRequest;
import ru.bogatov.buymetal.model.request.OrderSearchRequest;
import ru.bogatov.buymetal.model.request.UpdateOrderStatusRequest;
import ru.bogatov.buymetal.service.OrderService;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(RouteConstants.API_V1 + RouteConstants.ORDER)
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(body));
    }

    @PostMapping("/{id}/status")
    private ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest body) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, body));
    }
    @GetMapping("/{id}")
    private ResponseEntity<Order> findOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
    @PostMapping("/search")
    private ResponseEntity<Set<Order>> search(@RequestBody OrderSearchRequest body) {
        return ResponseEntity.ok(orderService.search(body));
    }

}
