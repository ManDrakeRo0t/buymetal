package ru.bogatov.buymetal.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.model.enums.Role;
import ru.bogatov.buymetal.model.enums.UserPosition;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CustomUserDetails implements UserDetails {

    private String password;
    private String email;
    private boolean isActive;
    private Set<Role> roleSet;
    private boolean isBlocked;
    private UUID userId;
    private UserPosition userPosition;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleSet;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails fromUserToUserDetails(User user) {
        return CustomUserDetails.builder()
                .password(user.getPassword())
                .email(user.getEmail())
                .roleSet(Set.of(Role.USER))
                .userId(user.getId())
                .userPosition(user.getPosition())
                .build();
    }
}
