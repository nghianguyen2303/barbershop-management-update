package com.barbershop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lich_hen_dich_vu")
public class LichHenDichVu {

    @EmbeddedId
    private LichHenDichVuId id = new LichHenDichVuId();

    @MapsId("maLh")
    @ManyToOne
    @JoinColumn(name = "ma_lh")
    private LichHen lichHen;

    @MapsId("maDv")
    @ManyToOne
    @JoinColumn(name = "ma_dv")
    private DichVu dichVu;

    @Column(name = "ghi_chu")
    private String ghiChu;

    // ================== GETTER - SETTER =====================

    public LichHenDichVuId getId() {
        return id;
    }

    public void setId(LichHenDichVuId id) {
        this.id = id;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
        this.id.setMaLh(lichHen != null ? lichHen.getMaLh() : null);
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
        this.id.setMaDv(dichVu != null ? dichVu.getMaDv() : null);
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
