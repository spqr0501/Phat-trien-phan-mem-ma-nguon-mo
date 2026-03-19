package com.mycompany.repository;

import com.mycompany.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // Simple repository - no custom queries needed for now
}
