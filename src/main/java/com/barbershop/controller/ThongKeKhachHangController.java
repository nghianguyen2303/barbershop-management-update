package com.barbershop.controller;

import com.barbershop.entity.KhachHang;
import com.barbershop.repository.KhachHangRepository;
import com.barbershop.repository.LichHenRepository;
import com.barbershop.repository.LichHenDichVuRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/thongke/khachhang")
public class ThongKeKhachHangController {

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private LichHenRepository lichHenRepo;

    @Autowired
    private LichHenDichVuRepository ldvRepo;

    // =====================================================
    //  DANH SÁCH KHÁCH HÀNG + SỐ LẦN ĐẾN CỬA HÀNG
    // =====================================================
    @GetMapping
    public String list(Model model) {

        // Danh sách khách hàng
        List<KhachHang> khList = khRepo.findAll();

        // Lấy lượt đến từ repo
        List<Object[]> luotDen = lichHenRepo.thongKeLuotDen();

        // Map khách -> số lần đến
        Map<Integer, Long> mapLuotDen = new HashMap<>();
        for (Object[] row : luotDen) {
            Integer maKh = (Integer) row[0];
            Long count = (Long) row[1];
            mapLuotDen.put(maKh, count);
        }

        model.addAttribute("khList", khList);
        model.addAttribute("mapLuotDen", mapLuotDen);

        return "thongke-khachhang-list";
    }

    // =====================================================
    //  CHI TIẾT LỊCH SỬ SỬ DỤNG DỊCH VỤ CỦA 1 KHÁCH HÀNG
    // =====================================================
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {

        // Kiểm tra khách tồn tại
        KhachHang khachHang = khRepo.findById(id).orElse(null);
        if (khachHang == null) {
            model.addAttribute("errorMessage", "Khách hàng không tồn tại!");
            return "thongke-khachhang-detail";
        }

        // Lịch sử dịch vụ
        List<Object[]> lichSu = ldvRepo.lichSuDichVu(id);

        model.addAttribute("khachHang", khachHang);
        model.addAttribute("lichSu", lichSu);

        return "thongke-khachhang-detail";
    }
}
