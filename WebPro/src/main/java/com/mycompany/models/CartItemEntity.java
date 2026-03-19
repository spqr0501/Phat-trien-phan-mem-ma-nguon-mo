package com.mycompany.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "product_id", nullable = false, length = 255)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    // Constructors
    public CartItemEntity() {
        this.addedDate = LocalDateTime.now();
    }

    public CartItemEntity(String userId, String productId, Integer quantity) {
        this();
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
}
