package com.barbershop.controller;

import com.barbershop.entity.Account;
import com.barbershop.entity.KhachHang;
import com.barbershop.repository.AccountRepository;
import com.barbershop.repository.KhachHangRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {

        Object err = session.getAttribute("registerError");
        if (err != null) {
            model.addAttribute("errorMsg", err);
            session.removeAttribute("registerError");
        }

        Object ok = session.getAttribute("registerSuccess");
        if (ok != null) {
            model.addAttribute("successMsg", ok);
            session.removeAttribute("registerSuccess");
        }

        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String hoTen,
                           @RequestParam String sdt,
                           HttpSession session) {

        if (accountRepo.findByUsername(username) != null) {
            session.setAttribute("registerError", "Tên đăng nhập đã tồn tại!");
            return "redirect:/register";
        }

        Account acc = new Account();
        acc.setUsername(username);
        acc.setPassword(password);
        acc.setRole("ROLE_USER");
        acc = accountRepo.save(acc);

        KhachHang kh = new KhachHang();
        kh.setHoTen(hoTen);
        kh.setSdt(sdt);
        kh.setAccount(acc);  // Quan hệ 1-1 đúng chuẩn
        khRepo.save(kh);

        session.setAttribute("registerSuccess", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/register";
    }
}
