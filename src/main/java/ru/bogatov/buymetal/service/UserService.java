package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.request.AuthorizationRequest;
import ru.bogatov.buymetal.model.request.LoginForm;
import ru.bogatov.buymetal.model.request.RegistrationRequest;
import ru.bogatov.buymetal.model.request.UpdateUserRequest;
import ru.bogatov.buymetal.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    VerificationService verificationService;

    public User createUser(RegistrationRequest body) {
        if (userRepository.isUserExistWithMail(body.getEmail()).isPresent()) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Почта занята");
        }
        if (userRepository.isUserExistWithPhoneNumber(body.getPhone()).isPresent()) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Телефон занят");
        }
        if (!verificationService.isVerified(body.getPhone())) {
            throw ErrorUtils.buildException(ApplicationError.BUSINESS_LOGIC_ERROR, "Телефон не подтвержден, или подтверждение просрочилось");
        }
        User user = new User();
        user.setEmail(body.getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user.setPhone(body.getPhone());
        user.setTin(body.getTin());
        user.setCompanyName(body.getCompanyName());
        user.setBlocked(false);
        user.setMailConfirmed(false);
        user.setPhoneConfirmed(true);
        user.setPosition(body.getPosition());
        user.setFullName(body.getFullName());
        user.setCompanyAddress(body.getCompanyAddress());
        user.setRegistrationDate(LocalDate.now());
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw ErrorUtils.buildException(ApplicationError.COMMON_ERROR, e.getLocalizedMessage());
        }
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR, "Пользователь не найден"));
    }

    public User updateUser(UUID id, UpdateUserRequest updateUserRequest) {
        User old = findById(id);
        if (!old.getFullName().equals(updateUserRequest.getFullName())) {
            old.setFullName(updateUserRequest.getFullName());
        }
        if (!old.getCompanyName().equals(updateUserRequest.getCompanyName())) {
            old.setCompanyName(updateUserRequest.getCompanyName());
        }
        if (!old.getCompanyAddress().equals(updateUserRequest.getCompanyAddress())) {
            old.setCompanyAddress(updateUserRequest.getCompanyAddress());
        }
        if (!old.getTin().equals(updateUserRequest.getTin())) {
            old.setTin(updateUserRequest.getTin());
        }
        if (!old.getPhone().equals(updateUserRequest.getPhone())) {
            if (userRepository.findByPhone(updateUserRequest.getPhone()).isPresent()) {
                throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Номер занят другим пользователем");
            }
            old.setPhone(updateUserRequest.getPhone());
            old.setPhoneConfirmed(false);
        }
        if (!old.getEmail().equals(updateUserRequest.getEmail())) {
            if (userRepository.findByEmail(updateUserRequest.getEmail()).isPresent()) {
                throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Почта занята другим пользователем");
            }
            old.setEmail(updateUserRequest.getEmail());
            old.setMailConfirmed(false);
        }
        return userRepository.save(old);
    }

    public void updateRefresh(UUID id, String refresh) {
        userRepository.updateRefreshToken(id, refresh);
    }

    public User findUserByEmailAndPassword(AuthorizationRequest body) {
        User user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR, "Пользователь не найден"));
        if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Логин или пароль неверны");
        }
        return user;
    }

    public User findUserByPhoneAndPassword(AuthorizationRequest body) {
        User user = userRepository.findByPhone(body.getPhone())
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR, "Пользователь не найден"));
        if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Логин или пароль неверны");
        }
        return user;
    }

    public Void blockUser(UUID id) {
        User user = findById(id);
        user.setBlocked(true);
        userRepository.save(user);
        return null;
    }

    @Transactional
    public User findUserAndResetPassword(LoginForm loginForm) {
        if (!verificationService.isVerified(loginForm.getPhoneNumber())) {
            throw ErrorUtils.buildException(ApplicationError.BUSINESS_LOGIC_ERROR, "Verification not found");
        }
        User user = userRepository.findByPhone(loginForm.getPhoneNumber())
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR,  "Пользователь не найден"));
        checkUserStatus(user);
        if (this.passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Старый пароль совпадает с новым");
        }
        user.setPassword(this.passwordEncoder.encode(loginForm.getPassword()));
        verificationService.deleteRecord(loginForm.getPhoneNumber());
        return userRepository.save(user);
    }

    private void checkUserStatus(User user) {
        if (user.isBlocked()) {
            throw ErrorUtils.buildException(ApplicationError.BUSINESS_LOGIC_ERROR, "Пользователь заблокирован");
        }
    }
}
