package com.barbershop.controller;

import com.barbershop.entity.DichVu;
import com.barbershop.repository.DichVuRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/dichvu")
public class DichVuController {

    @Autowired
    private DichVuRepository dichVuRepo;

    // ===================== DANH SÁCH + BỘ LỌC =====================
    @GetMapping
    public String list(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        // Chuẩn hóa keyword rỗng => null để query gọn
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        List<DichVu> list = dichVuRepo.search(keyword, minPrice, maxPrice);

        model.addAttribute("listDichVu", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "dichvu-list";
    }

    // ===================== FORM THÊM =====================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("dichVu", new DichVu());
        return "dichvu-add";
    }

    // ===================== XỬ LÝ THÊM =====================
    @PostMapping("/add")
    public String add(@ModelAttribute DichVu dv) {
        dichVuRepo.save(dv);
        return "redirect:/admin/dichvu";
    }

    // ===================== FORM SỬA =====================
    @GetMapping("/edit/{maDv}")
    public String editForm(@PathVariable("maDv") int maDv,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        DichVu dv = dichVuRepo.findById(maDv).orElse(null);
        model.addAttribute("dichVu", dv);

        return "dichvu-edit";
    }

    // ===================== XỬ LÝ SỬA =====================
    @PostMapping("/edit")
    public String edit(@ModelAttribute DichVu dv) {
        dichVuRepo.save(dv);
        return "redirect:/admin/dichvu";
    }

    // ===================== XÓA =====================
    @GetMapping("/delete/{maDv}")
    public String delete(@PathVariable("maDv") int maDv) {
        dichVuRepo.deleteById(maDv);
        return "redirect:/admin/dichvu";
    }
}
