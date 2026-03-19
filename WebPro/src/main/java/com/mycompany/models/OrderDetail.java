package com.mycompany.models;

import jakarta.persistence.*;

@Entity
@Table(name = "chitietdonhang")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mactdh")
    private Long mactdh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madon")
    private Order order;

    @Column(name = "mahh", length = 20)
    private String mahh;

    @Column(name = "tenhh", length = 200)
    private String tenhh;

    @Column(name = "dongia")
    private Integer dongia;

    @Column(name = "soluong")
    private Integer soluong;

    @Column(name = "thanhtien")
    private Integer thanhtien;

    // Constructors
    public OrderDetail() {
    }

    public OrderDetail(String mahh, String tenhh, Integer dongia, Integer soluong) {
        this.mahh = mahh;
        this.tenhh = tenhh;
        this.dongia = dongia;
        this.soluong = soluong;
        this.thanhtien = dongia * soluong;
    }

    // Getters and Setters
    public Long getMactdh() {
        return mactdh;
    }

    public void setMactdh(Long mactdh) {
        this.mactdh = mactdh;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

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

    public Integer getDongia() {
        return dongia;
    }

    public void setDongia(Integer dongia) {
        this.dongia = dongia;
    }

    public Integer getSoluong() {
        return soluong;
    }

    public void setSoluong(Integer soluong) {
        this.soluong = soluong;
        this.thanhtien = this.dongia * soluong;
    }

    public Integer getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(Integer thanhtien) {
        this.thanhtien = thanhtien;
    }
}
