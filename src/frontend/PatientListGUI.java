package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PatientListGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnLoadData; // Thêm nút tải dữ liệu

    public PatientListGUI() {
        setTitle("Danh sách bệnh nhân");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo bảng
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Thêm cột cho bảng
        model.addColumn("ID");
        model.addColumn("Tên");
        model.addColumn("Ngày sinh");
        model.addColumn("Giới tính");
        model.addColumn("SĐT");
        model.addColumn("Địa chỉ");
        model.addColumn("Tiền sử bệnh");

        // Thêm nút tải dữ liệu
        btnLoadData = new JButton("Tải danh sách");
        btnLoadData.addActionListener(e -> loadPatients());
        add(btnLoadData, BorderLayout.SOUTH);
    }

    // Đưa loadPatients() ra ngoài constructor
    private void loadPatients() {
        String url = "jdbc:mysql://localhost:3306/hospitaldb?autoReconnect=true&useSSL=false&characterEncoding=UTF-8";
        String user = "root";
        String pass = "2005";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT * FROM patients WHERE name LIKE '%Thị%'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // Xóa dữ liệu cũ
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("birthdate"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("medical_history")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientListGUI().setVisible(true));
    }
}
