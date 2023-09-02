package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.bogatov.buymetal.entity.Order;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(nativeQuery = true, value = "select * from orders where customer_id = :id and status in :statuses order by update_date desc limit :limit offset :offset")
    Set<Order> searchCustomerOrders(UUID id, List<String> statuses, int limit, int offset);

    @Query(nativeQuery = true, value = "select * from orders where supplier_id = :id and status in :statuses order by update_date desc limit :limit offset :offset")
    Set<Order> searchSupplierOrders(UUID id, List<String> statuses, int limit, int offset);

}
