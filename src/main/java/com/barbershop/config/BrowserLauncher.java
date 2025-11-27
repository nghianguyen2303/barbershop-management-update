package com.barbershop.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BrowserLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:8081/login";

        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Windows
                new ProcessBuilder("cmd", "/c", "start", url).start();
            } else if (os.contains("mac")) {
                // macOS
                new ProcessBuilder("open", url).start();
            } else {
                // Linux (xdg-open)
                new ProcessBuilder("xdg-open", url).start();
            }

        } catch (Exception e) {
            System.out.println("⚠ Không thể tự mở trình duyệt: " + e.getMessage());
        }
    }
}
