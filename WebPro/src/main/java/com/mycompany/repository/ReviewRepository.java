package com.mycompany.repository;

import com.mycompany.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Find all reviews for a specific product
    List<Review> findByProductIdOrderByCreatedDateDesc(String productId);

    // Find reviews by user
    List<Review> findByUserIdOrderByCreatedDateDesc(String userId);

    // Count reviews for a product
    Long countByProductId(String productId);

    // Calculate average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = ?1")
    Double getAverageRatingByProductId(String productId);

    // Check if user has reviewed a product
    boolean existsByProductIdAndUserId(String productId, String userId);
}
