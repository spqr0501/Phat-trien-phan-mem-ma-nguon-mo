package com.mycompany.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "product_id" })
})
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "product_id", nullable = false, length = 10)
    private String productId;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    // Constructors
    public Wishlist() {
        this.addedDate = LocalDateTime.now();
    }

    public Wishlist(String userId, String productId) {
        this();
        this.userId = userId;
        this.productId = productId;
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

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
