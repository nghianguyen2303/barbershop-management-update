package com.barbershop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ca_lam")
public class CaLam {

    @Id
    @Column(name = "ma_ca")
    private Integer maCa;

    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;

    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;

    public CaLam() {}

    // ================= GETTER - SETTER =================

    public Integer getMaCa() {
        return maCa;
    }

    public void setMaCa(Integer maCa) {
        this.maCa = maCa;
    }

    public LocalDate getNgayLamViec() {
        return ngayLamViec;
    }

    public void setNgayLamViec(LocalDate ngayLamViec) {
        this.ngayLamViec = ngayLamViec;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    // ================= COMPUTED FIELD =================
    // Đây là tên Ca hiển thị trên giao diện
    public String getTenCa() {
        return "Ca " + maCa + " (" + gioBatDau + " - " + gioKetThuc + ")";
    }
}
