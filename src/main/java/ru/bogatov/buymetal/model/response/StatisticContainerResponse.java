package ru.bogatov.buymetal.model.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StatisticContainerResponse {
    LocalDate from;
    LocalDate to;
    Double totalMoneyAmount;
    Integer createdOrdersAmount;
    Integer completedOrdersAmount;
    Integer rejectedOrdersAmount;
    Integer totalApplicationAmount;
    Integer totalApplicationResponseAmount;
    List<DetailedStatisticEntry> detailedStatistic;
    @Data
    public static class DetailedStatisticEntry {
        LocalDate date;
        long createdOrders;
        long createdApplications;
        long createdApplicationResponeses;
        long rejectedOrders;
        long completedOrders;
    }
}
