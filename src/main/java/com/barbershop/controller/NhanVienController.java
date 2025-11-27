package com.barbershop.controller;

import com.barbershop.entity.Account;
import com.barbershop.entity.NhanVien;
import com.barbershop.repository.AccountRepository;
import com.barbershop.repository.CaLamRepository;
import com.barbershop.repository.NhanVienRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/nhanvien")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private CaLamRepository caLamRepo;

    @Autowired
    private AccountRepository accountRepo; // <<< thêm

    // =================== DANH SÁCH + TÌM KIẾM ====================
    @GetMapping
    public String list(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        List<NhanVien> list;

        if (keyword != null && !keyword.trim().isEmpty()) {
            list = nhanVienRepo.search(keyword.trim());
        } else {
            list = nhanVienRepo.findAll();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("listNhanVien", list);
        return "nhanvien-list";
    }

    // =================== FORM THÊM ====================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("listCaLam", caLamRepo.findAll());
        return "nhanvien-add";
    }

    // =================== XỬ LÝ THÊM (TỰ TẠO ACCOUNT STAFF) ====================
    @PostMapping("/add")
    public String add(@ModelAttribute NhanVien nv) {

        // Chỉ tạo account nếu NV chưa có account
        if (nv.getAccount() == null) {

            Account acc = new Account();

            // ----- sinh username mặc định -----
            String baseUsername;
            if (nv.getSdt() != null && !nv.getSdt().isBlank()) {
                baseUsername = nv.getSdt().trim();
            } else if (nv.getHoTen() != null) {
                baseUsername = nv.getHoTen()
                        .toLowerCase()
                        .trim()
                        .replaceAll("\\s+", "");
            } else {
                baseUsername = "staff";
            }

            String username = baseUsername;
            int suffix = 1;
            // Nếu trùng username thì thêm số 1,2,3...
            while (accountRepo.existsByUsername(username)) {
                username = baseUsername + suffix;
                suffix++;
            }

            acc.setUsername(username);
            acc.setPassword("123456"); // password mặc định
            acc.setRole("STAFF");

            // Lưu account trước
            accountRepo.save(acc);

            // Gắn account vào nhân viên
            nv.setAccount(acc);
        }

        nhanVienRepo.save(nv);
        return "redirect:/admin/nhanvien";
    }

    // =================== FORM SỬA ====================
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        NhanVien nv = nhanVienRepo.findById(id).orElse(null);

        model.addAttribute("nhanVien", nv);
        model.addAttribute("listCaLam", caLamRepo.findAll());
        return "nhanvien-edit";
    }

    // =================== XỬ LÝ SỬA (KHÔNG MẤT ACCOUNT_ID) ====================
    @PostMapping("/edit")
    public String edit(@ModelAttribute NhanVien nvForm,
            @RequestParam("accountId") Integer accountId) {

        // 1. Lấy NV hiện tại trong DB
        NhanVien nvDb = nhanVienRepo.findById(nvForm.getManv()).orElse(null);
        if (nvDb == null)
            return "redirect:/admin/nhanvien";

        // 2. Cập nhật các trường cho phép sửa
        nvDb.setHoTen(nvForm.getHoTen());
        nvDb.setSdt(nvForm.getSdt());
        nvDb.setGioiTinh(nvForm.getGioiTinh());
        nvDb.setNgaySinh(nvForm.getNgaySinh());
        nvDb.setChucVu(nvForm.getChucVu());
        nvDb.setNgayVaoLam(nvForm.getNgayVaoLam());
        nvDb.setLuongCoBan(nvForm.getLuongCoBan());
        nvDb.setCaLam(nvForm.getCaLam());

        // 3. KHÔNG đụng vào account (giữ nguyên liên kết)
        if (nvDb.getAccount() != null &&
                !nvDb.getAccount().getId().equals(accountId)) {
            // trường hợp bất thường thì thôi cũng không sửa, để tránh mất link
        }

        // 4. Lưu
        nhanVienRepo.save(nvDb);

        return "redirect:/admin/nhanvien";
    }

    // =================== XÓA ====================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        nhanVienRepo.deleteById(id);
        return "redirect:/admin/nhanvien";
    }
}
