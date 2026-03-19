package com.mycompany.controllers;

import com.mycompany.models.Review;
import com.mycompany.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Get all reviews for a product
    @GetMapping("/product/{productId}")
    @ResponseBody
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable String productId) {
        try {
            List<Review> reviews = reviewService.getProductReviews(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Add a new review
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addReview(
            @RequestParam String productId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập để đánh giá!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String userId = authentication.getName();
            Review review = reviewService.addReview(productId, userId, rating, comment);

            response.put("success", true);
            response.put("message", "Đánh giá thành công!");
            response.put("review", review);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra, vui lòng thử lại!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete a review
    @DeleteMapping("/{reviewId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Integer reviewId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String userId = authentication.getName();
            reviewService.deleteReview(reviewId, userId);

            response.put("success", true);
            response.put("message", "Xóa đánh giá thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Check if user can review
    @GetMapping("/can-review/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> canUserReview(
            @PathVariable String productId,
            Authentication authentication) {

        Map<String, Boolean> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("canReview", false);
            return ResponseEntity.ok(response);
        }

        String userId = authentication.getName();
        boolean canReview = reviewService.canUserReview(productId, userId);
        response.put("canReview", canReview);

        return ResponseEntity.ok(response);
    }
}
