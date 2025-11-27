package com.barbershop.controller;

import com.barbershop.entity.*;
import com.barbershop.repository.*;

import com.barbershop.temp.ShiftChangeRequest;
import com.barbershop.temp.ShiftChangeRequestStore;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private NhanVienRepository nvRepo;

    @Autowired
    private LichHenRepository lichHenRepo;

    @Autowired
    private LichHenDichVuRepository lhDvRepo;

    // =================== UTIL ===================
    private NhanVien getNhanVienFromSession(HttpSession session) {
        Account acc = (Account) session.getAttribute("user");
        if (acc == null)
            return null;
        return nvRepo.findByAccount(acc);
    }

    private String checkStaffRole(HttpSession session) {
        Account acc = (Account) session.getAttribute("user");
        if (acc == null || acc.getRole() == null)
            return "redirect:/login";

        if (!acc.getRole().equalsIgnoreCase("STAFF"))
            return "redirect:/login";

        return null;
    }

    // =================== STAFF HOME ===================
    @GetMapping("/home")
    public String homeStaff(Model model, HttpSession session) {

        String redirect = checkStaffRole(session);
        if (redirect != null)
            return redirect;

        LocalDate today = LocalDate.now();

        // lấy thông báo
        Object success = session.getAttribute("successMsg");
        Object error = session.getAttribute("errorMsg");
        if (success != null) {
            model.addAttribute("successMsg", success);
            session.removeAttribute("successMsg");
        }
        if (error != null) {
            model.addAttribute("errorMsg", error);
            session.removeAttribute("errorMsg");
        }

        NhanVien nv = getNhanVienFromSession(session);
        if (nv == null) {
            model.addAttribute("errorMsg", "Tài khoản này chưa được liên kết với nhân viên.");
            return "staff-home";
        }

        // toàn bộ lịch của nv
        List<LichHen> all = lichHenRepo.findByNhanVien_Manv(nv.getManv());

        // lịch hôm nay
        List<LichHen> todayAppointments = new ArrayList<>();
        for (LichHen lh : all) {
            if (lh.getNgayHen() != null && lh.getNgayHen().equals(today))
                todayAppointments.add(lh);
        }

        todayAppointments.sort(
                Comparator.comparing(LichHen::getGioHen, Comparator.nullsLast(Comparator.naturalOrder())));

        // map dịch vụ
        Map<Integer, List<LichHenDichVu>> mapDv = new HashMap<>();
        for (LichHen lh : todayAppointments) {
            if (lh.getMaLh() != null)
                mapDv.put(lh.getMaLh(), lhDvRepo.findByLichHen_MaLh(lh.getMaLh()));
        }

        // khách hàng của nv
        Map<Integer, KhachHang> mapKh = new LinkedHashMap<>();
        for (LichHen lh : all) {
            if (lh.getKhachHang() != null)
                mapKh.put(lh.getKhachHang().getMakh(), lh.getKhachHang());
        }

        // thống kê
        long totalToday = todayAppointments.size();
        long totalAll = all.size();

        model.addAttribute("nv", nv);
        model.addAttribute("today", today);
        model.addAttribute("listKhachHang", new ArrayList<>(mapKh.values()));
        model.addAttribute("listLichHenToday", todayAppointments);
        model.addAttribute("mapDichVu", mapDv);
        model.addAttribute("totalToday", totalToday);
        model.addAttribute("totalAll", totalAll);

        return "staff-home";
    }

    // =================== YÊU CẦU ĐỔI CA ===================
    @GetMapping("/shift-request")
    public String shiftRequestForm(
            @RequestParam(name = "currentShift", required = false) String currentShift,
            Model model,
            HttpSession session) {

        String redirect = checkStaffRole(session);
        if (redirect != null)
            return redirect;

        NhanVien nv = getNhanVienFromSession(session);
        if (nv == null) {
            model.addAttribute("errorMsg", "Tài khoản này chưa gắn với nhân viên.");
            return "staff-shift-request";
        }

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("currentShift", currentShift != null ? currentShift : "");
        model.addAttribute("staffName", nv.getHoTen());

        return "staff-shift-request";
    }

    @PostMapping("/shift-request")
    public String submitShiftRequest(
            @RequestParam("date") LocalDate date,
            @RequestParam("currentShift") String currentShift,
            @RequestParam("desiredShift") String desiredShift,
            @RequestParam("reason") String reason,
            HttpSession session) {

        String redirect = checkStaffRole(session);
        if (redirect != null)
            return redirect;

        NhanVien nv = getNhanVienFromSession(session);
        if (nv == null) {
            session.setAttribute("errorMsg", "Tài khoản nhân viên không tồn tại.");
            return "redirect:/staff/home";
        }

        ShiftChangeRequest req = new ShiftChangeRequest(
                nv.getHoTen(),
                date,
                currentShift.trim(),
                desiredShift.trim(),
                reason.trim());

        ShiftChangeRequestStore.add(req);

        session.setAttribute("successMsg", "✔ Đã gửi yêu cầu đổi ca cho Admin.");
        return "redirect:/staff/home";
    }
}
