package com.mycompany.models;

import jakarta.persistence.*;

@Entity
@Table(name = "loaihang")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maloai")
    private int maloai;

    @Column(name = "tenloai")
    private String tenloai;

    // Constructors
    public Category() {}

    public Category(int maloai, String tenloai) {
        this.maloai = maloai;
        this.tenloai = tenloai;
    }

    // Getters & Setters
    public int getMaloai() { return maloai; }
    public void setMaloai(int maloai) { this.maloai = maloai; }

    public String getTenloai() { return tenloai; }
    public void setTenloai(String tenloai) { this.tenloai = tenloai; }
}