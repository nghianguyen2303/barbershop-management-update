package com.barbershop.temp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ShiftChangeRequestStore {

    private static final List<ShiftChangeRequest> REQUESTS = new ArrayList<>();
    private static int NEXT_ID = 1;

    // Thêm yêu cầu, tự gán ID tăng dần
    public static synchronized void add(ShiftChangeRequest req) {
        req.setId(NEXT_ID++);
        REQUESTS.add(req);
    }

    // Lấy toàn bộ danh sách (read-only)
    public static synchronized List<ShiftChangeRequest> getAll() {
        return Collections.unmodifiableList(REQUESTS);
    }

    // Xóa tất cả (nếu cần)
    public static synchronized void clear() {
        REQUESTS.clear();
    }

    // Xóa theo id (admin bấm "Đã xử lý")
    public static synchronized void removeById(int id) {
        Iterator<ShiftChangeRequest> it = REQUESTS.iterator();
        while (it.hasNext()) {
            ShiftChangeRequest r = it.next();
            if (r.getId() == id) {
                it.remove();
                break;
            }
        }
    }
}
