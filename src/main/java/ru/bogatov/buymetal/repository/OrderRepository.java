package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.bogatov.buymetal.entity.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(nativeQuery = true, value = "select * from orders where customer_id = :id and status in :statuses order by update_date desc limit :limit offset :offset")
    Set<Order> searchCustomerOrders(UUID id, List<String> statuses, int limit, int offset);

    @Query(nativeQuery = true, value = "select * from orders where supplier_id = :id and status in :statuses order by update_date desc limit :limit offset :offset")
    Set<Order> searchSupplierOrders(UUID id, List<String> statuses, int limit, int offset);
    @Query(nativeQuery = true, value = "select * from orders where (customer_id = :id or supplier_id = :id) and status = 'COMPLETED' and complete_date >= :from and complete_date <= :to")
    Set<Order> searchCompetedOrdersForStatistic(UUID id, LocalDate from, LocalDate to);
    @Query(nativeQuery = true, value = "select * from orders where (customer_id = :id or supplier_id = :id) and status = 'REJECTED' and reject_date >= :from and reject_date <= :to")
    Set<Order> searchRejectedOrdersForStatistic(UUID id, LocalDate from, LocalDate to);
    @Query(nativeQuery = true, value = "select * from orders where (customer_id = :id or supplier_id = :id) and creation_time >= :from and creation_time <= :to")
    Set<Order> searchCreatedOrdersForStatistic(UUID id, LocalDate from, LocalDate to);

}
