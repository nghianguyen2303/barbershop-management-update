package com.barbershop.temp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftChangeRequest {

    private int id; // ID tạm để admin xóa theo yêu cầu
    private String staffName;
    private LocalDate date; // ngày muốn đổi ca
    private String currentShift; // ca hiện tại (text)
    private String desiredShift; // ca muốn đổi (text)
    private String reason; // lý do
    private LocalDateTime createdAt;

    public ShiftChangeRequest() {
    }

    public ShiftChangeRequest(String staffName, LocalDate date,
            String currentShift, String desiredShift,
            String reason) {
        this.staffName = staffName;
        this.date = date;
        this.currentShift = currentShift;
        this.desiredShift = desiredShift;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    // GETTER / SETTER

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCurrentShift() {
        return currentShift;
    }

    public void setCurrentShift(String currentShift) {
        this.currentShift = currentShift;
    }

    public String getDesiredShift() {
        return desiredShift;
    }

    public void setDesiredShift(String desiredShift) {
        this.desiredShift = desiredShift;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
