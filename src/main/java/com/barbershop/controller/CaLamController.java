package com.barbershop.controller;

import com.barbershop.entity.CaLam;
import com.barbershop.repository.CaLamRepository;

import com.barbershop.temp.ShiftChangeRequestStore;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/calam")
public class CaLamController {

    @Autowired
    private CaLamRepository caLamRepo;

    // ===================== DANH SÁCH CA =====================
    @GetMapping
    public String list(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        List<CaLam> list = caLamRepo.findAll();
        model.addAttribute("listCaLam", list);

        // Đẩy danh sách yêu cầu đổi ca (lưu tạm trong RAM) cho view
        model.addAttribute("shiftRequests", ShiftChangeRequestStore.getAll());

        return "calam-list";
    }

    // ============ ADMIN ĐÁNH DẤU ĐÃ XỬ LÝ YÊU CẦU ĐỔI CA ============
    @GetMapping("/request/delete/{id}")
    public String deleteShiftRequest(@PathVariable("id") int id,
            HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        ShiftChangeRequestStore.removeById(id);

        return "redirect:/admin/calam";
    }

    // ===================== FORM THÊM =====================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("caLam", new CaLam());
        model.addAttribute("errorMessage", null);
        return "calam-add";
    }

    // ===================== XỬ LÝ THÊM =====================
    @PostMapping("/add")
    public String add(@ModelAttribute CaLam caLam, Model model) {

        // Kiểm tra mã ca đã tồn tại chưa
        if (caLamRepo.existsById(caLam.getMaCa())) {
            model.addAttribute("errorMessage", "❌ Mã ca đã tồn tại! Vui lòng nhập mã khác.");
            model.addAttribute("caLam", caLam);
            return "calam-add";
        }

        caLamRepo.save(caLam);
        return "redirect:/admin/calam";
    }

    // ===================== FORM SỬA =====================
    @GetMapping("/edit/{maCa}")
    public String editForm(@PathVariable("maCa") Integer maCa, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        CaLam caLam = caLamRepo.findById(maCa).orElse(null);

        if (caLam == null) {
            return "redirect:/admin/calam";
        }

        model.addAttribute("caLam", caLam);
        model.addAttribute("errorMessage", null);
        return "calam-edit";
    }

    // ===================== XỬ LÝ SỬA =====================
    @PostMapping("/edit")
    public String edit(@ModelAttribute CaLam caLam, Model model) {

        // Kiểm tra tồn tại
        if (!caLamRepo.existsById(caLam.getMaCa())) {
            model.addAttribute("errorMessage", "❌ Mã ca không tồn tại!");
            return "calam-edit";
        }

        caLamRepo.save(caLam);
        return "redirect:/admin/calam";
    }

    // ===================== XÓA =====================
    @GetMapping("/delete/{maCa}")
    public String delete(@PathVariable("maCa") Integer maCa) {

        if (caLamRepo.existsById(maCa)) {
            caLamRepo.deleteById(maCa);
        }

        return "redirect:/admin/calam";
    }
}
