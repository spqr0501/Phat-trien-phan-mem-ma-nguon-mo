package com.mycompany.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "product_id", nullable = false, length = 10)
    private String productId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(nullable = false)
    private Integer rating; // 1-5 stars

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // For displaying user name
    @Transient
    private String userName;

    // Constructors
    public Review() {
        this.createdDate = LocalDateTime.now();
    }

    public Review(String productId, String userId, Integer rating, String comment) {
        this();
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
