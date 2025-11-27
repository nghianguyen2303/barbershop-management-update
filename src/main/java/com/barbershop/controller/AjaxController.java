package com.barbershop.controller;

import com.barbershop.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax")
public class AjaxController {

    @Autowired
    private KhachHangRepository khRepo;

    @GetMapping("/check-sdt")
    public String checkSdt(@RequestParam String value) {
        return khRepo.existsBySdt(value) ? "exists" : "ok";
    }
}
