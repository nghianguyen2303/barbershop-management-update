package com.barbershop.controller;

import com.barbershop.entity.KhachHang;
import com.barbershop.repository.KhachHangRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khachHangRepo;

    // ===================== DANH SÁCH + TÌM KIẾM =====================
    @GetMapping
    public String list(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        List<KhachHang> list;

        if (keyword != null && !keyword.trim().isEmpty()) {
            list = khachHangRepo.search(keyword.trim());
        } else {
            list = khachHangRepo.findAll();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("listKhachHang", list);

        return "khachhang-list";
    }

    // ===================== FORM THÊM =====================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("khachHang", new KhachHang());
        return "khachhang-add";
    }

    // ===================== XỬ LÝ THÊM =====================
    @PostMapping("/add")
    public String add(@ModelAttribute KhachHang kh, RedirectAttributes ra) {

        if (kh.getMakh() == null) {
            ra.addFlashAttribute("errorMsg", "Vui lòng nhập mã khách hàng!");
            return "redirect:/admin/khachhang/add";
        }

        if (khachHangRepo.existsById(kh.getMakh())) {
            ra.addFlashAttribute("errorMsg", "Mã khách hàng đã tồn tại!");
            return "redirect:/admin/khachhang/add";
        }

        khachHangRepo.save(kh);
        ra.addFlashAttribute("successMsg", "Thêm khách hàng thành công!");
        return "redirect:/admin/khachhang";
    }

    // ===================== FORM SỬA =====================
    @GetMapping("/edit/{makh}")
    public String editForm(@PathVariable("makh") int makh, Model model, HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        KhachHang kh = khachHangRepo.findById(makh).orElse(null);
        model.addAttribute("khachHang", kh);

        return "khachhang-edit";
    }

    // ===================== XỬ LÝ SỬA (ĐÃ FIX MẤT ACCOUNT) =====================
    @PostMapping("/edit")
    public String edit(@ModelAttribute KhachHang khForm) {

        // 1. Lấy bản gốc từ database
        KhachHang khDb = khachHangRepo.findById(khForm.getMakh()).orElse(null);
        if (khDb == null)
            return "redirect:/admin/khachhang";

        // 2. Cập nhật các trường được phép sửa
        khDb.setHoTen(khForm.getHoTen());
        khDb.setGioiTinh(khForm.getGioiTinh());
        khDb.setNgaySinh(khForm.getNgaySinh());
        khDb.setSdt(khForm.getSdt());

        // 🟢 3. KHÔNG ghi đè account => giữ nguyên account_id
        // (Không làm gì cả)

        khachHangRepo.save(khDb);

        return "redirect:/admin/khachhang";
    }

    // ===================== XÓA =====================
    @GetMapping("/delete/{makh}")
    public String delete(@PathVariable("makh") int makh) {
        khachHangRepo.deleteById(makh);
        return "redirect:/admin/khachhang";
    }
}
