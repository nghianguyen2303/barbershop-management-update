package com.barbershop.repository;

import com.barbershop.entity.LichHenDichVu;
import com.barbershop.entity.LichHenDichVuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LichHenDichVuRepository extends JpaRepository<LichHenDichVu, LichHenDichVuId> {

    // Lấy tất cả dịch vụ theo mã lịch hẹn
    List<LichHenDichVu> findByLichHen_MaLh(Integer maLh);

    // XÓA TẤT CẢ dịch vụ của 1 lịch hẹn → CẦN TRANSACTION + MODIFYING
    @Modifying
    @Transactional
    void deleteByLichHen_MaLh(Integer maLh);

    // Lịch sử dịch vụ của khách
    @Query("""
        SELECT dv.tenDv, dv.gia, lh.ngayHen, lh.gioHen
        FROM LichHenDichVu ldv
        JOIN ldv.lichHen lh
        JOIN ldv.dichVu dv
        WHERE lh.khachHang.makh = :idKhach
        ORDER BY lh.ngayHen DESC, lh.gioHen DESC
    """)
    List<Object[]> lichSuDichVu(Integer idKhach);
}
