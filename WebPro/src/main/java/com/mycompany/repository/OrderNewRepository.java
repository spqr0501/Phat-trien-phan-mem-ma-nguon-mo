package com.mycompany.repository;

import com.mycompany.models.OrderNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderNewRepository extends JpaRepository<OrderNew, Integer> {

    // Find orders by user ID
    List<OrderNew> findByUserIdOrderByCreatedDateDesc(String userId);

    // Find orders by status
    List<OrderNew> findByStatusOrderByCreatedDateDesc(String status);

    // Count orders by user
    Long countByUserId(String userId);

    // Count orders by status
    Long countByStatus(String status);

    // Get total revenue
    @Query("SELECT SUM(o.totalAmount) FROM OrderNew o WHERE o.status = 'Completed'")
    Double getTotalRevenue();

    // Get revenue by user
    @Query("SELECT SUM(o.totalAmount) FROM OrderNew o WHERE o.userId = ?1 AND o.status = 'Completed'")
    Double getTotalRevenueByUser(String userId);
}
