// src/main/java/com/mycompany/models/CartItem.java
package com.mycompany.models;

public class CartItem {
    private String mahh;
    private String tenhh;
    private int dongia;
    private String hinh;
    private int soluong = 1;

    // Constructor
    public CartItem() {}
    public CartItem(String mahh, String tenhh, int dongia, String hinh) {
        this.mahh = mahh;
        this.tenhh = tenhh;
        this.dongia = dongia;
        this.hinh = hinh;
    }

    // Getters & Setters
    public String getMahh() { return mahh; }
    public void setMahh(String mahh) { this.mahh = mahh; }

    public String getTenhh() { return tenhh; }
    public void setTenhh(String tenhh) { this.tenhh = tenhh; }

    public int getDongia() { return dongia; }
    public void setDongia(int dongia) { this.dongia = dongia; }

    public String getHinh() { return hinh; }
    public void setHinh(String hinh) { this.hinh = hinh; }

    public int getSoluong() { return soluong; }
    public void setSoluong(int soluong) { this.soluong = soluong; }

    public int getThanhtien() { return dongia * soluong; }
}