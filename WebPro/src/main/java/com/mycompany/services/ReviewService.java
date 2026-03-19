package com.mycompany.services;

import com.mycompany.models.Review;
import com.mycompany.models.Product;
import com.mycompany.repository.ReviewRepository;
import com.mycompany.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all reviews for a product
    public List<Review> getProductReviews(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedDateDesc(productId);
    }

    // Get reviews by user
    public List<Review> getUserReviews(String userId) {
        return reviewRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    // Add a review
    @Transactional
    public Review addReview(String productId, String userId, Integer rating, String comment) {
        // Check if user already reviewed
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi!");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Đánh giá phải từ 1-5 sao!");
        }

        Review review = new Review(productId, userId, rating, comment);
        Review saved = reviewRepository.save(review);

        // Update product average rating
        updateProductRating(productId);

        return saved;
    }

    // Update product's average rating
    @Transactional
    public void updateProductRating(String productId) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setAverageRating(avgRating != null ? avgRating : 0.0);
            product.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
            productRepository.save(product);
        }
    }

    // Delete review
    @Transactional
    public void deleteReview(Integer reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá!"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa đánh giá này!");
        }

        String productId = review.getProductId();
        reviewRepository.delete(review);

        // Update product rating
        updateProductRating(productId);
    }

    // Check if user can review (already reviewed or not)
    public boolean canUserReview(String productId, String userId) {
        return !reviewRepository.existsByProductIdAndUserId(productId, userId);
    }
}
