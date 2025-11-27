package com.barbershop.controller;

import com.barbershop.entity.*;
import com.barbershop.repository.*;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/user/lichhen")
public class LichHenUserController {

    @Autowired
    private LichHenRepository lichHenRepo;
    @Autowired
    private KhachHangRepository khRepo;
    @Autowired
    private NhanVienRepository nvRepo;
    @Autowired
    private DichVuRepository dichVuRepo;
    @Autowired
    private LichHenDichVuRepository lhDvRepo;

    // ========================= UTIL =========================

    private KhachHang getKhachHang(Account acc) {
        return khRepo.findByAccount(acc);
    }

    /** Tạo slot 08:00 → 20:00, mỗi 20 phút */
    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> list = new ArrayList<>();
        LocalTime t = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 0);

        while (!t.isAfter(end)) {
            list.add(t);
            t = t.plusMinutes(20);
        }
        return list;
    }

    /** Tổng thời gian thực hiện của các dịch vụ */
    private int getTotalDuration(List<Integer> dvIds) {
        if (dvIds == null || dvIds.isEmpty())
            return 20;

        return dvIds.stream()
                .map(id -> dichVuRepo.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .mapToInt(DichVu::getThoiGianThucHien)
                .sum();
    }

    // ========================= LIST (CHỈ REDIRECT VỀ HOME)
    // =========================
    @GetMapping
    public String list(HttpSession session) {

        Account acc = (Account) session.getAttribute("user");
        if (acc == null) {
            return "redirect:/login";
        }

        // Danh sách lịch hẹn đã được hiển thị ở trang user-home,
        // nên /user/lichhen chỉ dùng để điều hướng về đó
        return "redirect:/user/home";
    }

    // ========================= ADD FORM =========================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("errorMsg", session.getAttribute("errorAddMsg"));
        session.removeAttribute("errorAddMsg");

        model.addAttribute("listNhanVien", nvRepo.findAll());
        model.addAttribute("listDichVu", dichVuRepo.findAll());

        return "lichhen-add";
    }

    // ========================= API LẤY GIỜ RẢNH/BẬN =========================
    @GetMapping("/api/timeslots")
    @ResponseBody
    public List<Map<String, Object>> apiTimeSlots(
            @RequestParam("manv") int manv,
            @RequestParam("ngay") String ngay,
            @RequestParam(name = "dvIds", required = false) List<Integer> dvIds,
            @RequestParam(name = "excludeId", required = false) Integer excludeId) {

        LocalDate d = LocalDate.parse(ngay); // 2025-11-25

        // Nếu đang sửa lịch hẹn mà không gửi dvIds lên thì lấy lại từ DB
        if ((dvIds == null || dvIds.isEmpty()) && excludeId != null) {
            dvIds = lhDvRepo.findByLichHen_MaLh(excludeId)
                    .stream()
                    .map(x -> x.getDichVu().getMaDv())
                    .toList();
        }

        if (dvIds == null)
            dvIds = new ArrayList<>();

        int duration = getTotalDuration(dvIds);
        if (duration == 0)
            duration = 20; // tối thiểu 20 phút

        List<LocalTime> slots = generateTimeSlots();

        List<LichHen> booked = lichHenRepo.findByNhanVien_Manv(manv).stream()
                .filter(lh -> lh.getNgayHen().equals(d))
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (LocalTime slot : slots) {

            LocalTime slotEnd = slot.plusMinutes(duration);
            boolean busy = false;

            for (LichHen lh : booked) {

                // bỏ qua lịch đang sửa
                if (excludeId != null && lh.getMaLh() == excludeId)
                    continue;

                int usedMinutes = lhDvRepo.findByLichHen_MaLh(lh.getMaLh())
                        .stream()
                        .mapToInt(x -> x.getDichVu().getThoiGianThucHien())
                        .sum();

                if (usedMinutes == 0)
                    usedMinutes = 20;

                LocalTime s = lh.getGioHen();
                LocalTime e = s.plusMinutes(usedMinutes);

                boolean overlap = !(slotEnd.isBefore(s) || slot.isAfter(e));
                if (overlap) {
                    busy = true;
                    break;
                }
            }

            Map<String, Object> row = new HashMap<>();
            row.put("time", slot.toString());
            row.put("busy", busy);
            result.add(row);
        }

        return result;
    }

    // ========================= SAVE =========================
    @PostMapping("/save")
    public String save(
            @RequestParam LocalDate ngayHen,
            @RequestParam LocalTime gioHen,
            @RequestParam int manv,
            @RequestParam(name = "dichVuChon", required = false) List<Integer> dvIds,
            HttpSession session) {

        Account acc = (Account) session.getAttribute("user");
        if (acc == null)
            return "redirect:/login";
        KhachHang kh = getKhachHang(acc);

        if (LocalDateTime.of(ngayHen, gioHen).isBefore(LocalDateTime.now())) {
            session.setAttribute("errorAddMsg", "❌ Không thể đặt lịch trong quá khứ!");
            return "redirect:/user/lichhen/add";
        }

        LichHen lh = new LichHen();
        lh.setNgayHen(ngayHen);
        lh.setGioHen(gioHen);
        lh.setKhachHang(kh);
        lh.setNhanVien(nvRepo.findById(manv).orElse(null));
        lichHenRepo.save(lh);

        if (dvIds != null) {
            for (Integer dv : dvIds) {
                LichHenDichVu item = new LichHenDichVu();
                item.setLichHen(lh);
                item.setDichVu(dichVuRepo.findById(dv).orElse(null));
                lhDvRepo.save(item);
            }
        }

        session.setAttribute("successMsg", "✔ Đặt lịch thành công!");
        return "redirect:/user/home"; // CHỈNH Ở ĐÂY
    }

    // ========================= EDIT FORM =========================
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model, HttpSession session) {

        Account acc = (Account) session.getAttribute("user");
        if (acc == null)
            return "redirect:/login";

        LichHen lh = lichHenRepo.findById(id).orElse(null);
        if (lh == null || lh.getKhachHang().getAccount().getId() != acc.getId()) {
            // nếu không tồn tại hoặc không thuộc KH hiện tại → về Home
            return "redirect:/user/home"; // CHỈNH Ở ĐÂY
        }

        List<Integer> selected = lhDvRepo.findByLichHen_MaLh(id)
                .stream().map(x -> x.getDichVu().getMaDv()).toList();

        model.addAttribute("lichHen", lh);
        model.addAttribute("listNhanVien", nvRepo.findAll());
        model.addAttribute("listDichVu", dichVuRepo.findAll());
        model.addAttribute("selectedDvIds", selected);

        return "lichhen-edit";
    }

    // ========================= UPDATE =========================
    @Transactional
    @PostMapping("/edit")
    public String update(
            @RequestParam int maLh,
            @RequestParam LocalDate ngayHen,
            @RequestParam LocalTime gioHen,
            @RequestParam int nhanVien,
            @RequestParam(name = "dichVuChon", required = false) List<Integer> dvIds,
            HttpSession session) {

        Account acc = (Account) session.getAttribute("user");
        if (acc == null)
            return "redirect:/login";

        LichHen lh = lichHenRepo.findById(maLh).orElse(null);
        if (lh == null)
            return "redirect:/user/home"; // CHỈNH Ở ĐÂY

        lh.setNgayHen(ngayHen);
        lh.setGioHen(gioHen);
        lh.setNhanVien(nvRepo.findById(nhanVien).orElse(null));
        lichHenRepo.save(lh);

        lhDvRepo.deleteByLichHen_MaLh(maLh);

        if (dvIds != null) {
            for (Integer dv : dvIds) {
                LichHenDichVu ldv = new LichHenDichVu();
                ldv.setLichHen(lh);
                ldv.setDichVu(dichVuRepo.findById(dv).orElse(null));
                lhDvRepo.save(ldv);
            }
        }

        session.setAttribute("successMsg", "✔ Cập nhật thành công!");
        return "redirect:/user/home"; // CHỈNH Ở ĐÂY
    }

    // ========================= DELETE =========================
    @Transactional
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {

        Account acc = (Account) session.getAttribute("user");
        if (acc == null)
            return "redirect:/login";

        LichHen lh = lichHenRepo.findById(id).orElse(null);

        if (lh == null || lh.getKhachHang().getAccount().getId() != acc.getId())
            return "redirect:/user/home"; // CHỈNH Ở ĐÂY

        lhDvRepo.deleteByLichHen_MaLh(id);
        lichHenRepo.deleteById(id);

        session.setAttribute("successMsg", "✔ Đã xóa lịch hẹn!");
        return "redirect:/user/home"; // CHỈNH Ở ĐÂY
    }
}
