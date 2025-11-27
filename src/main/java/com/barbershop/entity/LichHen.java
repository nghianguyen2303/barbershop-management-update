package com.barbershop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "lich_hen")
public class LichHen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lh")
    private Integer maLh;

    // TÁCH thành 2 cột: ngay_hen (DATE), gio_hen (TIME)
    @Column(name = "ngay_hen")
    private LocalDate ngayHen;

    @Column(name = "gio_hen")
    private LocalTime gioHen;

    @ManyToOne
    @JoinColumn(name = "makh")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "manv")
    private NhanVien nhanVien;

    @OneToMany(mappedBy = "lichHen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LichHenDichVu> dichVus;

    // getters / setters
    public Integer getMaLh() {
        return maLh;
    }

    public void setMaLh(Integer maLh) {
        this.maLh = maLh;
    }

    public LocalDate getNgayHen() {
        return ngayHen;
    }

    public void setNgayHen(LocalDate ngayHen) {
        this.ngayHen = ngayHen;
    }

    public LocalTime getGioHen() {
        return gioHen;
    }

    public void setGioHen(LocalTime gioHen) {
        this.gioHen = gioHen;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public List<LichHenDichVu> getDichVus() {
        return dichVus;
    }

    public void setDichVus(List<LichHenDichVu> dichVus) {
        this.dichVus = dichVus;
    }
}
