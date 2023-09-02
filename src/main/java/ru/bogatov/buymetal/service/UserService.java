package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.request.AuthorizationRequest;
import ru.bogatov.buymetal.model.request.RegistrationRequest;
import ru.bogatov.buymetal.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    public User createUser(RegistrationRequest body) {
        User user = new User();
        user.setEmail(body.getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user.setPhone(body.getPhone());
        user.setTin(body.getTin());
        user.setCompanyName(body.getCompanyName());
        user.setBlocked(false);
        user.setMailConfirmed(false);
        user.setPosition(body.getPosition());
        user.setFullName(body.getFullName());
        user.setCompanyAddress(body.getCompanyAddress());
        user.setRegistrationDate(LocalDate.now());
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR);
        }
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> ErrorUtils.buildException(ApplicationError.COMMON_ERROR));
    }

    public void updateRefresh(UUID id, String refresh) {
        userRepository.updateRefreshToken(id, refresh);
    }

    public User findUserByEmailAndPassword(AuthorizationRequest body) {
        User user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.COMMON_ERROR));
        if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR);
        }
        return user;
    }

    public Void blockUser(UUID id) {
        User user = findById(id);
        user.setBlocked(true);
        userRepository.save(user);
        return null;
    }
}
