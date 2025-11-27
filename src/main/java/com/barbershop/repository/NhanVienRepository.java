package com.barbershop.repository;

import com.barbershop.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.barbershop.entity.Account;

import java.util.List;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    @Query("SELECT nv FROM NhanVien nv " +
            "WHERE LOWER(nv.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR nv.sdt LIKE CONCAT('%', :keyword, '%')")
    List<NhanVien> search(String keyword);

    NhanVien findByAccount(Account account);

}
