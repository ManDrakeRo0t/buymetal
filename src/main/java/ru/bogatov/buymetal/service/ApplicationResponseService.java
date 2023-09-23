package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bogatov.buymetal.entity.Application;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;
import ru.bogatov.buymetal.entity.ApplicationResponse;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.model.enums.UserPosition;
import ru.bogatov.buymetal.model.request.ApplicationResponseCreationRequest;
import ru.bogatov.buymetal.repository.ApplicationResponseRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ApplicationResponseService {

    private final ApplicationResponseRepository applicationResponseRepository;

    private final UserService userService;

    private final ApplicationService applicationService;

    public ApplicationResponse createApplicationResponse(ApplicationResponseCreationRequest body) {
        User user = userService.findById(body.getUserID());
        Application application = applicationService.findById(body.getApplicationId());

        if (user.getPosition() == UserPosition.CUSTOMER) {
            throw ErrorUtils.buildException(ApplicationError.REQUEST_PARAMS_ERROR, "Пользователь должен быть поставщиком");
        }

        ApplicationResponse applicationResponse = fillBaseParams(body);
        applicationResponse.setPrice(body.getPrice());
        applicationResponse.setFullPrice(body.getFullPrice());
        applicationResponse.setSimilar(body.isSimilar());
        applicationResponse.setInStock(body.isInStock());
        applicationResponse.setCreationDate(LocalDateTime.now());
        applicationResponse.setDeliverDate(body.getDeliverDate());
        applicationResponse.setSupplier(user);
        applicationResponse.setApplication(application);
        applicationResponse = applicationResponseRepository.save(applicationResponse);


        if (application.getApplicationResponses() == null || application.getApplicationResponses().isEmpty()){
            Set<ApplicationResponse> responses = new HashSet<>();
            responses.add(applicationResponse);
            application.setApplicationResponses(responses);
        } else {
            application.getApplicationResponses().add(applicationResponse);
        }
        applicationService.save(application);
        return applicationResponse;
    }

    public ApplicationResponse fillBaseParams(ApplicationBaseParams body) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setAmount(body.getAmount());
        applicationResponse.setMaterialParams(body.getMaterialParams());
        applicationResponse.setMaterialGost(body.getMaterialGost());
        applicationResponse.setMaterialBrand(body.getMaterialBrand());
        applicationResponse.setRolledType(body.getRolledType());
        applicationResponse.setRolledSize(body.getRolledSize());
        applicationResponse.setRolledGost(body.getRolledGost());
        applicationResponse.setRolledForm(body.getRolledForm());
        applicationResponse.setRolledParams(body.getRolledParams());
        return applicationResponse;
    }

    public ApplicationResponse findById(UUID id) {
        return applicationResponseRepository.findById(id)
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.NOT_FOUND_ERROR, "Ответ на заявку не найден"));
    }

    public Set<ApplicationResponse> getApplicationResponseByApplicationId(UUID id) {
        return applicationResponseRepository.findAllByApplicationId(id);
    }

    public Set<ApplicationResponse> getSupplierResponses(UUID id) {
        return applicationResponseRepository.findAllBySupplierId(id);
    }

    public void deleteApplicationById(UUID id) {
        applicationResponseRepository.deleteById(id);
    }
}
