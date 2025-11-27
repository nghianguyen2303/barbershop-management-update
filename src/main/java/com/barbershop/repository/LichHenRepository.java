package com.barbershop.repository;

import com.barbershop.entity.LichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LichHenRepository extends JpaRepository<LichHen, Integer> {

        // ===================== THỐNG KÊ =====================
        @Query("""
                            SELECT lh.khachHang.makh, COUNT(lh)
                            FROM LichHen lh
                            GROUP BY lh.khachHang.makh
                        """)
        List<Object[]> thongKeLuotDen();

        // ===================== LẤY DANH SÁCH =====================
        @Query("""
                            SELECT lh
                            FROM LichHen lh
                            ORDER BY lh.ngayHen DESC, lh.gioHen ASC
                        """)
        List<LichHen> findAllOrderByNgayDescGioAsc();

        @Query("""
                            SELECT lh
                            FROM LichHen lh
                            WHERE lh.khachHang.makh = :makh
                            ORDER BY lh.ngayHen DESC, lh.gioHen ASC
                        """)
        List<LichHen> findByKhachHangOrderByNgayDescGioAsc(Integer makh);

        // ===================== KIỂM TRA TRÙNG LỊCH CŨ =====================

        @Query("""
                            SELECT COUNT(lh)
                            FROM LichHen lh
                            WHERE lh.nhanVien.manv = :manv
                              AND lh.maLh <> :excludeId
                              AND lh.ngayHen = :ngayHen
                              AND lh.gioHen = :gioHen
                        """)
        int countOverlapForNhanVien(int manv,
                        LocalDate ngayHen,
                        LocalTime gioHen,
                        int excludeId);

        @Query("""
                            SELECT COUNT(lh)
                            FROM LichHen lh
                            WHERE lh.khachHang.makh = :makh
                              AND lh.maLh <> :excludeId
                              AND lh.ngayHen = :ngayHen
                              AND lh.gioHen = :gioHen
                        """)
        int countOverlapForKhach(int makh,
                        LocalDate ngayHen,
                        LocalTime gioHen,
                        int excludeId);

        @Query("""
                            SELECT COUNT(lh)
                            FROM LichHen lh
                            WHERE lh.nhanVien.manv = :manv
                              AND lh.khachHang.makh <> :makh
                              AND lh.maLh <> :excludeId
                              AND lh.ngayHen = :ngayHen
                              AND lh.gioHen = :gioHen
                        """)
        int countOverlapOtherKhach(int manv,
                        int makh,
                        LocalDate ngayHen,
                        LocalTime gioHen,
                        int excludeId);

        // ===================== KIỂM TRA TRÙNG LỊCH THEO KHOẢNG THỜI GIAN
        // =====================
        @Query(value = """
                            SELECT *
                            FROM lich_hen lh
                            WHERE lh.manv = :manv
                              AND lh.ngay_hen = :ngayHen
                              AND (
                                    (:gioBatDau BETWEEN lh.gio_hen AND ADDTIME(lh.gio_hen, INTERVAL :duration MINUTE))
                                    OR
                                    (ADDTIME(:gioBatDau, INTERVAL :duration MINUTE)
                                        BETWEEN lh.gio_hen AND ADDTIME(lh.gio_hen, INTERVAL :duration MINUTE))
                                    OR
                                    (lh.gio_hen BETWEEN :gioBatDau AND ADDTIME(:gioBatDau, INTERVAL :duration MINUTE))
                                  )
                        """, nativeQuery = true)
        List<LichHen> checkOverlapRange(
                        @Param("manv") Integer manv,
                        @Param("ngayHen") LocalDate ngayHen,
                        @Param("gioBatDau") LocalTime gioBatDau,
                        @Param("duration") Integer duration);

        @Query("SELECT COUNT(lh) FROM LichHen lh WHERE lh.ngayHen = :today")
        long countByNgayHen(@Param("today") LocalDate today);

        // ===================== LẤY LỊCH THEO NHÂN VIÊN =====================
        List<LichHen> findByNhanVien_Manv(Integer manv);

        // ===================== BỘ LỌC DANH SÁCH LỊCH HẸN (ADMIN) =====================
        @Query("""
                        SELECT lh FROM LichHen lh
                        WHERE
                            (
                                :keyword IS NULL
                                OR LOWER(lh.khachHang.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR lh.khachHang.sdt LIKE CONCAT('%', :keyword, '%')
                                OR LOWER(lh.nhanVien.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            )
                          AND (:fromDate IS NULL OR lh.ngayHen >= :fromDate)
                          AND (:toDate IS NULL OR lh.ngayHen <= :toDate)
                        ORDER BY lh.ngayHen DESC, lh.gioHen ASC
                        """)
        List<LichHen> searchForAdmin(
                        @Param("keyword") String keyword,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);
}
