package com.mycompany.repository;

import com.mycompany.models.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemEntityRepository extends JpaRepository<CartItemEntity, Integer> {

    // Find all cart items for a user
    List<CartItemEntity> findByUserIdOrderByAddedDateDesc(String userId);

    // Find specific cart item
    Optional<CartItemEntity> findByUserIdAndProductId(String userId, String productId);

    // Check if item exists in cart
    boolean existsByUserIdAndProductId(String userId, String productId);

    // Delete specific cart item
    @Transactional
    void deleteByUserIdAndProductId(String userId, String productId);

    // Delete all cart items for user
    @Transactional
    void deleteByUserId(String userId);

    // Count cart items for user
    Long countByUserId(String userId);
}
