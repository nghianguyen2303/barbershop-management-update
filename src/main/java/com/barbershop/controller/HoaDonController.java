package com.barbershop.controller;

import com.barbershop.entity.HoaDon;
import com.barbershop.entity.LichHen;
import com.barbershop.entity.LichHenDichVu;
import com.barbershop.repository.HoaDonRepository;
import com.barbershop.repository.LichHenRepository;
import com.barbershop.repository.LichHenDichVuRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// iText
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont; // 🔹 THÊM DÒNG NÀY

@Controller
@RequestMapping("/admin/hoadon")
public class HoaDonController {

    @Autowired
    private HoaDonRepository hoaDonRepo;

    @Autowired
    private LichHenRepository lichHenRepo;

    @Autowired
    private LichHenDichVuRepository lichHenDichVuRepo;

    // ==================== LIST + BỘ LỌC ====================
    @GetMapping
    public String list(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "method", required = false) String method,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        if (keyword != null && keyword.trim().isEmpty())
            keyword = null;
        if (method != null && method.trim().isEmpty())
            method = null;

        List<HoaDon> list = hoaDonRepo.search(keyword, fromDate, toDate, method);

        Map<Integer, List<LichHenDichVu>> mapDv = new HashMap<>();
        for (HoaDon hd : list) {
            if (hd.getLichHen() != null && hd.getLichHen().getMaLh() != null) {
                mapDv.put(
                        hd.getMaHd(),
                        lichHenDichVuRepo.findByLichHen_MaLh(hd.getLichHen().getMaLh()));
            }
        }

        model.addAttribute("listHoaDon", list);
        model.addAttribute("mapDichVu", mapDv);

        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("method", method);

        return "hoadon-list";
    }

    // ==================== ADD FORM ====================
    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        // chỉ cho chọn những lịch hẹn CHƯA có hóa đơn
        List<LichHen> all = lichHenRepo.findAll();
        List<LichHen> chuaCoHoaDon = all.stream()
                .filter(lh -> lh.getMaLh() != null
                        && !hoaDonRepo.existsByLichHen_MaLh(lh.getMaLh()))
                .toList();

        model.addAttribute("hoaDon", new HoaDon());
        model.addAttribute("listLichHen", chuaCoHoaDon);
        return "hoadon-add";
    }

    // ==================== ADD (POST) ====================
    @PostMapping("/add")
    public String add(@ModelAttribute HoaDon hd) {

        if (hd.getLichHen() == null || hd.getLichHen().getMaLh() == null) {
            return "redirect:/admin/hoadon?error=missing_lichhen";
        }

        Integer maLh = hd.getLichHen().getMaLh();

        // ❗ Không cho tạo hóa đơn nếu lịch hẹn này đã có hóa đơn
        if (hoaDonRepo.existsByLichHen_MaLh(maLh)) {
            return "redirect:/admin/hoadon?error=invoice_exists";
        }

        List<LichHenDichVu> ds = lichHenDichVuRepo.findByLichHen_MaLh(maLh);

        hd.tinhTongTien(ds);
        hd.setNgayThanhToan(LocalDate.now());

        hoaDonRepo.save(hd);

        return "redirect:/admin/hoadon";
    }

    // ==================== EDIT FORM ====================
    @GetMapping("/edit/{maHd}")
    public String editForm(@PathVariable int maHd, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        HoaDon hd = hoaDonRepo.findById(maHd).orElse(null);

        model.addAttribute("hoaDon", hd);
        model.addAttribute("listLichHen", lichHenRepo.findAll());
        return "hoadon-edit";
    }

    // ==================== EDIT (POST) ====================
    @PostMapping("/edit")
    public String edit(@ModelAttribute HoaDon hd) {

        HoaDon existing = hoaDonRepo.findById(hd.getMaHd()).orElse(null);
        if (existing == null) {
            return "redirect:/admin/hoadon";
        }

        LichHen lichHen = existing.getLichHen();
        if (lichHen == null || lichHen.getMaLh() == null) {
            return "redirect:/admin/hoadon?error=missing_lichhen";
        }

        Integer maLh = lichHen.getMaLh();

        existing.setPhuongThucTt(hd.getPhuongThucTt());

        List<LichHenDichVu> ds = lichHenDichVuRepo.findByLichHen_MaLh(maLh);
        existing.tinhTongTien(ds);
        existing.setNgayThanhToan(LocalDate.now());

        hoaDonRepo.save(existing);

        return "redirect:/admin/hoadon";
    }

    // ==================== DELETE ====================
    @GetMapping("/delete/{maHd}")
    public String delete(@PathVariable int maHd) {
        hoaDonRepo.deleteById(maHd);
        return "redirect:/admin/hoadon";
    }

    private double tinhTongTienTuDichVu(LichHen lh) {
        if (lh == null || lh.getMaLh() == null)
            return 0d;

        List<LichHenDichVu> dsDv = lichHenDichVuRepo.findByLichHen_MaLh(lh.getMaLh());
        double sum = 0d;
        for (LichHenDichVu item : dsDv) {
            if (item.getDichVu() == null)
                continue;
            Double gia = item.getDichVu().getGia();
            if (gia != null)
                sum += gia;
        }
        return sum;
    }

    // ==================== VIEW DETAIL / IN HÓA ĐƠN (HTML) ====================
    @GetMapping("/view/{maHd}")
    public String viewInvoice(@PathVariable int maHd, Model model) {
        HoaDon hd = hoaDonRepo.findById(maHd).orElse(null);
        if (hd == null) {
            return "redirect:/admin/hoadon";
        }

        LichHen lh = hd.getLichHen();

        var dsDichVu = new java.util.ArrayList<LichHenDichVu>();
        if (hd.getLichHen() != null && hd.getLichHen().getMaLh() != null) {
            dsDichVu = new java.util.ArrayList<>(
                    lichHenDichVuRepo.findByLichHen_MaLh(hd.getLichHen().getMaLh()));
        }

        double tongTienDv = tinhTongTienTuDichVu(lh);

        model.addAttribute("hoaDon", hd);
        model.addAttribute("dsDichVu", dsDichVu);
        model.addAttribute("tongTienDv", tongTienDv);
        return "hoadon-detail";
    }

    // ===========================================================
    // ============= 1) XUẤT MỘT HÓA ĐƠN RA PDF ==================
    // ===========================================================
    @GetMapping("/export-pdf/{maHd}")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable int maHd)
            throws DocumentException, IOException {

        HoaDon hd = hoaDonRepo.findById(maHd).orElse(null);
        if (hd == null) {
            return ResponseEntity.notFound().build();
        }

        List<LichHenDichVu> dsDv = List.of();
        if (hd.getLichHen() != null && hd.getLichHen().getMaLh() != null) {
            dsDv = lichHenDichVuRepo.findByLichHen_MaLh(hd.getLichHen().getMaLh());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        // 🔥 DÙNG ARIAL CÓ SẴN TRONG WINDOWS – HỖ TRỢ TIẾNG VIỆT
        BaseFont bf = BaseFont.createFont(
                "C:/Windows/Fonts/arial.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 16, Font.BOLD);
        Font normalFont = new Font(bf, 11, Font.NORMAL);
        Font boldFont = new Font(bf, 11, Font.BOLD);

        Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ", normalFont));

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngay = hd.getNgayThanhToan() != null ? hd.getNgayThanhToan().format(df) : "";
        String tenKhach = (hd.getLichHen() != null && hd.getLichHen().getKhachHang() != null)
                ? hd.getLichHen().getKhachHang().getHoTen()
                : "---";

        document.add(new Paragraph("Mã hóa đơn: " + hd.getMaHd(), normalFont));
        document.add(new Paragraph("Ngày thanh toán: " + ngay, normalFont));
        document.add(new Paragraph("Khách hàng: " + tenKhach, normalFont));
        document.add(new Paragraph("Phương thức: " +
                (hd.getPhuongThucTt() != null ? hd.getPhuongThucTt() : ""), normalFont));
        document.add(new Paragraph(" ", normalFont));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 4f, 2f, 2f });

        PdfPCell c1 = new PdfPCell(new Phrase("Dịch vụ", boldFont));
        PdfPCell c2 = new PdfPCell(new Phrase("Giá (VND)", boldFont));
        PdfPCell c3 = new PdfPCell(new Phrase("Ghi chú", boldFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);

        double tongTien = 0d;
        for (LichHenDichVu item : dsDv) {
            String tenDv = item.getDichVu() != null ? item.getDichVu().getTenDv() : "";
            Double gia = (item.getDichVu() != null ? item.getDichVu().getGia() : 0d);
            tongTien += (gia != null ? gia : 0d);

            table.addCell(new Phrase(tenDv, normalFont));
            table.addCell(new Phrase(String.format("%.0f", gia != null ? gia : 0d), normalFont));
            table.addCell(new Phrase("", normalFont));
        }

        document.add(table);
        document.add(new Paragraph(" ", normalFont));
        document.add(new Paragraph("Tổng tiền: " +
                String.format("%.0f", tongTien) + " VND", boldFont));

        document.close();

        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "hoadon_" + maHd + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
