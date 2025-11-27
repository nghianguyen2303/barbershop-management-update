package com.barbershop.controller;

import com.barbershop.entity.*;
import com.barbershop.repository.*;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/admin/lichhen")
public class LichHenAdminController {

    @Autowired
    private LichHenRepository lichHenRepo;

    @Autowired
    private LichHenDichVuRepository lhDvRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private NhanVienRepository nvRepo;

    @Autowired
    private DichVuRepository dichVuRepo;

    // =================== UTIL ===================

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

    // ========================= LIST + BỘ LỌC =========================
    @GetMapping
    public String list(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        // Load flash message rồi xóa
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

        // Chuẩn hóa keyword rỗng => null
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // Lấy danh sách theo bộ lọc
        List<LichHen> list = lichHenRepo.searchForAdmin(keyword, fromDate, toDate);

        // Map: maLh -> danh sách dịch vụ
        Map<Integer, List<LichHenDichVu>> mapDv = new HashMap<>();
        for (LichHen lh : list) {
            mapDv.put(
                    lh.getMaLh(),
                    lhDvRepo.findByLichHen_MaLh(lh.getMaLh()));
        }

        model.addAttribute("listLichHen", list);
        model.addAttribute("mapDichVu", mapDv);

        // Đẩy lại filter để hiển thị trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "lichhen-admin-list";
    }

    // ========================= FORM THÊM LỊCH HẸN (ADMIN)
    // =========================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        // Lấy flash error nếu có
        Object error = session.getAttribute("errorAddMsg");
        if (error != null) {
            model.addAttribute("errorMsg", error);
            session.removeAttribute("errorAddMsg");
        }

        model.addAttribute("listKhachHang", khRepo.findAll());
        model.addAttribute("listNhanVien", nvRepo.findAll());
        model.addAttribute("listDichVu", dichVuRepo.findAll());

        return "lichhen-admin-add";
    }

    // ========================= API LẤY GIỜ TRỐNG/BẬN (ADMIN)
    // =========================
    @GetMapping("/api/timeslots")
    @ResponseBody
    public List<Map<String, Object>> apiTimeSlots(
            @RequestParam("manv") int manv,
            @RequestParam("ngay") String ngay,
            @RequestParam(name = "dvIds", required = false) List<Integer> dvIds,
            @RequestParam(name = "excludeId", required = false) Integer excludeId) {

        LocalDate d = LocalDate.parse(ngay); // 2025-11-25

        // Nếu đang sửa lịch hẹn mà không gửi dvIds lên thì lấy từ DB
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
            duration = 20;

        List<LocalTime> slots = generateTimeSlots();

        // Lấy tất cả lịch hẹn của NV trong ngày đó
        List<LichHen> booked = lichHenRepo.findByNhanVien_Manv(manv).stream()
                .filter(lh -> lh.getNgayHen().equals(d))
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (LocalTime slot : slots) {

            LocalTime slotEnd = slot.plusMinutes(duration);
            boolean busy = false;

            for (LichHen lh : booked) {

                // bỏ qua lịch đang sửa
                if (excludeId != null && Objects.equals(lh.getMaLh(), excludeId))
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

    // ========================= XỬ LÝ THÊM LỊCH HẸN (ADMIN)
    // =========================
    @PostMapping("/add")
    @Transactional
    public String add(
            @RequestParam LocalDate ngayHen,
            @RequestParam LocalTime gioHen,
            @RequestParam int makh,
            @RequestParam int manv,
            @RequestParam(name = "dichVuChon", required = false) List<Integer> dvIds,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        // Không cho đặt lịch trong quá khứ
        if (LocalDateTime.of(ngayHen, gioHen).isBefore(LocalDateTime.now())) {
            session.setAttribute("errorAddMsg", "❌ Không thể tạo lịch hẹn trong quá khứ!");
            return "redirect:/admin/lichhen/add";
        }

        KhachHang kh = khRepo.findById(makh).orElse(null);
        NhanVien nv = nvRepo.findById(manv).orElse(null);

        if (kh == null || nv == null) {
            session.setAttribute("errorAddMsg", "❌ Khách hàng hoặc nhân viên không hợp lệ!");
            return "redirect:/admin/lichhen/add";
        }

        // Tạo lịch hẹn
        LichHen lh = new LichHen();
        lh.setNgayHen(ngayHen);
        lh.setGioHen(gioHen);
        lh.setKhachHang(kh);
        lh.setNhanVien(nv);
        lichHenRepo.save(lh);

        // Gắn dịch vụ
        if (dvIds != null) {
            for (Integer dvId : dvIds) {
                DichVu dv = dichVuRepo.findById(dvId).orElse(null);
                if (dv == null)
                    continue;

                LichHenDichVu item = new LichHenDichVu();
                item.setLichHen(lh);
                item.setDichVu(dv);
                lhDvRepo.save(item);
            }
        }

        session.setAttribute("successMsg", "✔ Đã tạo lịch hẹn cho khách hàng " + kh.getHoTen());
        return "redirect:/admin/lichhen";
    }

    // ========================= DELETE =========================
    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable int id, HttpSession session) {

        LichHen lh = lichHenRepo.findById(id).orElse(null);

        if (lh == null) {
            session.setAttribute("errorMsg", "❌ Lịch hẹn không tồn tại!");
            return "redirect:/admin/lichhen";
        }

        // Xóa dịch vụ trước
        lhDvRepo.deleteByLichHen_MaLh(id);

        // Xóa lịch hẹn
        lichHenRepo.deleteById(id);

        session.setAttribute("successMsg", "✔ Đã xóa lịch hẹn thành công!");

        return "redirect:/admin/lichhen";
    }
}
