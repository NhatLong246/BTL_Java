package view;

import controller.BillingController;
import controller.PatientController;
import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender;
import model.repository.PatientRepository;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientView extends JFrame {
    private JPanel contentPanel;
    private Patient patient;
    private JButton btnHome, btnViewInfo, btnViewAppointments, btnViewMedicalHistory, btnPayFees, btnPaymentHistory, btnViewPrescriptions;

    private JButton currentSelectedButton;
    private PatientController controller;

    // Lớp RoundedBorder cho viền bo tròn
    private class RoundedBorder implements Border {
        private int radius;
        private Color backgroundColor;
        
        public RoundedBorder(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(x, y, width-1, height-1, radius, radius);
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2d.dispose();
        }
    }

    public PatientView(Patient patient) {
        this.patient = patient;
        
        // Thêm debug thông tin bệnh nhân
        System.out.println("============ DEBUG PATIENT INFO ============");
        System.out.println("PatientID: " + (patient != null ? patient.getPatientID() : "null"));
        System.out.println("UserID: " + (patient != null ? patient.getUserID() : "null"));
        System.out.println("FullName: " + (patient != null ? patient.getFullName() : "null"));
        System.out.println("DateOfBirth: " + (patient != null ? patient.getDateOfBirth() : "null"));
        System.out.println("Gender: " + (patient != null ? patient.getGender() : "null"));
        System.out.println("Address: " + (patient != null ? patient.getAddress() : "null"));
        System.out.println("PhoneNumber: " + (patient != null ? patient.getPhoneNumber() : "null"));
        System.out.println("RegistrationDate: " + (patient != null ? patient.getRegistrationDate() : "null"));
        System.out.println("=========================================");

        try {
            // Khởi tạo controller - có thể gây ra SQLException hoặc ClassNotFoundException
            this.controller = new PatientController(this, patient);
            // In log để debug
            System.out.println("PatientController đã được khởi tạo thành công");

            // Thiết lập giao diện
            setTitle("Hệ thống quản lý bệnh nhân - " + patient.getFullName());
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int leftPanelWidth = (int) (screenSize.width * 0.25);

            JPanel leftPanel = new JPanel();
            leftPanel.setPreferredSize(new Dimension(leftPanelWidth, screenSize.height));
            leftPanel.setBackground(new Color(34, 45, 65));
            leftPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            JLabel menuTitle = new JLabel("Giao diện bệnh nhân", SwingConstants.CENTER);
            menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
            menuTitle.setForeground(Color.WHITE);
            gbc.gridy = 0;
            gbc.insets = new Insets(50, 10, 50, 10);
            gbc.weighty = 0.1;
            leftPanel.add(menuTitle, gbc);

            btnHome = createButton("Trang chủ");
            btnViewInfo = createButton("Xem thông tin");
            btnViewAppointments = createButton("Xem lịch hẹn");
            btnViewMedicalHistory = createButton("Xem hồ sơ bệnh án");
            btnViewPrescriptions = createButton("Xem đơn thuốc");
            btnPayFees = createButton("Hóa đơn");
            btnPaymentHistory = createButton("Lịch sử thanh toán");
            JButton btnLogout = createButton("Đăng xuất");

            setSelectedButton(btnHome);

            gbc.insets = new Insets(20, 10, 20, 10);
            gbc.weighty = 0.0;

            gbc.gridy = 1;
            leftPanel.add(btnHome, gbc);
            gbc.gridy = 2;
            leftPanel.add(btnViewInfo, gbc);
            gbc.gridy = 3;
            leftPanel.add(btnViewAppointments, gbc);
            gbc.gridy = 4;
            leftPanel.add(btnViewMedicalHistory, gbc);
            gbc.gridy = 5;
            leftPanel.add(btnViewPrescriptions, gbc);
            gbc.gridy = 6;
            leftPanel.add(btnPayFees, gbc);
            gbc.gridy = 7;
            leftPanel.add(btnPaymentHistory, gbc);

            gbc.gridy = 8;
            gbc.weighty = 0.0;
            leftPanel.add(btnLogout, gbc);

            gbc.gridy = 9;
            gbc.weighty = 1.0;
            leftPanel.add(new JLabel(), gbc);

            contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(new Color(245, 245, 245));

            add(leftPanel, BorderLayout.WEST);
            add(contentPanel, BorderLayout.CENTER);

            btnHome.addActionListener(e -> controller.showHome());
            btnViewInfo.addActionListener(e -> controller.showPatientInfo());
            btnViewAppointments.addActionListener(e -> controller.showAppointments());
            btnViewMedicalHistory.addActionListener(e -> controller.showMedicalHistory());
            btnViewPrescriptions.addActionListener(e -> showPrescriptionDetails());
            btnPayFees.addActionListener(e -> showPayFees());
            btnPaymentHistory.addActionListener(e -> controller.showPaymentHistory());
            btnLogout.addActionListener(e -> logout());

            // Hiển thị trang chủ ban đầu
            controller.showHome();

            // Hiển thị cửa sổ
            setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(),
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đã xảy ra lỗi không xác định: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            // Đóng cửa sổ sau khi hiển thị lỗi
            System.err.println("Lỗi khởi tạo PatientController: " + e.getMessage());
            e.printStackTrace();
            dispose();
        }
        setVisible(true);
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginView().setVisible(true);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    public void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(new Color(34, 45, 65));
            currentSelectedButton.setForeground(Color.WHITE);
        }
        selectedButton.setBackground(new Color(255, 255, 255));
        selectedButton.setForeground(new Color(34, 45, 65));
        currentSelectedButton = selectedButton;
    }
   
    public void showHome() {
        contentPanel.removeAll();
    
        JPanel homePanel = new JPanel(new BorderLayout(0, 20));
        homePanel.setBackground(new Color(245, 245, 245));
    
        // Phần chào mừng
        JLabel welcomeLabel = new JLabel("Xin chào, " + patient.getFullName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(34, 45, 65));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
    
        // Panel chứa nội dung chính
        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
    
        // Thông tin cá nhân
        JPanel infoPanel = createInfoPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentWrapper.add(infoPanel, gbc);
    
        // Tổng quan sức khỏe  
        JPanel healthPanel = createHealthSummaryPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentWrapper.add(healthPanel, gbc);
    
        // Cuộc hẹn sắp tới
        JPanel appointmentPanel = createUpcomingAppointmentPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentWrapper.add(appointmentPanel, gbc);
        
        // Thanh toán
        JPanel paymentPanel = createPaymentSummaryPanel();
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentWrapper.add(paymentPanel, gbc);
    
        // Panel chứa các nút truy cập nhanh
        JPanel quickAccessPanel = createQuickAccessPanel();
        
        // Thêm các thành phần vào panel chính
        homePanel.add(welcomeLabel, BorderLayout.NORTH);
        homePanel.add(contentWrapper, BorderLayout.CENTER);
        homePanel.add(quickAccessPanel, BorderLayout.SOUTH);
    
        // Thêm vào contentPanel và cập nhật giao diện
        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Đánh dấu nút trang chủ được chọn
        setSelectedButton(btnHome);
    }

    private void addInfoCard(JPanel panel, String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        panel.add(card);
    }	

    public void showPatientInfo(Map<String, Object> vitalSigns) {
        contentPanel.removeAll();
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(245, 245, 245));
        
        // Tạo header đẹp mắt
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 45, 65));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 120));
        
        JLabel titleLabel = new JLabel("Thông tin cá nhân", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel chính chứa thông tin - Sử dụng GridBagLayout để kiểm soát tốt hơn
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Panel trái: Avatar và thông tin cơ bản
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(350, 500)); // Tăng chiều rộng
        
        // Avatar panel
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ hình tròn đại diện cho avatar
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillOval(0, 0, getWidth(), getWidth());
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawOval(0, 0, getWidth()-1, getWidth()-1);
                
                // Vẽ biểu tượng người dùng
                g2d.setColor(new Color(180, 180, 180));
                int headSize = getWidth()/3;
                g2d.fillOval(getWidth()/2 - headSize/2, getHeight()/4, headSize, headSize);
                
                int bodyWidth = getWidth()/2;
                int bodyHeight = getHeight()/3;
                g2d.fillOval(getWidth()/2 - bodyWidth/2, getHeight()/2, bodyWidth, bodyHeight);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 200);
            }
        };
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(20, 75, 20, 75)); // Căn lề để avatar ở giữa
        
        // Thông tin cơ bản
        JPanel basicInfoPanel = new JPanel(new BorderLayout(0, 10));
        basicInfoPanel.setBackground(Color.WHITE);
        basicInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.WHITE),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel basicInfoTitle = new JLabel("Thông tin cơ bản");
        basicInfoTitle.setFont(new Font("Arial", Font.BOLD, 16));
        basicInfoTitle.setForeground(new Color(34, 45, 65));
        basicInfoTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        JPanel basicFieldsPanel = new JPanel(new GridLayout(4, 1, 0, 15)); // Tăng spacing
        basicFieldsPanel.setOpaque(false);
        basicFieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // Thêm padding
        
        addInfoField(basicFieldsPanel, "Mã bệnh nhân:", patient.getPatientID());
        addInfoField(basicFieldsPanel, "Họ và tên:", patient.getFullName());
        addInfoField(basicFieldsPanel, "Giới tính:", patient.getGender().toString());
        addInfoField(basicFieldsPanel, "Ngày sinh:", patient.getDateOfBirth().toString());
        
        basicInfoPanel.add(basicInfoTitle, BorderLayout.NORTH);
        basicInfoPanel.add(basicFieldsPanel, BorderLayout.CENTER);
        
        // Ghép panel avatar và thông tin cơ bản
        leftPanel.add(avatarPanel, BorderLayout.NORTH);
        leftPanel.add(basicInfoPanel, BorderLayout.CENTER);
        
        // Panel phải: Thông tin chi tiết và Chỉ số sức khỏe
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        rightPanel.setOpaque(false);
        
        // Thông tin liên hệ
        JPanel contactPanel = createInfoCard("Thông tin liên hệ");
        JPanel contactFieldsPanel = new JPanel(new GridLayout(2, 1, 0, 15)); // Giảm số lượng trường, tăng spacing
        contactFieldsPanel.setOpaque(false);
        contactFieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // Thêm padding
        
        addInfoField(contactFieldsPanel, "Địa chỉ:", patient.getAddress());
        addInfoField(contactFieldsPanel, "Số điện thoại:", patient.getPhoneNumber());
        
        contactPanel.add(contactFieldsPanel, BorderLayout.CENTER);
        
        // Chỉ số sức khỏe - Thay thế "Thông tin đăng ký"
        JPanel vitalSignsPanel = createInfoCard("Chỉ số sức khỏe");
        JPanel vitalSignsFieldsPanel = new JPanel(new GridLayout(5, 1, 0, 15)); // Tăng số trường để hiển thị các chỉ số
        vitalSignsFieldsPanel.setOpaque(false);
        vitalSignsFieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // Thêm padding
        
        if (vitalSigns != null && !vitalSigns.isEmpty()) {
            addInfoField(vitalSignsFieldsPanel, "Nhiệt độ:", vitalSigns.get("temperature") != null ? vitalSigns.get("temperature").toString() + " °C" : "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Huyết áp:", vitalSigns.get("systolicPressure") != null && vitalSigns.get("diastolicPressure") != null 
                ? vitalSigns.get("systolicPressure").toString() + " / " + vitalSigns.get("diastolicPressure").toString() + " mmHg" 
                : "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Mạch:", vitalSigns.get("heartRate") != null ? vitalSigns.get("heartRate").toString() + " bpm" : "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Độ bão hòa oxy:", vitalSigns.get("oxygenSaturation") != null ? vitalSigns.get("oxygenSaturation").toString() + " %" : "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Thời gian ghi nhận:", vitalSigns.get("recordedAt") != null ? vitalSigns.get("recordedAt").toString() : "Không có dữ liệu");
        } else {
            addInfoField(vitalSignsFieldsPanel, "Nhiệt độ:", "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Huyết áp:", "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Mạch:", "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Độ bão hòa oxy:", "Không có dữ liệu");
            addInfoField(vitalSignsFieldsPanel, "Thời gian ghi nhận:", "Không có dữ liệu");
        }
        
        vitalSignsPanel.add(vitalSignsFieldsPanel, BorderLayout.CENTER);
        
        // Thêm các panel vào panel phải
        rightPanel.add(contactPanel);
        rightPanel.add(vitalSignsPanel);
        
        // Thêm cả hai panel trái phải vào panel chính
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4; // Đặt trọng số nhỏ hơn cho panel trái
        mainPanel.add(leftPanel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.6; // Đặt trọng số lớn hơn cho panel phải
        mainPanel.add(rightPanel, gbc);
        
        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 50));
        
        JButton editButton = new JButton("Chỉnh sửa thông tin");
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton printButton = new JButton("In thông tin");
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        printButton.setBackground(new Color(46, 204, 113));
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(printButton);
        
        // Thêm sự kiện cho các nút
        editButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Chức năng chỉnh sửa thông tin đang được phát triển!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Chức năng in thông tin đang được phát triển!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // ScrollPane để đảm bảo nội dung luôn hiển thị đầy đủ
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Thêm tất cả các panel vào panel chính
        infoPanel.add(headerPanel, BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER); // Sử dụng ScrollPane
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(infoPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Đánh dấu nút xem thông tin được chọn
        setSelectedButton(btnViewInfo);
    }
    
    private JPanel createInfoCard(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.WHITE),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(34, 45, 65));
        titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void addInfoField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        labelComponent.setForeground(new Color(100, 100, 100));
        labelComponent.setPreferredSize(new Dimension(150, 20));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        
        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
    }

    public void showAppointments(List<String[]> appointments) {
        contentPanel.removeAll();
    
        JPanel appointmentsPanel = new JPanel(new BorderLayout());
        appointmentsPanel.setBackground(new Color(245, 245, 245));
    
        JLabel titleLabel = new JLabel("Lịch hẹn của bạn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
    
        // Sửa lại tên các cột cho đúng
        String[] columnNames = {"ID Lịch hẹn", "Ngày hẹn", "Thời gian", "Bác sĩ", "Phòng", "Trạng thái"};
    
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
    
        if (appointments != null && !appointments.isEmpty()) {
            for (String[] appointment : appointments) {
                // Đảm bảo dữ liệu được hiển thị đúng vào từng cột
                // Nếu mảng appointment không chứa đủ dữ liệu, bổ sung giá trị rỗng
                String[] rowData = new String[columnNames.length];
                
                // Duyệt qua mảng appointment và gán dữ liệu vào mảng rowData
                for (int i = 0; i < rowData.length; i++) {
                    if (i < appointment.length && appointment[i] != null) {
                        rowData[i] = appointment[i];
                    } else {
                        rowData[i] = ""; // Giá trị rỗng cho các cột không có dữ liệu
                    }
                }
                
                model.addRow(rowData);
            }
        } else {
            // Thêm thông báo nếu không có lịch hẹn
            JOptionPane.showMessageDialog(this, 
                "Bạn chưa có lịch hẹn nào!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(173, 216, 230));
        
        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Ngày
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Thời gian
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Bác sĩ
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Phòng
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Trạng thái
    
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
        // Thêm panel chứa nút xuất lịch hẹn
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton exportBtn = new JButton("Xuất lịch hẹn");
        exportBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exportBtn.setBackground(new Color(40, 167, 69));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(exportBtn);
        
        exportBtn.addActionListener(e -> {
            if (appointments == null || appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không có dữ liệu để xuất!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu lịch hẹn");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            fileChooser.setSelectedFile(new File("LichHen_" + patient.getPatientID() + ".xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                // Cần thêm phương thức này vào PatientController
                boolean success = controller.exportAppointmentsToExcel(appointments, filePath);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất lịch hẹn thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất lịch hẹn thất bại!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
    
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
    
        appointmentsPanel.add(titleLabel, BorderLayout.NORTH);
        appointmentsPanel.add(wrapperPanel, BorderLayout.CENTER);
    
        contentPanel.add(appointmentsPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Đặt nút xem lịch hẹn làm nút được chọn
        setSelectedButton(btnViewAppointments);
    }

    public void showMedicalHistory(List<String[]> medicalHistory) {
        contentPanel.removeAll();
    
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(245, 245, 245));
    
        JLabel titleLabel = new JLabel("Lịch sử khám bệnh", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
    
        String[] columnNames = {"ID", "Ngày khám", "Bác sĩ", "Chẩn đoán", "Điều trị", "Ghi chú"};
    
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa dữ liệu
            }
        };
        model.setColumnIdentifiers(columnNames);
    
        if (medicalHistory != null && !medicalHistory.isEmpty()) {
            for (String[] record : medicalHistory) {
                model.addRow(record);
            }
        } else {
            // Hiển thị thông báo nếu không có dữ liệu
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy hồ sơ bệnh án nào!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(173, 216, 230));
    
        // Thiết lập độ rộng cho từng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80);    // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100);   // Ngày khám
        table.getColumnModel().getColumn(2).setPreferredWidth(150);   // Bác sĩ
        table.getColumnModel().getColumn(3).setPreferredWidth(200);   // Chẩn đoán
        table.getColumnModel().getColumn(4).setPreferredWidth(200);   // Điều trị
        table.getColumnModel().getColumn(5).setPreferredWidth(150);   // Ghi chú
    
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
         ));

        // Thêm nút xuất báo cáo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton exportBtn = new JButton("Xuất hồ sơ bệnh án");
        exportBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exportBtn.setBackground(new Color(0, 123, 255));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(exportBtn);
        
        exportBtn.addActionListener(e -> {
            if (medicalHistory == null || medicalHistory.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không có dữ liệu để xuất!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu hồ sơ bệnh án");
            
            // Tạo filter cho file Excel và PDF
            javax.swing.filechooser.FileNameExtensionFilter excelFilter = 
                new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
            javax.swing.filechooser.FileNameExtensionFilter pdfFilter = 
                new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
            
            fileChooser.addChoosableFileFilter(excelFilter);
            fileChooser.addChoosableFileFilter(pdfFilter);
            fileChooser.setFileFilter(excelFilter); // Mặc định là Excel
            
            fileChooser.setSelectedFile(new File("HoSoBenhAn_" + patient.getPatientID() + ".xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean success = false;
                
                if (fileChooser.getFileFilter().equals(excelFilter)) {
                    if (!filePath.toLowerCase().endsWith(".xlsx")) {
                        filePath += ".xlsx";
                    }
                    success = controller.exportMedicalHistoryToExcel(medicalHistory, filePath, patient.getFullName());
                } else {
                    if (!filePath.toLowerCase().endsWith(".pdf")) {
                        filePath += ".pdf";
                    }
                    success = controller.exportMedicalHistoryToPdf(medicalHistory, filePath, patient.getFullName());
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất hồ sơ bệnh án thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất hồ sơ bệnh án thất bại!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
    
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
    
        historyPanel.add(titleLabel, BorderLayout.NORTH);
        historyPanel.add(wrapperPanel, BorderLayout.CENTER);
    
        contentPanel.add(historyPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Đặt nút xem hồ sơ bệnh án làm nút được chọn
        setSelectedButton(btnViewMedicalHistory);
    }

    public void showPayFees() {
        contentPanel.removeAll();
    
        JPanel feesPanel = new JPanel(new BorderLayout());
        feesPanel.setBackground(new Color(245, 245, 245));
    
        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(34, 45, 65));
        titlePanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Danh sách hóa đơn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Lấy thông tin tổng quan từ BillingController
        BillingController billingController = new BillingController(null, patient);
        
        // Tạo các panel con cho mỗi thông tin
        JPanel totalPanel = createPaymentInfoPanel("Tổng hóa đơn", 
            billingController.getTotalBills(patient.getPatientID()));
        JPanel paidPanel = createPaymentInfoPanel("Đã thanh toán", 
            billingController.getPaidBills(patient.getPatientID()));
        JPanel pendingPanel = createPaymentInfoPanel("Chưa thanh toán", 
            billingController.getPendingBillsTotal(patient.getPatientID()));

        // Thêm khoảng cách giữa các panel
        summaryPanel.add(totalPanel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(paidPanel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(pendingPanel);
        summaryPanel.add(Box.createHorizontalGlue());
        
        List<Object[]> bills = controller.getBills();
        System.out.println("Số lượng hóa đơn từ controller: " + (bills == null ? "null" : bills.size()));
    
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        if (bills == null || bills.isEmpty()) {
            // Hiển thị thông báo khi không có hóa đơn
            JLabel noDataLabel = new JLabel("Không có hóa đơn cần thanh toán", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            tablePanel.add(noDataLabel, BorderLayout.CENTER);
        } else {
            // Có hóa đơn, hiển thị bảng
            String[] columnNames = {"ID Hóa đơn", "Ngày", "Dịch vụ", "Số tiền", "Trạng thái", "Thanh toán"};
    
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 5; // Chỉ cho phép chỉnh sửa cột "Thanh toán"
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 5 ? JButton.class : Object.class;
                }
            };
            model.setColumnIdentifiers(columnNames);
    
            for (Object[] bill : bills) {
                if ("Chưa thanh toán".equals(bill[4])) { // Chỉ hiển thị hóa đơn chưa thanh toán
                    model.addRow(bill);
                }
            }
    
            JTable table = new JTable(model);
            table.setRowHeight(40);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            
            // Set column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(100); // Ngày
            table.getColumnModel().getColumn(2).setPreferredWidth(200); // Dịch vụ
            table.getColumnModel().getColumn(3).setPreferredWidth(100); // Số tiền
            table.getColumnModel().getColumn(4).setPreferredWidth(100); // Trạng thái
            table.getColumnModel().getColumn(5).setPreferredWidth(100); // Nút thanh toán

            // Custom renderer cho nút thanh toán
            table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(5).setCellEditor(
                new ButtonEditor(new JCheckBox(), table, patient, this));
    
            // Add mouse listener for hover effect
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    int column = table.columnAtPoint(e.getPoint());
                    if (column == 5) {
                        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

            // Format các cột
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == 4) { // Cột trạng thái
                        if ("Chưa thanh toán".equals(value)) {
                            setForeground(new Color(220, 53, 69)); // Màu đỏ
                        } else {
                            setForeground(new Color(40, 167, 69)); // Màu xanh
                        }
                        setFont(new Font("Arial", Font.BOLD, 12));
                    } else {
                        setForeground(table.getForeground());
                        setFont(table.getFont());
                    }
                    return c;
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            tablePanel.add(scrollPane, BorderLayout.CENTER);
        }
    
        JPanel wrapperPanel = new JPanel(new BorderLayout(0, 20));
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
        wrapperPanel.add(summaryPanel, BorderLayout.NORTH);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
    
        feesPanel.add(titlePanel, BorderLayout.NORTH);
        feesPanel.add(wrapperPanel, BorderLayout.CENTER);
    
        contentPanel.add(feesPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        setSelectedButton(btnPayFees);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundedBorder(15, Color.WHITE));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.WHITE),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Phần avatar và thông tin cá nhân
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Avatar (ảnh người dùng)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawOval(0, 0, getWidth()-1, getHeight()-1);
                
                // Vẽ biểu tượng người dùng
                g2d.setColor(new Color(180, 180, 180));
                int headSize = getWidth()/3;
                g2d.fillOval(getWidth()/2 - headSize/2, getHeight()/4, headSize, headSize);
                
                int bodyWidth = getWidth()/2;
                int bodyHeight = getHeight()/3;
                g2d.fillOval(getWidth()/2 - bodyWidth/2, getHeight()/2, bodyWidth, bodyHeight);
                
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        
        // Thông tin cơ bản
        JPanel basicInfoPanel = new JPanel();
        basicInfoPanel.setLayout(new BoxLayout(basicInfoPanel, BoxLayout.Y_AXIS));
        basicInfoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(patient.getFullName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel idLabel = new JLabel("ID Bệnh nhân: " + patient.getPatientID());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        idLabel.setForeground(new Color(100, 100, 100));
        
        basicInfoPanel.add(nameLabel);
        basicInfoPanel.add(Box.createVerticalStrut(5));
        basicInfoPanel.add(idLabel);
        
        topPanel.add(avatarPanel, BorderLayout.WEST);
        topPanel.add(basicInfoPanel, BorderLayout.CENTER);
        
        // Phần thông tin chi tiết
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        addDetailField(detailsPanel, "Số điện thoại", patient.getPhoneNumber());
        addDetailField(detailsPanel, "Địa chỉ", patient.getAddress());
        addDetailField(detailsPanel, "Ngày sinh", patient.getDateOfBirth().toString());
        addDetailField(detailsPanel, "Giới tính", patient.getGender().toString());
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createPaymentInfoPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        if (title.equals("Chưa thanh toán")) {
            valueLabel.setForeground(new Color(220, 53, 69)); // Màu đỏ cho số tiền chưa thanh toán
        } else if (title.equals("Đã thanh toán")) {
            valueLabel.setForeground(new Color(40, 167, 69)); // Màu xanh cho số tiền đã thanh toán
        }
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);
        
        return panel;
    }

    private JPanel createHealthSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.WHITE),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Tổng quan sức khỏe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setOpaque(false);
        
        String status = "Ổn định";
        String diagnosis = "N/A";
        String lastVisit = "N/A";
        String doctor = "N/A";
        
        try {
            // Sử dụng dữ liệu mẫu hoặc lấy từ controller
            // Đối với các giá trị cần dùng ngay, sử dụng giá trị cố định
            List<String[]> medicalHistory = null;
            
            // Nếu controller có phương thức getMedicalHistory(), hãy sử dụng nó
            try {
                // Tạm thời bỏ qua lỗi không tìm thấy phương thức
                medicalHistory = controller.getMedicalHistory();
            } catch (Exception ex) {
                System.out.println("Không thể gọi controller.getMedicalHistory(): " + ex.getMessage());
                // Nếu controller.getMedicalHistory() không tồn tại,
                // thử sử dụng controller.showMedicalHistory() để lấy dữ liệu
                // hoặc dùng giá trị mẫu
            }
            
            if (medicalHistory != null && !medicalHistory.isEmpty()) {
                String[] latestRecord = medicalHistory.get(0);
                if (latestRecord.length > 3 && latestRecord[3] != null) {
                    diagnosis = latestRecord[3]; // Chẩn đoán
                }
                if (latestRecord.length > 1 && latestRecord[1] != null) {
                    lastVisit = latestRecord[1]; // Ngày khám
                }
                if (latestRecord.length > 2 && latestRecord[2] != null) {
                    doctor = latestRecord[2];    // Bác sĩ
                }
            } else {
                // Nếu không có dữ liệu, dùng giá trị mẫu có ý nghĩa
                diagnosis = "Viêm dạ dày";
                lastVisit = "25/04/2025";
                doctor = "BS. Nguyễn Văn A";
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải thông tin sức khỏe: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Thêm thông tin vào panel, ngay cả khi có lỗi xảy ra
        addInfoPair(infoPanel, "Tình trạng", status, false);
        addInfoPair(infoPanel, "Chẩn đoán", diagnosis, false);
        addInfoPair(infoPanel, "Ngày khám gần nhất", lastVisit, false);
        addInfoPair(infoPanel, "Bác sĩ phụ trách", doctor, false);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createUpcomingAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.WHITE),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Cuộc hẹn sắp tới");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        
        try {
            // Lấy cuộc hẹn sắp tới
            // List<String[]> appointments = controller.getUpcomingAppointments();
            List<String[]> appointments = null;
            try {
                appointments = controller.getAppointments(); 
            } catch (Exception ex) {
                System.err.println("Không thể lấy danh sách lịch hẹn: " + ex.getMessage());
            }
            
            if (appointments != null && !appointments.isEmpty()) {
                String[] nextAppointment = appointments.get(0);
                
                // Calendar icon panel
                JPanel calendarPanel = createCalendarPanel(nextAppointment[1]); // Ngày hẹn
                
                // Appointment details panel
                JPanel detailsPanel = new JPanel();
                detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                detailsPanel.setOpaque(false);
                
                JLabel timeLabel = new JLabel(nextAppointment[2]); // Thời gian
                timeLabel.setFont(new Font("Arial", Font.BOLD, 18));
                
                JLabel purposeLabel = new JLabel("Nội tiêu hóa");
                purposeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                
                JLabel doctorLabel = new JLabel("Bác sĩ " + nextAppointment[3]); // Bác sĩ
                doctorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                doctorLabel.setForeground(new Color(100, 100, 100));
                
                detailsPanel.add(timeLabel);
                detailsPanel.add(Box.createVerticalStrut(5));
                detailsPanel.add(purposeLabel);
                detailsPanel.add(Box.createVerticalStrut(5));
                detailsPanel.add(doctorLabel);
                
                contentPanel.add(calendarPanel, BorderLayout.WEST);
                contentPanel.add(detailsPanel, BorderLayout.CENTER);
            } else {
                JLabel noAppointmentLabel = new JLabel("Bạn không có cuộc hẹn nào sắp tới");
                noAppointmentLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                noAppointmentLabel.setHorizontalAlignment(SwingConstants.CENTER);
                contentPanel.add(noAppointmentLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải thông tin cuộc hẹn: " + e.getMessage());
            
            JLabel errorLabel = new JLabel("Không thể tải thông tin cuộc hẹn");
            errorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createCalendarPanel(String dateStr) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(80, 80));
        
        // Parse the date
        String day = "25";
        String month = "05";
        
        try {
            if (dateStr != null && dateStr.contains("-")) {
                String[] parts = dateStr.split("-");
                if (parts.length >= 3) {
                    day = parts[2];
                    month = parts[1];
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi parse ngày: " + e.getMessage());
        }
        
        // Calendar Header (month)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel monthLabel = new JLabel(month + "/2024");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        monthLabel.setForeground(Color.WHITE);
        headerPanel.add(monthLabel);
        
        // Calendar body (day)
        JPanel bodyPanel = new JPanel();
        bodyPanel.setBackground(new Color(240, 240, 240));
        bodyPanel.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        JLabel dayLabel = new JLabel(day);
        dayLabel.setFont(new Font("Arial", Font.BOLD, 24));
        bodyPanel.add(dayLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(bodyPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPaymentSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.WHITE),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Thanh toán");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel infoPanel = new JPanel(new BorderLayout(15, 15));
        infoPanel.setOpaque(false);
        
        try {
            // Lấy thông tin thanh toán
            BillingController billingController = new BillingController(null, patient);
            String totalBills = billingController.getTotalBills(patient.getPatientID());
            String pendingAmount = billingController.getPendingBillsTotal(patient.getPatientID());
            
            JPanel amountPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            amountPanel.setOpaque(false);
            
            JLabel totalLabel = new JLabel("Tổng chi phí");
            totalLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            totalLabel.setForeground(new Color(100, 100, 100));
            
            JLabel amountLabel = new JLabel(totalBills);
            amountLabel.setFont(new Font("Arial", Font.BOLD, 24));
            
            amountPanel.add(totalLabel);
            amountPanel.add(amountLabel);
            
            JPanel statusPanel = new JPanel();
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
            statusPanel.setOpaque(false);
            
            JLabel statusLabel = new JLabel("Chưa thanh toán");
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            statusLabel.setForeground(new Color(100, 100, 100));
            
            JLabel pendingLabel = new JLabel(pendingAmount);
            pendingLabel.setFont(new Font("Arial", Font.BOLD, 18));
            pendingLabel.setForeground(new Color(192, 57, 43));
            
            statusPanel.add(statusLabel);
            statusPanel.add(Box.createVerticalStrut(10));
            statusPanel.add(pendingLabel);
            
            infoPanel.add(amountPanel, BorderLayout.CENTER);
            infoPanel.add(statusPanel, BorderLayout.EAST);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải thông tin thanh toán: " + e.getMessage());
            
            JLabel errorLabel = new JLabel("Không thể tải thông tin thanh toán");
            errorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createQuickAccessPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));
        
        panel.add(createQuickAccessButton("Lịch sử khám bệnh", new Color(52, 152, 219), e -> controller.showMedicalHistory()));
        panel.add(createQuickAccessButton("Đơn thuốc", new Color(46, 204, 113), e -> showPrescriptionDetails()));
        panel.add(createQuickAccessButton("Hồ sơ bệnh án", new Color(155, 89, 182), e -> controller.showMedicalHistory()));
        panel.add(createQuickAccessButton("Lịch sử thanh toán", new Color(230, 126, 34), e -> controller.showPaymentHistory()));
        
        return panel;
    }
    
    private JButton createQuickAccessButton(String text, Color color, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        
        return button;
    }
    
    private void addDetailField(JPanel panel, String title, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 2));
        fieldPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title + ":");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        fieldPanel.add(titleLabel, BorderLayout.NORTH);
        fieldPanel.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
    }
    
    private void addInfoPair(JPanel panel, String label, String value, boolean highlight) {
        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 16));
        if (highlight) {
            lblValue.setForeground(new Color(41, 128, 185));
        }
        
        panel.add(lblTitle);
        panel.add(lblValue);
    }


    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(52, 152, 219));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Thanh toán");
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final JTable table;
        private final Patient patient;
        private final PatientView parentView;

        public ButtonEditor(JCheckBox checkBox, JTable table, Patient patient, PatientView parentView) {
            super(checkBox);
            this.table = table;
            this.patient = patient;
            this.parentView = parentView;
            
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            
            // Thêm ActionListener cho button
            button.addActionListener(e -> {
                fireEditingStopped();
                processPayment();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = "Thanh toán";
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        private void processPayment() {
            try {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String billId = table.getValueAt(selectedRow, 0).toString();
                    String service = table.getValueAt(selectedRow, 2).toString();
                    String amountStr = table.getValueAt(selectedRow, 3).toString()
                            .replace("VND", "").replace(",", "").trim();
                    double amount = Double.parseDouble(amountStr);

                    System.out.println("Processing payment for bill: " + billId);
                    System.out.println("Service: " + service);
                    System.out.println("Amount: " + amount);
                    
                    // Mở cửa sổ thanh toán
                    BillingView billingView = new BillingView(patient, billId, service, amount);
                    billingView.setLocationRelativeTo(parentView);
                    
                    // Thêm window listener để cập nhật danh sách sau khi đóng cửa sổ thanh toán
                    billingView.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            if (billingView.isPaymentSuccessful()) {
                                // Cập nhật lại danh sách hóa đơn
                                parentView.showPayFees();
                            }
                        }
                    });
                    
                    billingView.setVisible(true);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentView,
                    "Lỗi định dạng số tiền: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentView,
                    "Có lỗi xảy ra: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        public boolean shouldSelectCell(java.util.EventObject anEvent) {
            return true;
        }
    }

    public void showPaymentHistory(Object[][] paymentHistory) {
        contentPanel.removeAll();

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Lịch sử thanh toán", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        String[] columnNames = {"ID Hóa đơn", "Ngày", "Dịch vụ", "Số tiền", "Phương thức thanh toán", "Ngày thanh toán"};

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        if (paymentHistory != null) {
            for (Object[] payment : paymentHistory) {
                model.addRow(payment);
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        historyPanel.add(titleLabel, BorderLayout.NORTH);
        historyPanel.add(wrapperPanel, BorderLayout.CENTER);

        contentPanel.add(historyPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPaymentForm(String billID, double amount) {
        JDialog dialog = new JDialog(this, "Thanh toán hóa đơn", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 45, 65));
        headerPanel.setPreferredSize(new Dimension(400, 60));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Thanh toán hóa đơn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        JLabel idLabel = new JLabel("ID Hóa đơn:");
        JTextField idField = new JTextField(billID);
        idField.setEditable(false);
        addFormField(formPanel, gbc, "ID Hóa đơn:", idField, 0);

        JLabel amountLabel = new JLabel("Số tiền:");
        JTextField amountField = new JTextField(String.format("%,.0f VND", amount));
        amountField.setEditable(false);
        addFormField(formPanel, gbc, "Số tiền:", amountField, 1);

        JLabel methodLabel = new JLabel("Phương thức thanh toán:");
        String[] methods = {"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản ngân hàng"};
        JComboBox<String> methodComboBox = new JComboBox<>(methods);
        addFormField(formPanel, gbc, "Phương thức thanh toán:", methodComboBox, 2);

        // Add additional payment info fields
        JPanel cardDetailsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        cardDetailsPanel.setVisible(false);

        JTextField cardNumberField = new JTextField(20);
        JTextField cardExpiryField = new JTextField(10);

        cardDetailsPanel.add(new JLabel("Số thẻ:"));
        cardDetailsPanel.add(cardNumberField);
        cardDetailsPanel.add(new JLabel("Hạn thẻ (MM/YY):"));
        cardDetailsPanel.add(cardExpiryField);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        formPanel.add(cardDetailsPanel, gbc);

        // Show card details only when credit card is selected
        methodComboBox.addActionListener(e -> {
            String selectedMethod = (String) methodComboBox.getSelectedItem();
            cardDetailsPanel.setVisible("Thẻ tín dụng".equals(selectedMethod));
            dialog.pack();
            dialog.setSize(400, "Thẻ tín dụng".equals(selectedMethod) ? 450 : 350);
            dialog.setLocationRelativeTo(this);
        });

        JLabel confirmLabel = new JLabel("Xác nhận thanh toán:");
        JCheckBox confirmCheckbox = new JCheckBox("Tôi xác nhận các thông tin thanh toán là chính xác");
        gbc.gridy = 4;
        formPanel.add(confirmLabel, gbc);
        gbc.gridy = 5;
        formPanel.add(confirmCheckbox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Hủy");
        JButton payButton = new JButton("Thanh toán");

        payButton.setBackground(new Color(0, 123, 255));
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);
        payButton.setEnabled(false);

        confirmCheckbox.addActionListener(e -> {
            payButton.setEnabled(confirmCheckbox.isSelected());
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        payButton.addActionListener(e -> {
            String selectedMethod = (String) methodComboBox.getSelectedItem();

            // Validate card details if payment method is credit card
            if ("Thẻ tín dụng".equals(selectedMethod)) {
                String cardNumber = cardNumberField.getText().trim();
                String cardExpiry = cardExpiryField.getText().trim();

                if (cardNumber.isEmpty() || cardExpiry.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Vui lòng nhập đầy đủ thông tin thẻ tín dụng.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Basic card number validation (should have 16 digits)
                if (!cardNumber.matches("\\d{16}")) {
                    JOptionPane.showMessageDialog(dialog,
                            "Số thẻ không hợp lệ. Vui lòng nhập 16 chữ số.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Basic card expiry validation (should be in MM/YY format)
                if (!cardExpiry.matches("\\d{2}/\\d{2}")) {
                    JOptionPane.showMessageDialog(dialog,
                            "Định dạng hạn thẻ không hợp lệ. Vui lòng nhập theo định dạng MM/YY.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            boolean success = controller.payBill(billID, selectedMethod);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Thanh toán thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                controller.showPayFees(); // Cập nhật lại danh sách hóa đơn
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Thanh toán thất bại. Vui lòng thử lại sau.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(payButton);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public void showStatusMessage(String message) {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel(message);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        statusPanel.add(statusLabel);

        // If there's an existing status bar, remove it
        Component[] components = contentPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && "STATUS_BAR".equals(component.getName())) {
                contentPanel.remove(component);
                break;
            }
        }

        statusPanel.setName("STATUS_BAR");
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addFormField(JPanel formPanel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(field, gbc);
    }

    private void styleButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(34, 45, 65));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public JButton getBtnHome() {
        return btnHome;
    }

    public JButton getBtnViewInfo() {
        return btnViewInfo;
    }

    public JButton getBtnViewAppointments() {
        return btnViewAppointments;
    }

    public JButton getBtnViewMedicalHistory() {
        return btnViewMedicalHistory;
    }

    public JButton getBtnPayFees() {
        return btnPayFees;
    }

    public JButton getBtnPaymentHistory() {
        return btnPaymentHistory;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setController(PatientController controller) {
        this.controller = controller;
    }
    
    /**
     * Phương thức main để test PatientView với dữ liệu từ database
     */
    public static void main(String[] args) {
        try {
            // Thiết lập look and feel cho giao diện
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Kết nối database
                Connection conn = DatabaseConnection.getConnection();
                System.out.println("Kết nối database thành công!");
                
                // Truy vấn thông tin bệnh nhân với mã PAT-001
                String patientId = "PAT-001";
                String sql = "SELECT p.*, u.UserID, u.CreatedAt as RegistrationDate " +
                             "FROM Patients p " + 
                             "JOIN UserAccounts u ON p.UserID = u.UserID " +
                             "WHERE p.PatientID = ?";
                
                Patient patient = null;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, patientId);
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        // Tạo đối tượng Patient từ dữ liệu database
                        System.out.println("Tìm thấy bệnh nhân trong database!");
                        patient = new Patient(
                            rs.getString("UserID"),
                            rs.getString("PatientID"),
                            rs.getString("FullName"),
                            rs.getDate("DateOfBirth").toLocalDate(),
                            rs.getString("Address"),
                            Gender.fromDatabase(rs.getString("Gender")),
                            rs.getString("PhoneNumber"),
                            rs.getDate("RegistrationDate").toLocalDate()
                        );
                    }
                }
                
                // Nếu không tìm thấy trong database, tạo một đối tượng mẫu
                if (patient == null) {
                    System.out.println("Không tìm thấy bệnh nhân trong database, tạo dữ liệu mẫu...");
                    patient = new Patient(
                        "USER001", // userID
                        patientId, // patientID 
                        "Nguyễn Văn A", // fullName
                        LocalDate.of(1990, 5, 15), // dateOfBirth
                        "123 Đường ABC, Quận 1, TP.HCM", // address
                        Gender.MALE, // gender
                        "0901234567", // phoneNumber
                        LocalDate.now().minusYears(1) // createdAt (ngày đăng ký cách đây 1 năm)
                    );
                }
                
                System.out.println("============ THÔNG TIN BỆNH NHÂN ============");
                System.out.println("PatientID: " + patient.getPatientID());
                System.out.println("Họ tên: " + patient.getFullName());
                System.out.println("Ngày sinh: " + patient.getDateOfBirth());
                System.out.println("Giới tính: " + patient.getGender());
                System.out.println("Địa chỉ: " + patient.getAddress());
                System.out.println("SĐT: " + patient.getPhoneNumber());
                System.out.println("Ngày đăng ký: " + patient.getRegistrationDate());
                System.out.println("===========================================");
                
                // Hiển thị giao diện PatientView với thông tin bệnh nhân
                new PatientView(patient);
                
            } catch (SQLException e) {
                System.err.println("Lỗi kết nối database: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback: Tạo giao diện với dữ liệu mẫu nếu kết nối database thất bại
                JOptionPane.showMessageDialog(
                    null,
                    "Không thể kết nối đến cơ sở dữ liệu. Sử dụng dữ liệu mẫu thay thế.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
                );
                
                Patient fallbackPatient = new Patient(
                    "USER001", 
                    "PAT-001", 
                    "Nguyễn Văn A", 
                    LocalDate.of(1990, 5, 15), 
                    "123 Đường ABC, Quận 1, TP.HCM", 
                    Gender.MALE, 
                    "0901234567", 
                    LocalDate.now().minusYears(1)
                );
                
                new PatientView(fallbackPatient);
            } catch (Exception e) {
                System.err.println("Lỗi không xác định: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khởi động ứng dụng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }


    // Add new method to show prescription details
    public void showPrescriptionDetails() {
        contentPanel.removeAll();
    
        JPanel prescriptionPanel = new JPanel(new BorderLayout());
        prescriptionPanel.setBackground(new Color(245, 245, 245));
    
        // Title
        JLabel titleLabel = new JLabel("Chi tiết đơn thuốc", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
    
        // Disease Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    
        JLabel filterLabel = new JLabel("Chọn loại bệnh:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Lấy danh sách chẩn đoán từ database
        List<String> diagnoses = controller.getDiagnosisList();
        JComboBox<String> diseaseComboBox = new JComboBox<>(diagnoses.toArray(new String[0]));
        diseaseComboBox.setPreferredSize(new Dimension(200, 30));
    
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(diseaseComboBox);
    
        // Prescription List Panel
        JPanel prescriptionListPanel = new JPanel();
        prescriptionListPanel.setLayout(new BoxLayout(prescriptionListPanel, BoxLayout.Y_AXIS));
        prescriptionListPanel.setBackground(Color.WHITE);
        prescriptionListPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    
        JLabel prescriptionListLabel = new JLabel("Danh sách đơn thuốc:");
        prescriptionListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        prescriptionListPanel.add(prescriptionListLabel);
        prescriptionListPanel.add(Box.createVerticalStrut(10));
    
        // Lấy danh sách đơn thuốc từ database
        List<Map<String, Object>> prescriptions = controller.getPrescriptionsForPatient(patient.getPatientID());
    
        JPanel prescriptionButtonsPanel = new JPanel();
        prescriptionButtonsPanel.setLayout(new BoxLayout(prescriptionButtonsPanel, BoxLayout.Y_AXIS));
        prescriptionButtonsPanel.setBackground(Color.WHITE);
    
        // Tạo một CardLayout để hiển thị chi tiết đơn thuốc
        JPanel detailsContainer = new JPanel(new CardLayout());
        detailsContainer.setBackground(Color.WHITE);
        
        // Tạo một panel trống cho trạng thái mặc định
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(Color.WHITE);
        JLabel emptyLabel = new JLabel("Vui lòng chọn đơn thuốc để xem chi tiết", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        emptyPanel.add(emptyLabel);
        
        detailsContainer.add(emptyPanel, "EMPTY");
    
        // Tạo các nút cho từng đơn thuốc và panel chi tiết tương ứng
        ButtonGroup prescriptionGroup = new ButtonGroup();
        for (Map<String, Object> prescription : prescriptions) {
            String prescriptionId = (String) prescription.get("prescriptionId");
            Date date = (Date) prescription.get("date");
            String doctorName = (String) prescription.get("doctorName");
            String diagnosis = (String) prescription.get("diagnosis");
    
            // Tạo nút radio cho đơn thuốc
            JRadioButton prescriptionButton = new JRadioButton(
                    "<html><b>" + prescriptionId + "</b> - " + date + " - " + doctorName + "<br>" +
                    "<i>Chẩn đoán: " + (diagnosis != null ? diagnosis : "N/A") + "</i></html>"
            );
            prescriptionButton.setBackground(Color.WHITE);
            prescriptionButton.setFocusPainted(false);
            prescriptionGroup.add(prescriptionButton);
    
            // Tạo panel chi tiết cho đơn thuốc
            JPanel detailsPanel = createPrescriptionDetailsPanel(prescriptionId);
            detailsContainer.add(detailsPanel, prescriptionId);
    
            // Khi nút được chọn, hiển thị chi tiết tương ứng
            prescriptionButton.addActionListener(e -> {
                CardLayout cardLayout = (CardLayout) detailsContainer.getLayout();
                cardLayout.show(detailsContainer, prescriptionId);
            });
    
            prescriptionButtonsPanel.add(prescriptionButton);
            prescriptionButtonsPanel.add(Box.createVerticalStrut(5));
        }
    
        // Nếu không có đơn thuốc nào
        if (prescriptions.isEmpty()) {
            JLabel noDataLabel = new JLabel("Không có đơn thuốc nào!", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            prescriptionButtonsPanel.add(noDataLabel);
            
            // Hiển thị panel rỗng
            CardLayout cardLayout = (CardLayout) detailsContainer.getLayout();
            cardLayout.show(detailsContainer, "EMPTY");
        }
        
        JScrollPane prescriptionScroll = new JScrollPane(prescriptionButtonsPanel);
        prescriptionScroll.setPreferredSize(new Dimension(300, 300));
        prescriptionScroll.setBorder(BorderFactory.createEmptyBorder());
        prescriptionListPanel.add(prescriptionScroll);
    
        // Add action listener for disease filter
        diseaseComboBox.addActionListener(e -> {
            String selectedDisease = (String) diseaseComboBox.getSelectedItem();
            // Tải lại danh sách đơn thuốc theo chẩn đoán
            updatePrescriptionList(prescriptionButtonsPanel, detailsContainer, selectedDisease);
        });
    
        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
        
        // Top panel chứa filter
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Left panel chứa danh sách đơn thuốc
        mainPanel.add(prescriptionListPanel, BorderLayout.WEST);
        
        // Center panel chứa chi tiết đơn thuốc
        mainPanel.add(detailsContainer, BorderLayout.CENTER);
    
        prescriptionPanel.add(titleLabel, BorderLayout.NORTH);
        prescriptionPanel.add(mainPanel, BorderLayout.CENTER);
    
        contentPanel.add(prescriptionPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    
        // Set the selected button
        setSelectedButton(btnViewPrescriptions);
    }
    
    private JPanel createPrescriptionDetailsPanel(String prescriptionId) {
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 20));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    
        // Lấy chi tiết đơn thuốc từ database
        Map<String, Object> details = controller.getPrescriptionDetails(prescriptionId);
        
        if (details == null || details.isEmpty()) {
            JLabel errorLabel = new JLabel("Không thể tải thông tin đơn thuốc!", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            errorLabel.setForeground(Color.RED);
            detailsPanel.add(errorLabel, BorderLayout.CENTER);
            return detailsPanel;
        }
    
        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        // Add prescription information fields
        JLabel lblPrescriptionId = createInfoLabel("Mã đơn thuốc: " + prescriptionId);
        JLabel lblDiagnosis = createInfoLabel("Chẩn đoán: " + details.getOrDefault("diagnosis", "N/A"));
        JLabel lblDate = createInfoLabel("Ngày kê đơn: " + details.getOrDefault("date", "N/A"));
        JLabel lblDoctor = createInfoLabel("Bác sĩ: " + details.getOrDefault("doctorName", "N/A"));
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(lblPrescriptionId, gbc);
        gbc.gridy = 1;
        infoPanel.add(lblDiagnosis, gbc);
        gbc.gridy = 2;
        infoPanel.add(lblDate, gbc);
        gbc.gridy = 3;
        infoPanel.add(lblDoctor, gbc);
    
        // Medicine Table
        String[] columns = {"STT", "Tên thuốc", "Đơn vị", "Liều dùng", "Cách dùng"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> medications = (List<Map<String, Object>>) details.getOrDefault("medications", new ArrayList<>());
        
        for (Map<String, Object> med : medications) {
            model.addRow(new Object[]{
                med.get("index"),
                med.get("medicineName"),
                med.get("dosageForm"),
                med.get("dosage"),
                med.get("instructions")
            });
        }
    
        JTable medicineTable = new JTable(model);
        medicineTable.setRowHeight(30);
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(medicineTable);
    
        // Notes Panel
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("Ghi chú"));
        JTextArea txtNotes = new JTextArea(4, 40);
        txtNotes.setEditable(false);
        txtNotes.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setText((String) details.getOrDefault("notes", ""));
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesPanel.add(notesScroll);
    
        // Add components to details panel
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(scrollPane, BorderLayout.CENTER);
        detailsPanel.add(notesPanel, BorderLayout.SOUTH);
    
        return detailsPanel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }
    
    private void updatePrescriptionList(JPanel prescriptionButtonsPanel, JPanel detailsContainer, String selectedDisease) {
        // Xóa tất cả các nút cũ
        prescriptionButtonsPanel.removeAll();
        
        // Lấy danh sách đơn thuốc mới theo chẩn đoán
        List<Map<String, Object>> filteredPrescriptions = controller.getPrescriptionsByDiagnosis(
                patient.getPatientID(), selectedDisease);
        
        ButtonGroup prescriptionGroup = new ButtonGroup();
        
        // Thêm các nút mới
        for (Map<String, Object> prescription : filteredPrescriptions) {
            String prescriptionId = (String) prescription.get("prescriptionId");
            Date date = (Date) prescription.get("date");
            String doctorName = (String) prescription.get("doctorName");
            String diagnosis = (String) prescription.get("diagnosis");
            
            // Tạo nút radio cho đơn thuốc
            JRadioButton prescriptionButton = new JRadioButton(
                    "<html><b>" + prescriptionId + "</b> - " + date + " - " + doctorName + "<br>" +
                    "<i>Chẩn đoán: " + (diagnosis != null ? diagnosis : "N/A") + "</i></html>"
            );
            prescriptionButton.setBackground(Color.WHITE);
            prescriptionButton.setFocusPainted(false);
            prescriptionGroup.add(prescriptionButton);
            
            // Kiểm tra xem panel chi tiết đã tồn tại chưa
            boolean panelExists = false;
            for (Component comp : detailsContainer.getComponents()) {
                if (comp instanceof JPanel && comp.getName() != null && comp.getName().equals(prescriptionId)) {
                    panelExists = true;
                    break;
                }
            }
            
            // Nếu chưa tồn tại, tạo mới
            if (!panelExists) {
                JPanel detailsPanel = createPrescriptionDetailsPanel(prescriptionId);
                detailsPanel.setName(prescriptionId);
                detailsContainer.add(detailsPanel, prescriptionId);
            }
            
            // Khi nút được chọn, hiển thị chi tiết tương ứng
            prescriptionButton.addActionListener(e -> {
                CardLayout cardLayout = (CardLayout) detailsContainer.getLayout();
                cardLayout.show(detailsContainer, prescriptionId);
            });
            
            prescriptionButtonsPanel.add(prescriptionButton);
            prescriptionButtonsPanel.add(Box.createVerticalStrut(5));
        }
        
        // Nếu không có đơn thuốc nào
        if (filteredPrescriptions.isEmpty()) {
            JLabel noDataLabel = new JLabel("Không có đơn thuốc nào phù hợp với tiêu chí!", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            prescriptionButtonsPanel.add(noDataLabel);
            
            // Hiển thị panel rỗng
            CardLayout cardLayout = (CardLayout) detailsContainer.getLayout();
            cardLayout.show(detailsContainer, "EMPTY");
        }
        
        prescriptionButtonsPanel.revalidate();
        prescriptionButtonsPanel.repaint();
    }
}