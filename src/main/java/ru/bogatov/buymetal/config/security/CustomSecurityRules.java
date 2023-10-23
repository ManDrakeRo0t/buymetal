package ru.bogatov.buymetal.config.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.bogatov.buymetal.entity.Order;
import ru.bogatov.buymetal.model.CustomUserDetails;
import ru.bogatov.buymetal.service.ApplicationService;
import ru.bogatov.buymetal.service.OrderService;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CustomSecurityRules {

    private final ApplicationService applicationService;

    private final OrderService orderService;

    public boolean isUserRequest(UUID userId) {
        UUID idFromToken = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        return idFromToken.equals(userId);
    }

    public boolean isApplicationOwnerRequest(UUID applicationId) {
        UUID idFromToken = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        return idFromToken.equals(applicationService.findById(applicationId).getCustomer().getId());
    }

    public boolean isOrderOwnersRequest(UUID orderId) {
        UUID idFromToken = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        Order order = orderService.findById(orderId);
        return idFromToken.equals(order.getCustomerId()) || idFromToken.equals(order.getSupplierId());
    }

}
