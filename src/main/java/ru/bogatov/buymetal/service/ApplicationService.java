package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bogatov.buymetal.entity.Application;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.enums.ApplicationStatus;
import ru.bogatov.buymetal.model.enums.UserPosition;
import ru.bogatov.buymetal.model.request.ApplicationCreationRequest;
import ru.bogatov.buymetal.model.request.ApplicationSearchRequest;
import ru.bogatov.buymetal.repository.ApplicationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final UserService userService;
    public Application createApplication(ApplicationCreationRequest body) {
        User user = userService.findById(body.getUserId());

        if (user.getPosition() == UserPosition.SUPPLIER) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Пользователь должен быть заказчиком");
        }

        Application application = fillBaseParams(body);
        application.setStatus(ApplicationStatus.OPEN);
        application.setCreationDate(LocalDateTime.now());
        application.setCustomer(user);
        return save(application);
    }

    public Application fillBaseParams(ApplicationBaseParams body) {
        Application application = new Application();
        application.setAmount(body.getAmount());
        application.setMaterialParams(body.getMaterialParams());
        application.setMaterialGost(body.getMaterialGost());
        application.setMaterialBrand(body.getMaterialBrand());
        application.setRolledType(body.getRolledType());
        application.setRolledSize(body.getRolledSize());
        application.setRolledGost(body.getRolledGost());
        application.setRolledForm(body.getRolledForm());
        application.setRolledParams(body.getRolledParams());
        return application;
    }

    public Set<Application> search(ApplicationSearchRequest body) {
        List<String> statuses = body.getStatuses().stream().map(ApplicationStatus::getValue).collect(Collectors.toList());
        return applicationRepository.searchApplication(statuses);
    }

    public Application findById(UUID id) {
        return applicationRepository.findById(id).orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR, "Заявка не найдена"));
    }

    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    public Set<Application> getCustomerApplications(UUID customerId) {
        List<String> statuses = Set.of(ApplicationStatus.OPEN, ApplicationStatus.RESERVED)
                .stream().map(ApplicationStatus::getValue)
                .collect(Collectors.toList());
        return applicationRepository.findCustomerApplications(statuses, customerId);
    }

    public List<String> getApplicationsForStatistic(UUID userId, LocalDateTime from, LocalDateTime to) {
        return applicationRepository.searchCreatedApplicationsForStatistic(userId, from, to);
    }
}
