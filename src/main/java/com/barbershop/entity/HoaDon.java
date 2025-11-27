package com.barbershop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hd")
    private Integer maHd;

    @Column(name = "ngay_thanh_toan")
    private LocalDate ngayThanhToan;

    @Column(name = "tong_tien")
    private Double tongTien;

    @Column(name = "phuong_thuc_tt", length = 50)
    private String phuongThucTt;

    @ManyToOne
    @JoinColumn(name = "ma_lh")
    private LichHen lichHen;

    // --------------------------
    //  Hàm tính tổng tiền tự động
    // --------------------------
    public void tinhTongTien(List<LichHenDichVu> ds) {
        double sum = ds.stream()
                .mapToDouble(item -> item.getDichVu().getGia())
                .sum();
        this.tongTien = sum;
    }

    // GETTER - SETTER
    public Integer getMaHd() {
        return maHd;
    }

    public void setMaHd(Integer maHd) {
        this.maHd = maHd;
    }

    public LocalDate getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDate ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public String getPhuongThucTt() {
        return phuongThucTt;
    }

    public void setPhuongThucTt(String phuongThucTt) {
        this.phuongThucTt = phuongThucTt;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }
}
