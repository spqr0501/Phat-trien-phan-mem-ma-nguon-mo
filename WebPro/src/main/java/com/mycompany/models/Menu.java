package com.mycompany.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "position", nullable = false)
    private Integer position = 0;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Menu() {
    }

    public Menu(String menuName, Integer parentId, Integer position, String status, String url) {
        this.menuName = menuName;
        this.parentId = parentId;
        this.position = position;
        this.status = status;
        this.url = url;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
