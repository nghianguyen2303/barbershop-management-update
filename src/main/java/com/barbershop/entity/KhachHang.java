package com.barbershop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "khach_hang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makh")
    private Integer makh;

    @Column(name = "ho_ten", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    public KhachHang() {}

    // Getter - Setter
    public Integer getMakh() { return makh; }
    public void setMakh(Integer makh) { this.makh = makh; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
