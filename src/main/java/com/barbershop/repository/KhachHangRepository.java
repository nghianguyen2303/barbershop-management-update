package com.barbershop.repository;

import com.barbershop.entity.Account;
import com.barbershop.entity.KhachHang;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    KhachHang findBySdt(String sdt);

    boolean existsBySdt(String sdt);

    // Tìm theo Account (1-1)
    @Query("SELECT k FROM KhachHang k WHERE k.account = :acc")
    KhachHang findByAccount(@Param("acc") Account account);

    // Tìm theo account_id (nếu cần dạng số)
    @Query("SELECT k FROM KhachHang k WHERE k.account.id = :id")
    KhachHang findByAccountId(@Param("id") Integer id);

    @Query("""
            SELECT k
            FROM KhachHang k
            WHERE LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR k.sdt LIKE CONCAT('%', :keyword, '%')
            """)
    List<KhachHang> search(String keyword);
}
