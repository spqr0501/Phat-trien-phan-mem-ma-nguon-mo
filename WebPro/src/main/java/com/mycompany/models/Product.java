package com.mycompany.models;

import jakarta.persistence.*;

@Entity
@Table(name = "hanghoa")
public class Product {

    @Id
    @Column(name = "mahh", length = 10, nullable = false)
    private String mahh;

    @Column(name = "tenhh", length = 200, nullable = false)
    private String tenhh;

    @Column(name = "dongia", nullable = false)
    private int dongia;

    // QUAN TRỌNG: DÙNG Integer ĐỂ TRÁNH LỖI NULL KHI CỘT MỚI THÊM
    @Column(name = "giamgia", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer giamgia = 0;

    @Column(name = "mota", columnDefinition = "NVARCHAR(MAX)")
    private String mota;

    @Column(name = "xuatxu", length = 50)
    private String xuatxu;

    @Column(name = "loaihh")
    private Integer loaihh;

    @Column(name = "hinh", length = 200)
    private String hinh;

    @Column(name = "xuhuong")
    private Integer xuhuong;

    @Column(name = "phobien")
    private Integer phobien;

    // NEW: Review ratings and stock
    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2)")
    private Double averageRating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "soluong")
    private Integer soluong;

    // ==================== CONSTRUCTORS ====================
    public Product() {
        this.xuhuong = 0; // Default: không phải xu hướng
        this.phobien = 0; // Default: không phổ biến
    }

    public Product(String mahh, String tenhh, int dongia, Integer giamgia, String mota,
            String xuatxu, Integer loaihh, String hinh, Integer xuhuong, Integer phobien) {
        this.mahh = mahh;
        this.tenhh = tenhh;
        this.dongia = dongia;
        this.giamgia = giamgia == null ? 0 : giamgia;
        this.mota = mota;
        this.xuatxu = xuatxu;
        this.loaihh = loaihh;
        this.hinh = hinh;
        this.xuhuong = xuhuong;
        this.phobien = phobien;
    }

    // ==================== GETTERS & SETTERS ====================

    public String getMahh() {
        return mahh;
    }

    public void setMahh(String mahh) {
        this.mahh = mahh;
    }

    public String getTenhh() {
        return tenhh;
    }

    public void setTenhh(String tenhh) {
        this.tenhh = tenhh;
    }

    public int getDongia() {
        return dongia;
    }

    public void setDongia(int dongia) {
        this.dongia = dongia;
    }

    public Integer getGiamgia() {
        return giamgia;
    }

    public void setGiamgia(Integer giamgia) {
        this.giamgia = giamgia;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getXuatxu() {
        return xuatxu;
    }

    public void setXuatxu(String xuatxu) {
        this.xuatxu = xuatxu;
    }

    public Integer getLoaihh() {
        return loaihh;
    }

    public void setLoaihh(Integer loaihh) {
        this.loaihh = loaihh;
    }

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    public Integer getXuhuong() {
        return xuhuong;
    }

    public void setXuhuong(Integer xuhuong) {
        this.xuhuong = xuhuong;
    }

    public Integer getPhobien() {
        return phobien;
    }

    public void setPhobien(Integer phobien) {
        this.phobien = phobien;
    }

    // NEW FIELDS: Getters and Setters
    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getSoluong() {
        return soluong;
    }

    public void setSoluong(Integer soluong) {
        this.soluong = soluong;
    }

    // ==================== COMPUTED PROPERTIES ====================

    /**
     * Tính giá hiện tại sau khi giảm giá
     * 
     * @return Giá sau khi giảm (nếu giamgia > 0), hoặc giá gốc
     */
    @Transient
    public int getGiaHienTai() {
        if (getGiamgia() > 0) {
            return dongia - getGiamgia(); // Giảm theo số tiền cố định
        }
        return dongia; // Không giảm giá
    }

    /**
     * Tính % giảm giá
     * 
     * @return % giảm giá (làm tròn)
     */
    @Transient
    public int getPhanTramGiam() {
        if (getGiamgia() <= 0 || dongia <= 0)
            return 0;
        // SỬA: Đúng công thức là giamgia / dongia (không phải dongia + giamgia)
        return (int) Math.round((double) getGiamgia() / dongia * 100);
    }

    // ==================== toString ====================
    @Override
    public String toString() {
        return "Product{" +
                "mahh='" + mahh + '\'' +
                ", tenhh='" + tenhh + '\'' +
                ", dongia=" + dongia +
                ", giamgia=" + getGiamgia() +
                ", giaHienTai=" + getGiaHienTai() +
                ", phanTramGiam=" + getPhanTramGiam() + "%" +
                ", mota='" + mota + '\'' +
                ", xuatxu='" + xuatxu + '\'' +
                ", loaihh=" + loaihh +
                ", hinh='" + hinh + '\'' +
                ", xuhuong=" + xuhuong +
                ", phobien=" + phobien +
                '}';
    }
}