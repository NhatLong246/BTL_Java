package view;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;

public class Program {
    public static void main(String[] args) {
        try {
            // Thiết lập giao diện người dùng đẹp hơn
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            // Thiết lập font chung cho toàn bộ ứng dụng
            setUIFont(new Font("Arial", Font.PLAIN, 14));
        } catch (Exception e) {
            System.out.println("Không thể thiết lập giao diện: " + e.getMessage());
        }

        // Kiểm tra kết nối cơ sở dữ liệu
        SwingUtilities.invokeLater(() -> {
            if (!DatabaseConnection.testConnection()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Không thể kết nối đến cơ sở dữ liệu! Hãy kiểm tra cài đặt kết nối.",
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Hiển thị màn hình đăng nhập
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
            System.out.println("Hệ thống quản lý bệnh nhân đã khởi động thành công!");
        });
    }
    
    // Phương thức thiết lập font cho toàn bộ ứng dụng
    private static void setUIFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }
}