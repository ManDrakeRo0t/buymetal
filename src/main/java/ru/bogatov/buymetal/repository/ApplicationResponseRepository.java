package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bogatov.buymetal.entity.ApplicationBaseParams;
import ru.bogatov.buymetal.entity.ApplicationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ApplicationResponseRepository extends JpaRepository<ApplicationResponse, UUID> {
    @Query(nativeQuery = true, value = "select * from application_response where application_id = :id")
    Set<ApplicationResponse> findAllByApplicationId(@Param("id") UUID id);
    @Query(nativeQuery = true, value = "select * from application_response where supplier_id = :id")
    Set<ApplicationResponse> findAllBySupplierId(@Param("id") UUID id);

    @Query(nativeQuery = true, value = "select creation_date from application_response where supplier_id = :customerId and creation_date >= :from and creation_date <= :to")
    List<String> searchCreatedApplicationResponsesForStatistic(UUID customerId, LocalDateTime from, LocalDateTime to);

}
