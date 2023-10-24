package ru.bogatov.buymetal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.bogatov.buymetal.entity.Order;
import ru.bogatov.buymetal.model.CustomUserDetails;
import ru.bogatov.buymetal.model.enums.OrderStatus;
import ru.bogatov.buymetal.model.enums.UserPosition;
import ru.bogatov.buymetal.model.response.StatisticContainerResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class StatisticService {

    OrderService orderService;

    ApplicationService applicationService;

    ApplicationResponseService applicationResponseService;

    public StatisticContainerResponse getStatistic(Integer month, Integer year, Integer duration) {
        CustomUserDetails userDetails = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserPosition userPosition = userDetails.getUserPosition();

        LocalDateTime fromTime = LocalDateTime.of(year, month, 1, 0 ,0 ,0);
        LocalDateTime toTime = fromTime.plusMonths(duration);

        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = fromDate.plusMonths(duration);

        log.info("Statistic request : from {}, to {} for : {}", fromTime, toTime, userPosition);

        Map<String, Set<Order>> ordersMap = orderService.getOrdersForStatistic(userDetails.getUserId(), fromDate, toDate);

        List<String> applicationEntity = userPosition == UserPosition.CUSTOMER ?
                applicationService.getApplicationsForStatistic(userDetails.getUserId(), fromTime, toTime) :
                applicationResponseService.getApplicationsForStatistic(userDetails.getUserId(), fromTime, toTime);

        Set<Order> completedOrders = ordersMap.get("completed");
        Set<Order> createdOrders = ordersMap.get("created");
        Set<Order> rejectedOrders = ordersMap.get("rejected");

        StatisticContainerResponse response = new StatisticContainerResponse();

        response.setFrom(fromDate);
        response.setTo(toDate.minusDays(1));

        response.setCompletedOrdersAmount(completedOrders.size());
        response.setRejectedOrdersAmount(rejectedOrders.size());
        response.setCreatedOrdersAmount(createdOrders.size());
        response.setTotalMoneyAmount(createdOrders.stream()
                .filter(order -> order.getStatus() != OrderStatus.REJECTED)
                .map(order -> order.getResponse().getFullPrice())
                .mapToDouble(Double::doubleValue).sum());

        if (UserPosition.CUSTOMER == userPosition) {
            response.setTotalApplicationAmount(applicationEntity.size());
        } else {
            response.setTotalApplicationResponseAmount(applicationEntity.size());
        }

        List<StatisticContainerResponse.DetailedStatisticEntry> detailedStatisticEntries = new ArrayList<>();

        while (fromDate.isBefore(toDate)) {

            StatisticContainerResponse.DetailedStatisticEntry entry = new StatisticContainerResponse.DetailedStatisticEntry();

            entry.setDate(fromDate);
            LocalDate finalFromDate = fromDate;
            entry.setCompletedOrders(
                    completedOrders.stream().filter(order -> !order.getCompleteDate().isBefore(finalFromDate) && order.getCompleteDate().isBefore(finalFromDate.plusDays(1))).count()
            );
            entry.setRejectedOrders(
                    rejectedOrders.stream().filter(order -> !order.getRejectDate().isBefore(finalFromDate) && order.getRejectDate().isBefore(finalFromDate.plusDays(1))).count()
            );
            entry.setCreatedOrders(
                    createdOrders.stream().filter(order -> !order.getCreationTime().isBefore(finalFromDate) && order.getCreationTime().isBefore(finalFromDate.plusDays(1))).count()
            );

            if (UserPosition.CUSTOMER == userPosition) {
                entry.setCreatedApplications(
                        applicationEntity.stream()
                                .map(raw -> LocalDate.parse(raw.substring(0,10)))
                                .filter(dateTime -> !dateTime.isBefore(finalFromDate) && dateTime.isBefore(finalFromDate.plusDays(1))).count()
                );
            } else {
                entry.setCreatedApplicationResponeses(
                        applicationEntity.stream()
                                .map(raw -> LocalDate.parse(raw.substring(0,10)))
                                .filter(dateTime -> !dateTime.isBefore(finalFromDate) && dateTime.isBefore(finalFromDate.plusDays(1))).count()
                );
            }
            fromDate = fromDate.plusDays(1);

            detailedStatisticEntries.add(entry);
        }

        response.setDetailedStatistic(detailedStatisticEntries);

        return response;
    }

}
