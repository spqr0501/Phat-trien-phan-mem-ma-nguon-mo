package com.mycompany.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donhang")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "madon")
    private Long madon;

    @Column(name = "ngaydat")
    private LocalDateTime ngaydat;

    @Column(name = "makh", length = 20)
    private String makh;

    @Column(name = "hoten", length = 100)
    private String hoten;

    @Column(name = "diachi", length = 200)
    private String diachi;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "tongtien")
    private Integer tongtien;

    @Column(name = "trangthai", length = 50)
    private String trangthai; // Đang xử lý, Đã xác nhận, Đang giao, Hoàn thành, Hủy

    @Column(name = "ghichu", length = 500)
    private String ghichu;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // Constructors
    public Order() {
        this.ngaydat = LocalDateTime.now();
        this.trangthai = "Đang xử lý";
    }

    public Order(String makh, String hoten, String diachi, String sdt, String email, Integer tongtien) {
        this();
        this.makh = makh;
        this.hoten = hoten;
        this.diachi = diachi;
        this.sdt = sdt;
        this.email = email;
        this.tongtien = tongtien;
    }

    // Getters and Setters
    public Long getMadon() {
        return madon;
    }

    public void setMadon(Long madon) {
        this.madon = madon;
    }

    public LocalDateTime getNgaydat() {
        return ngaydat;
    }

    public void setNgaydat(LocalDateTime ngaydat) {
        this.ngaydat = ngaydat;
    }

    public String getMakh() {
        return makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTongtien() {
        return tongtien;
    }

    public void setTongtien(Integer tongtien) {
        this.tongtien = tongtien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    // Helper method to add order detail
    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
        detail.setOrder(this);
    }
}
