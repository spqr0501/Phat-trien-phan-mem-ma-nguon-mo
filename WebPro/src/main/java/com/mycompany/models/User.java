package com.mycompany.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "nguoidung")
public class User implements UserDetails { // QUAN TRỌNG: implements UserDetails

    @Id
    @Column(name = "tendangnhap", length = 50)
    private String tendangnhap;

    @Column(name = "matkhau", nullable = false)
    private String matkhau;

    @Column(name = "hoten", length = 100)
    private String hoten;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "admin", nullable = false)
    private boolean admin = false; // mặc định là user thường

    @Column(name = "sodienthoai", length = 15)
    private String sodienthoai;

    @Column(name = "avatar", length = 200)
    private String avatar;

    // ====================== Constructors ======================
    public User() {
    }

    public User(String tendangnhap, String matkhau, String hoten, String email, boolean admin) {
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.hoten = hoten;
        this.email = email;
        this.admin = admin;
    }

    public User(String tendangnhap, String matkhau, String hoten, String email, boolean admin, String sodienthoai,
            String avatar) {
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.hoten = hoten;
        this.email = email;
        this.admin = admin;
        this.sodienthoai = sodienthoai;
        this.avatar = avatar;
    }

    // ====================== Getters & Setters ======================
    public String getTendangnhap() {
        return tendangnhap;
    }

    public void setTendangnhap(String tendangnhap) {
        this.tendangnhap = tendangnhap;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // ====================== UserDetails Methods ======================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.admin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.matkhau;
    }

    @Override
    public String getUsername() {
        return this.tendangnhap;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}