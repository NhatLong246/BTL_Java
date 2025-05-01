package view;

import controller.AdminController;
import database.DatabaseConnection;
import model.entity.Doctor;
import model.enums.Gender;
import model.enums.Specialization;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminView extends JFrame {
    private AdminController controller;
    private JPanel contentPanel;
    private JButton btnHome, btnCreateDoctor, btnManageDoctor, btnViewLockedDoctors, btnScheduleDoctor, btnViewDoctorInfo, btnLogout;
    private JButton currentSelectedButton;
    private ImageIcon workingIcon, finishedIcon, notWorkingIcon;

    // Lớp để lưu trữ và hiển thị chuyên khoa trong ComboBox
    private class SpecialtyItem {
        private String id;
        private String name;
        
        public SpecialtyItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name; // Để hiển thị tên trong ComboBox
        }
    }

    public AdminView(String adminId) {
        this.controller = new AdminController(this, adminId);
        setTitle("Bảng điều khiển quản trị");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Khởi tạo các icons
        try {
            File checkFile = new File("resources/icons/check.png");
            File warningFile = new File("resources/icons/timeOut.png");
            File crossFile = new File("resources/icons/cross.png");

            if (checkFile.exists() && warningFile.exists() && crossFile.exists()) {
                workingIcon = new ImageIcon(checkFile.getAbsolutePath());
                finishedIcon = new ImageIcon(warningFile.getAbsolutePath());
                notWorkingIcon = new ImageIcon(crossFile.getAbsolutePath());

                workingIcon = new ImageIcon(workingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                finishedIcon = new ImageIcon(finishedIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                notWorkingIcon = new ImageIcon(notWorkingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            } else {
                System.out.println("Không tìm thấy một hoặc nhiều file icons.");
            }
        } catch (Exception e) {
            System.out.println("Không thể tải icons: " + e.getMessage());
            e.printStackTrace();
        }

        debugIconPaths();

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

        JLabel menuTitle = new JLabel("Admin Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10);
        gbc.weighty = 0.1;
        leftPanel.add(menuTitle, gbc);

        btnHome = createButton("Trang chủ");
        btnCreateDoctor = createButton("Tạo tài khoản bác sĩ");
        btnManageDoctor = createButton("Quản lý bác sĩ");
        btnViewLockedDoctors = createButton("Xem bác sĩ bị khóa");
        btnScheduleDoctor = createButton("Lịch làm việc bác sĩ");
        btnViewDoctorInfo = createButton("Thông tin bác sĩ");
        btnLogout = createButton("Đăng xuất");

        setSelectedButton(btnHome);

        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.weighty = 0.0;

        gbc.gridy = 1;
        leftPanel.add(btnHome, gbc);
        gbc.gridy = 2;
        leftPanel.add(btnCreateDoctor, gbc);
        gbc.gridy = 3;
        leftPanel.add(btnManageDoctor, gbc);
        gbc.gridy = 4;
        leftPanel.add(btnViewLockedDoctors, gbc);
        gbc.gridy = 5;
        leftPanel.add(btnScheduleDoctor, gbc);
        gbc.gridy = 6;
        leftPanel.add(btnViewDoctorInfo, gbc);
        gbc.gridy = 7;
        gbc.weighty = 0.0;
        leftPanel.add(btnLogout, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        controller.showHome();

        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnHome.addActionListener(e -> controller.showHome());
        btnCreateDoctor.addActionListener(e -> controller.showCreateDoctorForm());
        btnManageDoctor.addActionListener(e -> controller.showManageDoctorForm());
        btnViewLockedDoctors.addActionListener(e -> controller.showLockedDoctors());
        btnScheduleDoctor.addActionListener(e -> controller.showScheduleDoctorForm());
        btnViewDoctorInfo.addActionListener(e -> controller.showViewDoctorInfoForm());
        btnLogout.addActionListener(e -> controller.logout());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
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

    public void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(new Color(34, 45, 65));
            currentSelectedButton.setForeground(Color.WHITE);
        }
        selectedButton.setBackground(new Color(255, 255, 255));
        selectedButton.setForeground(new Color(34, 45, 65));
        currentSelectedButton = selectedButton;
    }

    public JButton getBtnHome() {
        return btnHome;
    }

    public JButton getBtnCreateDoctor() {
        return btnCreateDoctor;
    }

    public JButton getBtnManageDoctor() {
        return btnManageDoctor;
    }

    public JButton getBtnViewLockedDoctors() {
        return btnViewLockedDoctors;
    }

    public JButton getBtnScheduleDoctor() {
        return btnScheduleDoctor;
    }

    public JButton getBtnViewDoctorInfo() {
        return btnViewDoctorInfo;
    }

    public void showHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(new Color(245, 245, 245));

        // Header section - Welcome message and date
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));

        JLabel welcomeLabel = new JLabel("Chào mừng, Quản trị viên " + controller.getAdminName(), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(34, 45, 65));

        JLabel dateLabel = new JLabel(LocalDate.now().toString(), SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(100, 100, 100));

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        // Admin Info Panel
        JPanel adminInfoPanel = new JPanel(new BorderLayout());
        adminInfoPanel.setBackground(Color.WHITE);
        adminInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JPanel personalPanel = new JPanel(new BorderLayout(20, 0));
        personalPanel.setOpaque(false);

        JPanel avatarPanel = new JPanel();
        avatarPanel.setPreferredSize(new Dimension(150, 150));
        avatarPanel.setBackground(new Color(41, 128, 185));
        JLabel avatarLabel = new JLabel("QT", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 48));
        avatarLabel.setForeground(Color.WHITE);
        avatarPanel.add(avatarLabel);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Quản trị viên: " + controller.getAdminName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel emailLabel = new JLabel("Email: " + controller.getAdminEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel phoneLabel = new JLabel("Điện thoại: " + controller.getAdminPhone());
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        infoPanel.add(nameLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(phoneLabel);

        personalPanel.add(avatarPanel, BorderLayout.WEST);
        personalPanel.add(infoPanel, BorderLayout.CENTER);

        adminInfoPanel.add(personalPanel, BorderLayout.NORTH);

        // Dashboard - Statistics
        JPanel dashboardPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));
        dashboardPanel.setOpaque(false);

        // Placeholder statistics (you can fetch real data from the controller if available)
        int totalDoctors = 10; // Replace with controller method if available
        int lockedDoctors = 2; // Replace with controller method if available
        int scheduledDoctors = 5; // Replace with controller method if available

        JPanel doctorsCard = createDashboardCard("Tổng số bác sĩ", String.valueOf(totalDoctors), new Color(41, 128, 185));
        JPanel lockedCard = createDashboardCard("Bác sĩ bị khóa", String.valueOf(lockedDoctors), new Color(230, 126, 34));
        JPanel scheduledCard = createDashboardCard("Bác sĩ có lịch", String.valueOf(scheduledDoctors), new Color(39, 174, 96));

        dashboardPanel.add(doctorsCard);
        dashboardPanel.add(lockedCard);
        dashboardPanel.add(scheduledCard);

        // Sample Schedule Panel (Placeholder)
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel scheduleHeader = new JPanel(new BorderLayout());
        scheduleHeader.setOpaque(false);
        scheduleHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel scheduleLabel = new JLabel("Lịch làm việc bác sĩ", SwingConstants.LEFT);
        scheduleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        LocalDate today = LocalDate.now();
        String dayOfWeek = getDayOfWeekInVietnamese(today.getDayOfWeek());
        LocalTime now = LocalTime.now();
        JLabel todayLabel = new JLabel("Hôm nay: " + dayOfWeek + ", " + today + " - " + now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), SwingConstants.RIGHT);
        todayLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        scheduleHeader.add(scheduleLabel, BorderLayout.WEST);
        scheduleHeader.add(todayLabel, BorderLayout.EAST);

        JPanel calendarPanel = new JPanel(new GridLayout(4, 8));
        calendarPanel.setBackground(Color.WHITE);

        calendarPanel.add(createHeaderCell("Ca / Ngày"));
        calendarPanel.add(createHeaderCell("Thứ Hai"));
        calendarPanel.add(createHeaderCell("Thứ Ba"));
        calendarPanel.add(createHeaderCell("Thứ Tư"));
        calendarPanel.add(createHeaderCell("Thứ Năm"));
        calendarPanel.add(createHeaderCell("Thứ Sáu"));
        calendarPanel.add(createHeaderCell("Thứ Bảy"));
        calendarPanel.add(createHeaderCell("Chủ Nhật"));

        String[] shifts = {"Sáng (7:00-11:30)", "Chiều (13:30-17:00)", "Tối (17:00-7:00)"};
        LocalTime[] shiftStartTimes = {LocalTime.of(7, 0), LocalTime.of(13, 30), LocalTime.of(17, 0)};
        LocalTime[] shiftEndTimes = {LocalTime.of(11, 30), LocalTime.of(17, 0), LocalTime.of(7, 0)};

        // Placeholder schedule data (you can fetch real data from the controller)
        String[][] scheduleData = new String[][]{
            {"Đang làm việc", "Không làm việc", "Hết ca làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc", "Không làm việc"},
            {"Không làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc"},
            {"Hết ca làm việc", "Không làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc", "Đang làm việc", "Không làm việc"}
        };

        String currentShift = getCurrentShift(now);
        for (int i = 0; i < shifts.length; i++) {
            calendarPanel.add(createHeaderCell(shifts[i]));
            for (int j = 0; j < 7; j++) {
                String initialStatus = scheduleData[i][j];
                String displayStatus;
                Color bgColor;

                if (j == today.getDayOfWeek().getValue() - 1) {
                    if (initialStatus.equals("Đang làm việc")) {
                        LocalTime startTime = shiftStartTimes[i];
                        LocalTime endTime = shiftEndTimes[i];
                        boolean isInShift;
                        if (i == 2) {
                            isInShift = (now.isAfter(startTime) || now.equals(startTime)) || 
                                        (now.isBefore(endTime) || now.equals(endTime));
                        } else {
                            isInShift = (now.isAfter(startTime) || now.equals(startTime)) && 
                                        (now.isBefore(endTime) || now.equals(endTime));
                        }
                        if (isInShift) {
                            displayStatus = "Đang làm việc";
                            bgColor = new Color(40, 167, 69, 80);
                        } else {
                            displayStatus = "Hết ca làm việc";
                            bgColor = new Color(255, 193, 7, 100);
                        }
                    } else {
                        displayStatus = initialStatus;
                        if (displayStatus.equals("Hết ca làm việc")) {
                            bgColor = new Color(255, 193, 7, 100);
                        } else {
                            bgColor = new Color(240, 240, 240);
                        }
                    }
                } else {
                    displayStatus = initialStatus;
                    if (initialStatus.equals("Đang làm việc")) {
                        LocalDate cellDate = today.minusDays(today.getDayOfWeek().getValue() - 1 - j);
                        boolean isPastDay = cellDate.isBefore(today);
                        boolean isPastShiftOnToday = cellDate.equals(today) && (
                            (i == 0 && now.isAfter(LocalTime.of(11, 30))) || 
                            (i == 1 && now.isAfter(LocalTime.of(17, 0)))
                        );
                        if (isPastDay || isPastShiftOnToday) {
                            displayStatus = "Hết ca làm việc";
                            bgColor = new Color(255, 193, 7, 100);
                        } else {
                            bgColor = new Color(40, 167, 69, 80);
                        }
                    } else if (initialStatus.equals("Hết ca làm việc")) {
                        bgColor = new Color(255, 193, 7, 100);
                    } else {
                        bgColor = new Color(240, 240, 240);
                    }
                }

                boolean isCurrentShift = j == today.getDayOfWeek().getValue() - 1 && shifts[i].startsWith(currentShift);
                JPanel cell = createScheduleCell(displayStatus, bgColor, isCurrentShift);
                calendarPanel.add(cell);
            }
        }

        schedulePanel.add(scheduleHeader, BorderLayout.NORTH);
        schedulePanel.add(calendarPanel, BorderLayout.CENTER);

        // Legend for Schedule
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel workingLegend = createLegendItemWithIcon("Ca làm việc", new Color(40, 167, 69, 60), workingIcon);
        JPanel finishedLegend = createLegendItemWithIcon("Hết ca làm việc", new Color(255, 193, 7, 100), finishedIcon);
        JPanel notWorkingLegend = createLegendItemWithIcon("Không làm việc", new Color(240, 240, 240), notWorkingIcon);

        legendPanel.add(workingLegend);
        legendPanel.add(finishedLegend);
        legendPanel.add(notWorkingLegend);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(legendPanel, BorderLayout.CENTER);

        schedulePanel.add(bottomPanel, BorderLayout.SOUTH);

        // Quick Access Buttons
        JPanel quickAccessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        quickAccessPanel.setOpaque(false);
        quickAccessPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JButton createDoctorBtn = createQuickButton("Tạo bác sĩ mới", new Color(0, 123, 255));
        JButton manageDoctorBtn = createQuickButton("Quản lý bác sĩ", new Color(23, 162, 184));
        JButton scheduleBtn = createQuickButton("Lịch làm việc", new Color(40, 167, 69));

        createDoctorBtn.addActionListener(e -> controller.showCreateDoctorForm());
        manageDoctorBtn.addActionListener(e -> controller.showManageDoctorForm());
        scheduleBtn.addActionListener(e -> controller.showScheduleDoctorForm());

        quickAccessPanel.add(createDoctorBtn);
        quickAccessPanel.add(manageDoctorBtn);
        quickAccessPanel.add(scheduleBtn);

        // Layout Assembly
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(adminInfoPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(dashboardPanel, BorderLayout.NORTH);

        JPanel mainBottomPanel = new JPanel(new GridLayout(1, 1, 20, 0));
        mainBottomPanel.setOpaque(false);
        mainBottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        mainBottomPanel.add(schedulePanel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(quickAccessPanel, BorderLayout.NORTH);
        southPanel.add(mainBottomPanel, BorderLayout.CENTER);

        homePanel.add(headerPanel, BorderLayout.NORTH);
        homePanel.add(topPanel, BorderLayout.CENTER);
        homePanel.add(centerPanel, BorderLayout.SOUTH);
        homePanel.add(southPanel, BorderLayout.SOUTH);

        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showAdminInfo() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Thông tin quản trị viên", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

        public void showCreateDoctorForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
    
        JLabel titleLabel = new JLabel("Tạo tài khoản bác sĩ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
    
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
    
        JTextField txtUsername = new JTextField(40);
        JTextField txtFullName = new JTextField(40);
        JTextField txtEmail = new JTextField(40);
        JTextField txtPhone = new JTextField(40);
        JTextField txtAddress = new JTextField(40);
        JTextField txtBirthDate = new JTextField(40);
        JComboBox<Gender> cbGender = new JComboBox<>(Gender.values());
        
        // Tạo model cho JComboBox chuyên khoa từ database
        DefaultComboBoxModel<SpecialtyItem> specialtyModel = new DefaultComboBoxModel<>();
        List<Map<String, String>> specialties = controller.getAllSpecialties();
        
        for (Map<String, String> specialty : specialties) {
            specialtyModel.addElement(new SpecialtyItem(
                specialty.get("id"),
                specialty.get("name")
            ));
        }
        
        JComboBox<SpecialtyItem> cbSpecialty = new JComboBox<>(specialtyModel);
    
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        txtUsername.setFont(fieldFont);
        txtFullName.setFont(fieldFont);
        txtEmail.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        txtBirthDate.setFont(fieldFont);
        cbGender.setFont(fieldFont);
        cbSpecialty.setFont(fieldFont);
    
        addFormField(formPanel, gbc, "Tên đăng nhập:", txtUsername, 0);
        addFormField(formPanel, gbc, "Họ và tên:", txtFullName, 1);
        addFormField(formPanel, gbc, "Email:", txtEmail, 2);
        addFormField(formPanel, gbc, "Số điện thoại:", txtPhone, 3);
        addFormField(formPanel, gbc, "Địa chỉ:", txtAddress, 4);
        addFormField(formPanel, gbc, "Ngày sinh (yyyy-MM-dd):", txtBirthDate, 5);
        addFormField(formPanel, gbc, "Giới tính:", cbGender, 6);
        addFormField(formPanel, gbc, "Chuyên khoa:", cbSpecialty, 7);
    
        JButton btnSubmit = new JButton("Tạo bác sĩ");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 16));
        btnSubmit.setBackground(new Color(0, 123, 255));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(200, 45));
    
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnSubmit, gbc);
    
        btnSubmit.addActionListener(e -> {
            SpecialtyItem selectedSpecialty = (SpecialtyItem) cbSpecialty.getSelectedItem();
            controller.createDoctor(
                txtUsername.getText(), txtFullName.getText(), txtEmail.getText(), txtPhone.getText(),
                txtAddress.getText(), txtBirthDate.getText(), (Gender) cbGender.getSelectedItem(),
                selectedSpecialty != null ? selectedSpecialty.getId() : null
            );
        });
    
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));
    
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showManageDoctorForm(List<Doctor> doctors) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Quản lý bác sĩ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        String[] columnNames = {"Chọn", "ID Bác sĩ", "Họ và tên", "Email", "Số điện thoại"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        JTable table = new JTable(tableModel);

        if (doctors != null && !doctors.isEmpty()) {
            for (Doctor d : doctors) {
                tableModel.addRow(new Object[]{
                        false,
                        d.getDoctorId(),
                        d.getFullName(),
                        d.getEmail(),
                        d.getPhoneNumber()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                doctors == null ? "Không thể lấy danh sách bác sĩ! Vui lòng thử lại sau." : 
                                 "Không có bác sĩ nào để hiển thị! Vui lòng tạo bác sĩ mới.", 
                doctors == null ? "Lỗi" : "Thông báo", 
                doctors == null ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }

        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnDelete = new JButton("Khóa bác sĩ");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 16));
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(200, 45));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnDelete);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnDelete.addActionListener(e -> {
            String selectedDoctorId = null;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                if (isSelected != null && isSelected) {
                    selectedDoctorId = (String) tableModel.getValueAt(i, 1);
                    break;
                }
            }

            if (selectedDoctorId == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bác sĩ để khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn khóa bác sĩ này không?",
                    "Xác nhận khóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.lockDoctor(selectedDoctorId);
            }
        });

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showLockedDoctors(List<Doctor> lockedDoctors) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Danh sách bác sĩ bị khóa", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        String[] columnNames = {"Chọn", "ID Bác sĩ", "Họ và tên", "Email", "Số điện thoại"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        JTable table = new JTable(tableModel);

        if (lockedDoctors != null && !lockedDoctors.isEmpty()) {
            for (Doctor d : lockedDoctors) {
                tableModel.addRow(new Object[]{
                        false,
                        d.getDoctorId(),
                        d.getFullName(),
                        d.getEmail(),
                        d.getPhoneNumber()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                lockedDoctors == null ? "Không thể lấy danh sách bác sĩ bị khóa!" : 
                                       "Không có bác sĩ nào bị khóa!", 
                lockedDoctors == null ? "Lỗi" : "Thông báo", 
                lockedDoctors == null ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }

        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnUnlock = new JButton("Mở khóa bác sĩ");
        btnUnlock.setFont(new Font("Arial", Font.BOLD, 16));
        btnUnlock.setBackground(new Color(40, 167, 69));
        btnUnlock.setForeground(Color.WHITE);
        btnUnlock.setFocusPainted(false);
        btnUnlock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUnlock.setPreferredSize(new Dimension(200, 45));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnUnlock);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnUnlock.addActionListener(e -> {
            String selectedDoctorId = null;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                if (isSelected != null && isSelected) {
                    selectedDoctorId = (String) tableModel.getValueAt(i, 1);
                    break;
                }
            }

            if (selectedDoctorId == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bác sĩ để mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn mở khóa bác sĩ này không?",
                    "Xác nhận mở khóa",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.unlockDoctor(selectedDoctorId);
            }
        });

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showScheduleDoctorForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Lịch làm việc bác sĩ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Doctor ID Input
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtDoctorId = new JTextField(40);
        txtDoctorId.setFont(new Font("Arial", Font.PLAIN, 16));
        addFormField(inputPanel, gbc, "ID Bác sĩ:", txtDoctorId, 0);

        // Schedule Grid
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setOpaque(false);
        schedulePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel scheduleHeader = new JPanel(new BorderLayout());
        scheduleHeader.setOpaque(false);
        scheduleHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel scheduleLabel = new JLabel("Sắp xếp lịch làm việc", SwingConstants.LEFT);
        scheduleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        LocalDate today = LocalDate.now();
        String dayOfWeek = getDayOfWeekInVietnamese(today.getDayOfWeek());
        LocalTime now = LocalTime.now();
        JLabel todayLabel = new JLabel("Hôm nay: " + dayOfWeek + ", " + today + " - " + now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), SwingConstants.RIGHT);
        todayLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        scheduleHeader.add(scheduleLabel, BorderLayout.WEST);
        scheduleHeader.add(todayLabel, BorderLayout.EAST);

        JPanel calendarPanel = new JPanel(new GridLayout(4, 8));
        calendarPanel.setBackground(Color.WHITE);

        calendarPanel.add(createHeaderCell("Ca / Ngày"));
        calendarPanel.add(createHeaderCell("Thứ Hai"));
        calendarPanel.add(createHeaderCell("Thứ Ba"));
        calendarPanel.add(createHeaderCell("Thứ Tư"));
        calendarPanel.add(createHeaderCell("Thứ Năm"));
        calendarPanel.add(createHeaderCell("Thứ Sáu"));
        calendarPanel.add(createHeaderCell("Thứ Bảy"));
        calendarPanel.add(createHeaderCell("Chủ Nhật"));

        String[] shifts = {"Sáng (7:00-11:30)", "Chiều (13:30-17:00)", "Tối (17:00-7:00)"};
        boolean[][] schedule = new boolean[3][7]; // Tracks working/not working status

        // Initialize schedule grid with clickable cells
        for (int i = 0; i < shifts.length; i++) {
            calendarPanel.add(createHeaderCell(shifts[i]));
            for (int j = 0; j < 7; j++) {
                final int shiftIndex = i;
                final int dayIndex = j;
                schedule[i][j] = false; // Default to "Không làm việc"
                String status = schedule[i][j] ? "Đang làm việc" : "Không làm việc";
                Color bgColor = schedule[i][j] ? new Color(40, 167, 69, 80) : new Color(240, 240, 240);
                JPanel cell = createScheduleCell(status, bgColor, false);

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        schedule[shiftIndex][dayIndex] = !schedule[shiftIndex][dayIndex];
                        String newStatus = schedule[shiftIndex][dayIndex] ? "Đang làm việc" : "Không làm việc";
                        Color newBgColor = schedule[shiftIndex][dayIndex] ? new Color(40, 167, 69, 80) : new Color(240, 240, 240);
                        updateScheduleCell(cell, newStatus, newBgColor);
                    }
                });

                calendarPanel.add(cell);
            }
        }

        schedulePanel.add(scheduleHeader, BorderLayout.NORTH);
        schedulePanel.add(calendarPanel, BorderLayout.CENTER);

        // Legend for Schedule
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel workingLegend = createLegendItemWithIcon("Ca làm việc", new Color(40, 167, 69, 60), workingIcon);
        JPanel notWorkingLegend = createLegendItemWithIcon("Không làm việc", new Color(240, 240, 240), notWorkingIcon);

        legendPanel.add(workingLegend);
        legendPanel.add(notWorkingLegend);

        // Save Button
        JButton btnSave = new JButton("Lưu lịch");
        btnSave.setFont(new Font("Arial", Font.BOLD, 16));
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(200, 45));

        btnSave.addActionListener(e -> {
            String doctorId = txtDoctorId.getText().trim();
            if (doctorId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.saveDoctorSchedule(doctorId, schedule);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnSave);

        // Assemble the form
        formPanel.add(inputPanel, BorderLayout.NORTH);
        formPanel.add(schedulePanel, BorderLayout.CENTER);
        formPanel.add(legendPanel, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.add(southPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showViewDoctorInfoForm(List<Doctor> doctors) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Thông tin bác sĩ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        String[] columnNames = {
            "ID Người dùng", "ID Bác sĩ", "Họ và tên", "Email", "Số điện thoại", 
            "Địa chỉ", "Ngày sinh", "Giới tính", "Chuyên khoa", "Ngày tuyển dụng"
        };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        if (doctors != null && !doctors.isEmpty()) {
            for (Doctor d : doctors) {
                tableModel.addRow(new Object[]{
                    d.getUserId(),
                    d.getDoctorId(),
                    d.getFullName(),
                    d.getEmail(),
                    d.getPhoneNumber(),
                    d.getAddress(),
                    d.getDateOfBirth(),
                    d.getGender() != null ? d.getGender().getVietnamese() : "Không xác định",
                    d.getSpecialization() != null ? d.getSpecialization().getName() : "Không có",
                    d.getCreatedAt()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                doctors == null ? "Không thể lấy danh sách bác sĩ! Vui lòng thử lại sau." : 
                                 "Không có bác sĩ nào để hiển thị! Vui lòng tạo bác sĩ mới.", 
                doctors == null ? "Lỗi" : "Thông báo", 
                doctors == null ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }

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

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showDoctorInfo(Doctor doctor) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Thông tin chi tiết bác sĩ: " + doctor.getFullName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addInfoField(infoPanel, gbc, "ID Người dùng:", doctor.getUserId(), 0);
        addInfoField(infoPanel, gbc, "ID Bác sĩ:", doctor.getDoctorId(), 1);
        addInfoField(infoPanel, gbc, "Họ và tên:", doctor.getFullName(), 2);
        addInfoField(infoPanel, gbc, "Email:", doctor.getEmail(), 3);
        addInfoField(infoPanel, gbc, "Số điện thoại:", doctor.getPhoneNumber(), 4);
        addInfoField(infoPanel, gbc, "Địa chỉ:", doctor.getAddress(), 5);
        addInfoField(infoPanel, gbc, "Ngày sinh:", doctor.getDateOfBirth().toString(), 6);
        addInfoField(infoPanel, gbc, "Giới tính:", doctor.getGender() != null ? doctor.getGender().getVietnamese() : "Không xác định", 7);
        addInfoField(infoPanel, gbc, "Chuyên khoa:", doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : "Không có", 8);
        addInfoField(infoPanel, gbc, "Ngày tuyển dụng:", doctor.getCreatedAt().toString(), 9);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(infoPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addFormField(JPanel formPanel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(labelFont);
        formPanel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(field, gbc);
    }

    private void addInfoField(JPanel infoPanel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(valueLabel, gbc);
    }

    private JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createQuickButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private String getDayOfWeekInVietnamese(java.time.DayOfWeek dow) {
        switch (dow) {
            case MONDAY: return "Thứ Hai";
            case TUESDAY: return "Thứ Ba";
            case WEDNESDAY: return "Thứ Tư";
            case THURSDAY: return "Thứ Năm";
            case FRIDAY: return "Thứ Sáu";
            case SATURDAY: return "Thứ Bảy";
            case SUNDAY: return "Chủ Nhật";
            default: return "";
        }
    }

    private String getCurrentShift(LocalTime time) {
        if ((time.isAfter(LocalTime.of(7, 0)) || time.equals(LocalTime.of(7, 0))) &&
            time.isBefore(LocalTime.of(11, 30))) {
            return "Sáng";
        } else if ((time.isAfter(LocalTime.of(13, 30)) || time.equals(LocalTime.of(13, 30))) &&
                   time.isBefore(LocalTime.of(17, 0))) {
            return "Chiều";
        } else if ((time.isAfter(LocalTime.of(17, 0)) || time.equals(LocalTime.of(17, 0))) ||
                   time.isBefore(LocalTime.of(7, 0))) {
            return "Tối";
        } else {
            return "Ngoài giờ làm việc";
        }
    }

    private JPanel createHeaderCell(String text) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(new Color(240, 240, 240));
        cell.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

        cell.add(label, BorderLayout.CENTER);
        return cell;
    }

    private JPanel createScheduleCell(String status, Color bgColor, boolean isCurrentShift) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(bgColor);
        cell.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        if (isCurrentShift) {
            cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
            ));
        }

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);
        if (status.equals("Đang làm việc")) {
            iconLabel.setIcon(workingIcon);
        } else if (status.equals("Hết ca làm việc")) {
            iconLabel.setIcon(finishedIcon);
        } else {
            iconLabel.setIcon(notWorkingIcon);
        }

        contentPanel.add(iconLabel, BorderLayout.CENTER);
        cell.add(contentPanel, BorderLayout.CENTER);

        return cell;
    }

    private void updateScheduleCell(JPanel cell, String status, Color bgColor) {
        cell.setBackground(bgColor);
        JLabel iconLabel = (JLabel) ((JPanel) cell.getComponent(0)).getComponent(0);
        if (status.equals("Đang làm việc")) {
            iconLabel.setIcon(workingIcon);
        } else {
            iconLabel.setIcon(notWorkingIcon);
        }
        cell.revalidate();
        cell.repaint();
    }

    private JPanel createLegendItemWithIcon(String text, Color color, ImageIcon icon) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel iconLabel = new JLabel(icon);

        JLabel label = new JLabel(" " + text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(iconLabel);
        panel.add(label);

        return panel;
    }

    private ImageIcon createIconFromLabel(JLabel label) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        label.setSize(16, 16);
        label.paint(g2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private void debugIconPaths() {
        File resourceDir = new File("resources/icons");
        System.out.println("Thư mục icons có tồn tại: " + resourceDir.exists());
        if (resourceDir.exists()) {
            System.out.println("Danh sách files trong thư mục:");
            for (File file : resourceDir.listFiles()) {
                System.out.println(" - " + file.getName());
            }
        }
    }
//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (!DatabaseConnection.testConnection()) {
//            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu!", 
//                "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        SwingUtilities.invokeLater(() -> {
//            try {
//                String testAdminId = "ADM-001";
//                AdminView adminView = new AdminView(testAdminId);
//                adminView.setVisible(true);
//                System.out.println("Đã khởi tạo giao diện quản trị với ID: " + testAdminId);
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Lỗi khởi động giao diện: " + e.getMessage(), 
//                    "Lỗi", JOptionPane.ERROR_MESSAGE);
//                e.printStackTrace();
//            }
//        });
//    }
}