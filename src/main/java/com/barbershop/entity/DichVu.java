package com.barbershop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dich_vu")
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dv")
    private Integer maDv;
    @Column(name = "ten_dv", length = 100)
    private String tenDv;

    public String getTenDv() {
        return tenDv;
    }

    public void setTenDv(String tenDv) {
        this.tenDv = tenDv;
    }
    @Column(name = "gia")
    private Double gia;

    @Column(name = "thoi_gian_thuc_hien")
    private Integer thoiGianThucHien; // phút

    // Getter – Setter
    public Integer getMaDv() {
        return maDv;
    }

    public void setMaDv(Integer maDv) {
        this.maDv = maDv;
    }

    public Double getGia() {
        return gia;
    }

    public void setGia(Double gia) {
        this.gia = gia;
    }

    public Integer getThoiGianThucHien() {
        return thoiGianThucHien;
    }

    public void setThoiGianThucHien(Integer thoiGianThucHien) {
        this.thoiGianThucHien = thoiGianThucHien;
    }
}
