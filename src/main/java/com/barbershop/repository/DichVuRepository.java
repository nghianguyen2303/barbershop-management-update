package com.barbershop.repository;

import com.barbershop.entity.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DichVuRepository extends JpaRepository<DichVu, Integer> {

    @Query("""
            SELECT dv FROM DichVu dv
            WHERE (:keyword IS NULL OR LOWER(dv.tenDv) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:minPrice IS NULL OR dv.gia >= :minPrice)
              AND (:maxPrice IS NULL OR dv.gia <= :maxPrice)
            ORDER BY dv.maDv ASC
            """)
    List<DichVu> search(
            @Param("keyword") String keyword,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);
}
