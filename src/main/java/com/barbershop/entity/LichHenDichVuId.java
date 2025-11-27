package com.barbershop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LichHenDichVuId implements Serializable {

    @Column(name = "ma_lh")
    private Integer maLh;

    @Column(name = "ma_dv")
    private Integer maDv;

    public LichHenDichVuId() {}

    public LichHenDichVuId(Integer maLh, Integer maDv) {
        this.maLh = maLh;
        this.maDv = maDv;
    }

    public Integer getMaLh() {
        return maLh;
    }

    public void setMaLh(Integer maLh) {
        this.maLh = maLh;
    }

    public Integer getMaDv() {
        return maDv;
    }

    public void setMaDv(Integer maDv) {
        this.maDv = maDv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LichHenDichVuId)) return false;
        LichHenDichVuId that = (LichHenDichVuId) o;
        return Objects.equals(maLh, that.maLh) &&
               Objects.equals(maDv, that.maDv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLh, maDv);
    }
}
