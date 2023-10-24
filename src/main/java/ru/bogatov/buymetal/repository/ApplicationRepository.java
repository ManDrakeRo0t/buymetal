package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.bogatov.buymetal.entity.Application;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Query(nativeQuery = true, value = "select * from application where status in ?1 ")
    Set<Application> searchApplication(List<String> statuses);

    @Query(nativeQuery = true, value = "select * from application where status in ?1 and customer_id = ?2")
    Set<Application> findCustomerApplications(List<String> statuses, UUID customerId);
    @Query(nativeQuery = true, value = "select creation_date from application where customer_id = :customerId and creation_date >= :from and creation_date <= :to")
    List<String> searchCreatedApplicationsForStatistic(UUID customerId, LocalDateTime from, LocalDateTime to);

}
