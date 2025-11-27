package com.barbershop.repository;

import com.barbershop.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
  boolean existsByLichHen_MaLh(Integer maLh);

  HoaDon findByLichHen_MaLh(Integer maLh);

  // Doanh thu theo tháng trong tất cả các năm
  @Query("""
      SELECT
          MONTH(hd.ngayThanhToan) AS thang,
          YEAR(hd.ngayThanhToan) AS nam,
          SUM(hd.tongTien) AS doanhThu
      FROM HoaDon hd
      GROUP BY YEAR(hd.ngayThanhToan), MONTH(hd.ngayThanhToan)
      ORDER BY YEAR(hd.ngayThanhToan), MONTH(hd.ngayThanhToan)
      """)
  List<Object[]> getDoanhThuTheoThang();

  // Doanh thu 1 năm cụ thể
  @Query("""
      SELECT
          MONTH(hd.ngayThanhToan),
          SUM(hd.tongTien)
      FROM HoaDon hd
      WHERE YEAR(hd.ngayThanhToan) = :nam
      GROUP BY MONTH(hd.ngayThanhToan)
      ORDER BY MONTH(hd.ngayThanhToan)
      """)
  List<Object[]> getDoanhThuTrongNam(int nam);

  // Tổng doanh thu theo năm
  @Query("""
      SELECT
          YEAR(hd.ngayThanhToan),
          SUM(hd.tongTien)
      FROM HoaDon hd
      GROUP BY YEAR(hd.ngayThanhToan)
      ORDER BY YEAR(hd.ngayThanhToan)
      """)
  List<Object[]> getDoanhThuTheoNam();

  @Query("""
      SELECT COALESCE(SUM(hd.tongTien), 0)
      FROM HoaDon hd
      WHERE YEAR(hd.ngayThanhToan) = :year
        AND MONTH(hd.ngayThanhToan) = :month
      """)
  Double getTongDoanhThuTheoThang(@Param("year") int year,
      @Param("month") int month);

  // Danh sách hóa đơn theo tháng / năm
  @Query("""
      SELECT hd
      FROM HoaDon hd
      WHERE YEAR(hd.ngayThanhToan) = :nam
        AND MONTH(hd.ngayThanhToan) = :thang
      ORDER BY hd.ngayThanhToan ASC, hd.maHd ASC
      """)
  List<HoaDon> findByMonthAndYear(@Param("nam") int nam,
      @Param("thang") int thang);

  // ============= BỘ LỌC DANH SÁCH HÓA ĐƠN =============
  @Query("""
      SELECT hd FROM HoaDon hd
      WHERE
          (
              :keyword IS NULL
              OR LOWER(hd.lichHen.khachHang.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR hd.lichHen.khachHang.sdt LIKE CONCAT('%', :keyword, '%')
          )
        AND (:fromDate IS NULL OR hd.ngayThanhToan >= :fromDate)
        AND (:toDate IS NULL OR hd.ngayThanhToan <= :toDate)
        AND (:method IS NULL OR LOWER(hd.phuongThucTt) = LOWER(:method))
      ORDER BY hd.ngayThanhToan DESC, hd.maHd DESC
      """)
  List<HoaDon> search(
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("method") String method);
}
