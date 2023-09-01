package ru.bogatov.buymetal.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.model.CustomUserDetails;

import java.util.UUID;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            User user = userService.findById(UUID.fromString(userId));
            return CustomUserDetails.fromUserToUserDetails(user);
        }catch (Exception e) {
            return  null;
        }

    }
}
