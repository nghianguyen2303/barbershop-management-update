package com.barbershop.controller;

import com.barbershop.repository.KhachHangRepository;
import com.barbershop.repository.NhanVienRepository;
import com.barbershop.repository.LichHenRepository;
import com.barbershop.repository.HoaDonRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class AdminController {

    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final LichHenRepository lichHenRepository;
    private final HoaDonRepository hoaDonRepository;

    public AdminController(NhanVienRepository nhanVienRepository,
            KhachHangRepository khachHangRepository,
            LichHenRepository lichHenRepository,
            HoaDonRepository hoaDonRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
        this.lichHenRepository = lichHenRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    @GetMapping("/admin/home")
    public String adminHome(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // ===== SỐ LIỆU ĐỘNG =====
        long tongNhanVien = nhanVienRepository.count();
        long tongKhachHang = khachHangRepository.count();

        LocalDate today = LocalDate.now();
        long lichHenHomNay = lichHenRepository.countByNgayHen(today);

        int year = today.getYear();
        int month = today.getMonthValue();
        Double doanhThuThang = hoaDonRepository.getTongDoanhThuTheoThang(year, month);
        if (doanhThuThang == null)
            doanhThuThang = 0.0;

        // Đưa ra view
        model.addAttribute("tongNhanVien", tongNhanVien);
        model.addAttribute("tongKhachHang", tongKhachHang);
        model.addAttribute("lichHenHomNay", lichHenHomNay);
        model.addAttribute("doanhThuThang", doanhThuThang);

        return "admin-dashboard";
    }
}
