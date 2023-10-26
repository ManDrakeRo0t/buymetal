package ru.bogatov.buymetal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bogatov.buymetal.constant.RouteConstants;
import ru.bogatov.buymetal.model.response.StatisticContainerResponse;
import ru.bogatov.buymetal.service.StatisticService;


@RestController
@AllArgsConstructor
@RequestMapping(RouteConstants.API_V1 + RouteConstants.STATISTIC)
public class StaticsticController {

    StatisticService statisticService;
    @GetMapping("")
    public ResponseEntity<StatisticContainerResponse> getStatistic(
                                                                 @RequestParam Integer month,
                                                                 @RequestParam Integer year,
                                                                 @RequestParam(required = false ,defaultValue = "1") Integer duration) {
        return ResponseEntity.ok(statisticService.getStatistic(month, year, duration));
    }

}
