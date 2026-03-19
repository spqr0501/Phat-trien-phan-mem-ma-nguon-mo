package com.mycompany.models;

import jakarta.persistence.*;

@Entity
@Table(name = "khachhang")
public class Customer {

    @Id
    @Column(name = "makh")
    private String makh;

    @Column(name = "tenkh")
    private String tenkh;

    @Column(name = "diachi")
    private String diachi;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    // Constructors
    public Customer() {}

    public Customer(String makh, String tenkh, String diachi, String sdt, String email) {
        this.makh = makh;
        this.tenkh = tenkh;
        this.diachi = diachi;
        this.sdt = sdt;
        this.email = email;
    }

    // Getters & Setters
    public String getMakh() { return makh; }
    public void setMakh(String makh) { this.makh = makh; }

    public String getTenkh() { return tenkh; }
    public void setTenkh(String tenkh) { this.tenkh = tenkh; }

    public String getDiachi() { return diachi; }
    public void setDiachi(String diachi) { this.diachi = diachi; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}