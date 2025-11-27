package com.barbershop.controller;

import com.barbershop.entity.*;
import com.barbershop.repository.DichVuRepository;
import com.barbershop.repository.LichHenDichVuRepository;
import com.barbershop.repository.LichHenRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/lichhen-dichvu")
public class LichHenDichVuController {

    @Autowired
    private LichHenDichVuRepository repo;

    @Autowired
    private DichVuRepository dichVuRepo;

    @Autowired
    private LichHenRepository lichHenRepo;

    // =================== LIST ===================
    @GetMapping("/{maLh}")
    public String list(@PathVariable Integer maLh, Model model, HttpSession session) {

        if (session.getAttribute("user") == null) return "redirect:/login";

        List<LichHenDichVu> list = repo.findByLichHen_MaLh(maLh);

        model.addAttribute("list", list);
        model.addAttribute("maLh", maLh);

        return "lichhen-dichvu-list";
    }

    // =================== ADD FORM ===================
    @GetMapping("/add/{maLh}")
    public String addForm(@PathVariable Integer maLh, Model model, HttpSession session) {

        if (session.getAttribute("user") == null) return "redirect:/login";

        LichHen lh = lichHenRepo.findById(maLh).orElse(null);

        LichHenDichVu obj = new LichHenDichVu();
        obj.setLichHen(lh);

        model.addAttribute("obj", obj);
        model.addAttribute("listDichVu", dichVuRepo.findAll());

        return "lichhen-dichvu-add";
    }

    // =================== ADD (POST) ===================
    @PostMapping("/add")
    public String add(@ModelAttribute LichHenDichVu obj, Model model) {

        try {
            repo.save(obj);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "⚠ Dịch vụ này đã tồn tại trong lịch hẹn!");
            model.addAttribute("obj", obj);
            model.addAttribute("listDichVu", dichVuRepo.findAll());
            return "lichhen-dichvu-add";
        }

        return "redirect:/admin/lichhen-dichvu/" + obj.getLichHen().getMaLh();
    }

    // =================== EDIT FORM ===================
    @GetMapping("/edit/{maLh}/{maDv}")
    public String editForm(@PathVariable Integer maLh,
                           @PathVariable Integer maDv,
                           Model model,
                           HttpSession session) {

        if (session.getAttribute("user") == null) return "redirect:/login";

        LichHenDichVuId id = new LichHenDichVuId(maLh, maDv);
        LichHenDichVu obj = repo.findById(id).orElse(null);

        model.addAttribute("obj", obj);
        model.addAttribute("listDichVu", dichVuRepo.findAll());

        return "lichhen-dichvu-edit";
    }

    // =================== EDIT (POST) ===================
    @PostMapping("/edit")
    public String edit(@ModelAttribute LichHenDichVu obj) {

        // Không được đổi khóa → chỉ sửa ghi chú
        repo.save(obj);

        return "redirect:/admin/lichhen-dichvu/" + obj.getLichHen().getMaLh();
    }

    // =================== DELETE ===================
    @GetMapping("/delete/{maLh}/{maDv}")
    public String delete(@PathVariable Integer maLh, @PathVariable Integer maDv) {

        LichHenDichVuId id = new LichHenDichVuId(maLh, maDv);
        repo.deleteById(id);

        return "redirect:/admin/lichhen-dichvu/" + maLh;
    }
}
