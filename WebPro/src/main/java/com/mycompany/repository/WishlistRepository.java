package com.mycompany.repository;

import com.mycompany.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    // Find all wishlist items for a user
    List<Wishlist> findByUserIdOrderByAddedDateDesc(String userId);

    // Find specific wishlist item
    Optional<Wishlist> findByUserIdAndProductId(String userId, String productId);

    // Check if product is in user's wishlist
    boolean existsByUserIdAndProductId(String userId, String productId);

    // Delete from wishlist
    void deleteByUserIdAndProductId(String userId, String productId);

    // Count wishlist items for user
    Long countByUserId(String userId);
}
