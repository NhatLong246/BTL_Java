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

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.PlainDocument;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AdminView extends JFrame {
    private AdminController controller;
    private JPanel contentPanel;
    private JButton btnHome, btnCreateDoctor, btnManageDoctor, btnViewLockedDoctors, btnScheduleDoctor, btnViewDoctorInfo, btnLogout;
    private JButton currentSelectedButton;
    private ImageIcon workingIcon, finishedIcon, notWorkingIcon;

    private JPanel[][] cellPanels;
    private JLabel[][] iconLabels;

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

    private class DateLabelFormatter extends AbstractFormatter {
        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
    
        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parse(text);
        }
    
        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
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

        JLabel menuTitle = new JLabel("", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10);
        gbc.weighty = 0.1;
        leftPanel.add(menuTitle, gbc);

        btnHome = createButton("Trang chủ");
        btnCreateDoctor = createButton("Thêm bác sĩ");
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
    
        // Schedule Panel
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
    
        // Lấy dữ liệu lịch làm việc từ database
        String[][] scheduleData = controller.getDoctorScheduleSummary();
    
        // Nếu không có dữ liệu hoặc lỗi, sử dụng dữ liệu mặc định
        if (scheduleData == null || scheduleData.length == 0) {
            scheduleData = new String[][]{
                {"Đang làm việc", "Không làm việc", "Hết ca làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc", "Không làm việc"},
                {"Không làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc"},
                {"Hết ca làm việc", "Không làm việc", "Đang làm việc", "Không làm việc", "Hết ca làm việc", "Đang làm việc", "Không làm việc"}
            };
        }
    
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
    
        JTextField txtFullName = new JTextField(40);
        JTextField txtEmail = new JTextField(40);
        JTextField txtPhone = new JTextField(40);
        JTextField txtAddress = new JTextField(40);
        // Loại bỏ JTextField cho ngày sinh
        // JTextField txtBirthDate = new JTextField(40);
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
        txtFullName.setFont(fieldFont);
        txtEmail.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        cbGender.setFont(fieldFont);
        cbSpecialty.setFont(fieldFont);
        
        // Thiết lập DatePicker cho ngày sinh
        UtilDateModel dateModel = new UtilDateModel();
        
        // Tính toán ngày cho phép: tuổi 22-70
        Calendar minAgeCalendar = Calendar.getInstance();
        minAgeCalendar.add(Calendar.YEAR, -70); // Tuổi tối đa là 70
        
        Calendar maxAgeCalendar = Calendar.getInstance();
        maxAgeCalendar.add(Calendar.YEAR, -22); // Tuổi tối thiểu là 22
        
        // Chọn giá trị mặc định là 30 tuổi
        Calendar defaultAgeCalendar = Calendar.getInstance(); 
        defaultAgeCalendar.add(Calendar.YEAR, -30);
        dateModel.setValue(defaultAgeCalendar.getTime());
        
        Properties properties = new Properties();
        properties.put("text.today", "Hôm nay");
        properties.put("text.month", "Tháng");
        properties.put("text.year", "Năm");
        
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, properties);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.getJFormattedTextField().setFont(fieldFont);
        datePicker.setToolTipText("Tuổi của bác sĩ phải từ 22 đến 70 tuổi");
        
        addFormField(formPanel, gbc, "Họ và tên:", txtFullName, 0);
        addFormField(formPanel, gbc, "Email:", txtEmail, 1);
        addFormField(formPanel, gbc, "Số điện thoại:", txtPhone, 2);
        addFormField(formPanel, gbc, "Địa chỉ:", txtAddress, 3);
        addFormField(formPanel, gbc, "Ngày sinh:", datePicker, 4); // Thay thế bằng DatePicker
        addFormField(formPanel, gbc, "Giới tính:", cbGender, 5);
        addFormField(formPanel, gbc, "Chuyên khoa:", cbSpecialty, 6);
    
        JButton btnSubmit = new JButton("Tạo bác sĩ");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 16));
        btnSubmit.setBackground(new Color(0, 123, 255));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(200, 45));
    
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnSubmit, gbc);
    
        btnSubmit.addActionListener(e -> {
            // Kiểm tra trường Họ và tên
            if (txtFullName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ và tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtFullName.requestFocus();
                return;
            }
            
            // Lấy giá trị ngày sinh từ JDatePicker
            Date selectedDate = (Date) datePicker.getModel().getValue();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                datePicker.requestFocus();
                return;
            }
            
            // Chuyển đổi sang LocalDate để tính tuổi
            LocalDate birthDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // Tính tuổi
            int age = LocalDate.now().getYear() - birthDate.getYear();
            if (birthDate.plusYears(age).isAfter(LocalDate.now())) {
                age--; // Điều chỉnh nếu sinh nhật trong năm nay chưa đến
            }
            
            // Kiểm tra điều kiện tuổi
            if (age < 22 || age > 70) {
                JOptionPane.showMessageDialog(this, 
                    "Tuổi của bác sĩ phải từ 22 đến 70!\nTuổi hiện tại: " + age, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            SpecialtyItem selectedSpecialty = (SpecialtyItem) cbSpecialty.getSelectedItem();
            controller.createDoctor(
                "", // Truyền chuỗi rỗng cho username, sẽ được tạo tự động trong repository
                txtFullName.getText(), 
                txtEmail.getText(), 
                txtPhone.getText(),
                txtAddress.getText(), 
                birthDate.toString(), // Chuyển Date thành String định dạng yyyy-MM-dd
                (Gender) cbGender.getSelectedItem(),
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
        
        // Tạo model cho ComboBox chứa thông tin bác sĩ
        DefaultComboBoxModel<String> doctorModel = new DefaultComboBoxModel<>();
        doctorModel.addElement("Hiển thị tất cả"); // Thêm tùy chọn hiển thị tất cả
        
        // Lấy danh sách bác sĩ từ controller
        List<Doctor> doctors = controller.getAllDoctors();
        for (Doctor doctor : doctors) {
            String displayText = doctor.getDoctorId() + " - " + doctor.getFullName();
            if (doctor.getSpecialization() != null) {
                displayText += " (" + doctor.getSpecialization().getName() + ")";
            }
            doctorModel.addElement(displayText);
        }
        
        // Tạo JComboBox với model đã có
        JComboBox<String> cbDoctors = new JComboBox<>(doctorModel);
        cbDoctors.setFont(new Font("Arial", Font.PLAIN, 16));
        cbDoctors.setEditable(true); // Cho phép nhập để tìm kiếm nhanh
        cbDoctors.setMaximumRowCount(10); // Hiển thị tối đa 10 hàng trong dropdown
        
        // Thêm tính năng tự động hoàn thành cho JComboBox
        JTextField textField = (JTextField) cbDoctors.getEditor().getEditorComponent();
        textField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                
                String currentText = getText(0, getLength());
                String beforeOffset = currentText.substring(0, offs);
                String afterOffset = currentText.substring(offs, currentText.length());
                String futureText = beforeOffset + str + afterOffset;
                
                // Tìm item phù hợp với futureText
                for (int i = 0; i < cbDoctors.getItemCount(); i++) {
                    String item = cbDoctors.getItemAt(i).toString().toLowerCase();
                    if (item.startsWith(futureText.toLowerCase())) {
                        super.remove(0, getLength());
                        super.insertString(0, cbDoctors.getItemAt(i).toString(), a);
                        textField.setCaretPosition(futureText.length());
                        textField.moveCaretPosition(getLength());
                        return;
                    }
                }
                
                // Nếu không tìm thấy item phù hợp, chỉ chèn chuỗi đầu vào
                super.insertString(offs, str, a);
            }
        });
        
        // Tạo nút tìm kiếm
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Tạo panel chứa ComboBox và nút tìm kiếm
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(cbDoctors, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        
        // Thêm panel tìm kiếm vào form
        addFormField(inputPanel, gbc, "Tìm bác sĩ:", searchPanel, 0);
        
        // Thêm phần code xử lý sự kiện tìm kiếm
        btnSearch.addActionListener(e -> {
            String selectedText = cbDoctors.getSelectedItem() != null ? 
                                  cbDoctors.getSelectedItem().toString() : "";
            
            if (selectedText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Nếu chọn "Hiển thị tất cả", hiển thị lịch làm việc của tất cả bác sĩ
            if (selectedText.equals("Hiển thị tất cả")) {
                // Reset all cells first
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 7; j++) {
                        cellPanels[i][j].setBackground(Color.WHITE);
                        iconLabels[i][j].setText("<html><center>Hiển thị tất cả</center></html>");
                    }
                }
                
                // Tải thông tin lịch trực của tất cả bác sĩ
                String[] shiftNames = {"Sáng", "Chiều", "Tối"};
                String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
                
                // Lấy thông tin lịch trực của tất cả bác sĩ
                Map<String, Map<String, List<Doctor>>> allSchedules = controller.getAllDoctorSchedules();
                
                // Cập nhật UI dựa trên dữ liệu từ database
                for (int i = 0; i < shiftNames.length; i++) {
                    for (int j = 0; j < days.length; j++) {
                        String day = days[j];
                        String shiftName = shiftNames[i];
                        
                        if (allSchedules.containsKey(day) && allSchedules.get(day).containsKey(shiftName)) {
                            List<Doctor> availableDoctors = allSchedules.get(day).get(shiftName);
                            
                            if (availableDoctors != null && !availableDoctors.isEmpty()) {
                                // Format text hiển thị
                                StringBuilder displayText = new StringBuilder("<html>");
                                
                                // Giới hạn số lượng bác sĩ hiển thị
                                int displayLimit = 3; // Chỉ hiển thị tối đa 3 bác sĩ
                                int totalDoctors = availableDoctors.size();
                                
                                for (int k = 0; k < Math.min(displayLimit, totalDoctors); k++) {
                                    Doctor doctor = availableDoctors.get(k);
                                    displayText.append("• ").append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                                                    
                                    if (doctor.getSpecialization() != null) {
                                        displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                    }
                                    displayText.append("<br>");
                                }
                                
                                // Thêm dòng "Xem thêm..." nếu có nhiều hơn số lượng giới hạn
                                if (totalDoctors > displayLimit) {
                                    // In ra console để debug
                                    System.out.println(day + " - " + shiftName + ": Tổng số bác sĩ = " + totalDoctors + ", hiển thị = " + displayLimit);
                                    
                                    // Đảm bảo hiển thị số lượng bác sĩ còn lại chính xác
                                    int remainingDoctors = totalDoctors - displayLimit;
                                    displayText.append("<br><font color='blue'><u>+ " + remainingDoctors + " bác sĩ khác...</u></font>");
                                }
                                displayText.append("</html>");
                                
                                // Tạo tooltip để hiển thị đầy đủ danh sách
                                StringBuilder tooltipText = new StringBuilder("<html>");
                                for (int k = 0; k < totalDoctors; k++) {
                                    Doctor doctor = availableDoctors.get(k);
                                    if (k > 0) tooltipText.append("<br>");
                                    tooltipText.append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                    
                                    if (doctor.getSpecialization() != null) {
                                        tooltipText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                    }
                                }
                                tooltipText.append("</html>");
                                
                                // Cập nhật cell và thêm tooltip
                                iconLabels[i][j].setText(displayText.toString());
                                iconLabels[i][j].setToolTipText(tooltipText.toString());
                            } else {
                                // Nếu không có bác sĩ nào trực ca này
                                iconLabels[i][j].setText("<html><center>Hiển thị tất cả</center></html>");
                                iconLabels[i][j].setToolTipText(null);
                            }
                        }
                    }
                }
                
                return;
            }
            
            // Trích xuất ID bác sĩ từ chuỗi đã chọn
            String doctorId = "";
            if (selectedText.contains(" - ")) {
                doctorId = selectedText.substring(0, selectedText.indexOf(" - "));
            } else {
                doctorId = selectedText; // Nếu người dùng nhập trực tiếp ID
            }
            
            // Lấy dữ liệu lịch làm việc của bác sĩ để hiển thị
            String[] shiftNames = {"Sáng", "Chiều", "Tối"};
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            
            // Reset all cells first
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    cellPanels[i][j].setBackground(Color.WHITE); // Dùng màu trắng thay vì màu nền
                    iconLabels[i][j].setText("<html><center>Không có ca làm việc</center></html>");
                }
            }
            
            // Lấy dữ liệu phân công của bác sĩ này
            Map<String, Map<String, List<Doctor>>> doctorSchedule = controller.getDoctorShiftSchedule(doctorId);
            
            // Cập nhật giao diện dựa trên dữ liệu lịch
            for (int i = 0; i < shiftNames.length; i++) {
                for (int j = 0; j < days.length; j++) {
                    String day = days[j];
                    String shiftName = shiftNames[i]; // Sử dụng tên ca ngắn gọn để khớp với database
                    
                    // Kiểm tra xem có dữ liệu cho ngày và ca này không
                    if (doctorSchedule.containsKey(day) && doctorSchedule.get(day).containsKey(shiftName)) {
                        List<Doctor> availableDoctors = doctorSchedule.get(day).get(shiftName);
                        
                        if (availableDoctors != null && !availableDoctors.isEmpty()) {
                            // Format text hiển thị
                            StringBuilder displayText = new StringBuilder("<html>");
                            
                            // Giới hạn số lượng bác sĩ hiển thị
                            int displayLimit = 3; // Chỉ hiển thị tối đa 3 bác sĩ
                            int totalDoctors = availableDoctors.size();
                            
                            for (int k = 0; k < Math.min(displayLimit, totalDoctors); k++) {
                                Doctor doctor = availableDoctors.get(k);
                                
                                // Highlight bác sĩ đang tìm kiếm bằng chữ đậm
                                if (doctor.getDoctorId().equals(doctorId)) {
                                    displayText.append("• <b>").append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                    
                                    if (doctor.getSpecialization() != null) {
                                        displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                    }
                                    displayText.append("</b>");
                                } else {
                                    displayText.append("• ").append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                    
                                    if (doctor.getSpecialization() != null) {
                                        displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                    }
                                }
                                displayText.append("<br>");
                            }
                            
                            // Thêm dòng "Xem thêm..." nếu có nhiều hơn số lượng giới hạn
                            if (totalDoctors > displayLimit) {
                                int remainingDoctors = totalDoctors - displayLimit;
                                displayText.append("<br><font color='blue'><u>+ " + remainingDoctors + " bác sĩ khác...</u></font>");
                            }
                            
                            displayText.append("</html>");
                            
                            // Tạo tooltip để hiển thị đầy đủ danh sách
                            StringBuilder tooltipText = new StringBuilder("<html>");
                            for (int k = 0; k < totalDoctors; k++) {
                                Doctor doctor = availableDoctors.get(k);
                                if (k > 0) tooltipText.append("<br>");
                                tooltipText.append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                
                                if (doctor.getSpecialization() != null) {
                                    tooltipText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                }
                            }
                            tooltipText.append("</html>");
                            
                            // Cập nhật cell và thêm tooltip
                            iconLabels[i][j].setText(displayText.toString());
                            iconLabels[i][j].setToolTipText(tooltipText.toString());
                        }
                    }
                }
            }
        });
        
        // Thêm phần xử lý khi nhấn Enter trong ComboBox
        cbDoctors.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSearch.doClick();
                }
            }
        });
    
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
    
        // Tạo các tiêu đề cho bảng lịch
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
    
        // QUAN TRỌNG: Khởi tạo giá trị cho biến thành viên cellPanels và iconLabels
        cellPanels = new JPanel[3][7];
        iconLabels = new JLabel[3][7];
       
        for (int i = 0; i < shifts.length; i++) {
            calendarPanel.add(createHeaderCell(shifts[i]));
            for (int j = 0; j < 7; j++) {
                final int shiftIndex = i;
                final int dayIndex = j;
                
                // Thay đổi cấu trúc cell để hiển thị thông tin bác sĩ
                JPanel cellPanel = new JPanel();
                cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.Y_AXIS));
                cellPanel.setBackground(Color.WHITE); 
                cellPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
                
                // Label để hiển thị thông tin bác sĩ
                JLabel infoLabel = new JLabel("<html><center>Không có bác sĩ</center></html>");
                infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                
                cellPanel.add(Box.createVerticalGlue());
                cellPanel.add(infoLabel);
                cellPanel.add(Box.createVerticalGlue());
                
                // Lưu tham chiếu vào mảng
                cellPanels[i][j] = cellPanel;
                iconLabels[i][j] = infoLabel;
                
                // THÊM: Thêm MouseListener cho cả infoLabel
                infoLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Mở dialog chọn bác sĩ
                        showDoctorSelectorDialog(dayIndex, shiftIndex, iconLabels[shiftIndex][dayIndex], cellPanels[shiftIndex][dayIndex]);
                    }
                });
                
                // Đảm bảo cả label và panel đều có cursor hand để người dùng biết có thể click
                infoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Vẫn giữ MouseListener cho cellPanel
                cellPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showDoctorSelectorDialog(dayIndex, shiftIndex, iconLabels[shiftIndex][dayIndex], cellPanels[shiftIndex][dayIndex]);
                    }
                });
                
                cellPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                calendarPanel.add(cellPanel);
            }
        }
    
        schedulePanel.add(scheduleHeader, BorderLayout.NORTH);
        schedulePanel.add(calendarPanel, BorderLayout.CENTER);
    
        // Legend for Schedule
        /* JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    
        JPanel workingLegend = createLegendItemWithIcon("Ca làm việc", new Color(40, 167, 69, 60), workingIcon);
        JPanel notWorkingLegend = createLegendItemWithIcon("Không làm việc", new Color(240, 240, 240), notWorkingIcon);
    
        legendPanel.add(workingLegend);
        legendPanel.add(notWorkingLegend); */

        // Thay đổi phần legend panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel assignedLegend = createLegendItem("Có bác sĩ trực", new Color(40, 167, 69, 60));
        JPanel highlightedLegend = createLegendItem("Bác sĩ được tìm", new Color(40, 167, 69, 120));
        JPanel emptyLegend = createLegendItem("Không có bác sĩ", new Color(240, 240, 240));
        
        legendPanel.add(assignedLegend);
        legendPanel.add(highlightedLegend);
        legendPanel.add(emptyLegend);
    
        // Save Button
        JButton btnSave = new JButton("Lưu lịch");
        btnSave.setFont(new Font("Arial", Font.BOLD, 16));
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(200, 45));
        
        btnSearch.addActionListener(e -> {
            String selectedText = cbDoctors.getSelectedItem() != null ? 
                                  cbDoctors.getSelectedItem().toString() : "";
            
            if (selectedText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Trích xuất ID bác sĩ từ chuỗi đã chọn
            String doctorId = "";
            if (selectedText.contains(" - ")) {
                doctorId = selectedText.substring(0, selectedText.indexOf(" - "));
            } else {
                doctorId = selectedText; // Nếu người dùng nhập trực tiếp ID
            }
            
            // Lấy dữ liệu lịch làm việc của bác sĩ để hiển thị
            /* String[] shiftNames = {"Sáng", "Chiều", "Tối"};
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            
            // Reset all cells first
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    cellPanels[i][j].setBackground(Color.WHITE); // Dùng màu trắng thay vì màu nền
                    iconLabels[i][j].setText("<html><center>Không có bác sĩ</center></html>");
                }
            }
            
            // Lấy dữ liệu phân công của bác sĩ này
            Map<String, Map<String, List<Doctor>>> doctorSchedule = controller.getDoctorShiftSchedule(doctorId);
            
            // Cập nhật giao diện dựa trên dữ liệu lịch
            for (int i = 0; i < shifts.length; i++) {
                for (int j = 0; j < days.length; j++) {
                    String day = days[j];
                    String shiftName = shiftNames[i]; // Sử dụng tên ca ngắn gọn để khớp với database
                    
                    // Kiểm tra xem có dữ liệu cho ngày và ca này không
                    if (doctorSchedule.containsKey(day) && doctorSchedule.get(day).containsKey(shiftName)) {
                        List<Doctor> availableDoctors = doctorSchedule.get(day).get(shiftName);
                        
                        if (availableDoctors != null && !availableDoctors.isEmpty()) {
                            // Format text hiển thị
                            StringBuilder displayText = new StringBuilder("<html>");
                            // Giới hạn số lượng bác sĩ hiển thị
                            int displayLimit = 3; // Chỉ hiển thị tối đa 3 bác sĩ
                            int totalDoctors = availableDoctors.size();
                                                        
                            for (int k = 0; k < Math.min(displayLimit, totalDoctors); k++) {
                                Doctor doctor = availableDoctors.get(k);
                                
                                // Highlight bác sĩ đang tìm kiếm bằng chữ đậm
                                if (doctor.getDoctorId().equals(doctorId)) {
                                    displayText.append("• <b>").append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                    
                                    if (doctor.getSpecialization() != null) {
                                        displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                    }
                                    displayText.append("</b>");
                                } else {
                                    displayText.append("• ").append(doctor.getDoctorId()).append(": ")
                                            .append(doctor.getFullName());
                                    
                                    if (doctor.getSpecialization() != null) {
                                        displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                                    }
                                }
                                displayText.append("<br>");
                            }
                            displayText.append("</html>");
                            
                            // Cập nhật cell
                            iconLabels[i][j].setText(displayText.toString());
                        }
                    }
                }
            } */
            updateScheduleWithDoctorId(doctorId, cellPanels, iconLabels, shifts);
        });
    
        btnSave.addActionListener(e -> {
            String selectedText = cbDoctors.getSelectedItem() != null ? 
                                cbDoctors.getSelectedItem().toString() : "";
            
            if (selectedText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ để lưu lịch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Trích xuất ID bác sĩ từ chuỗi đã chọn
            String doctorId = "";
            if (selectedText.contains(" - ")) {
                doctorId = selectedText.substring(0, selectedText.indexOf(" - "));
            } else {
                doctorId = selectedText; // Nếu người dùng nhập trực tiếp ID
            }
            
            // Lấy danh sách các ca đã phân công cho bác sĩ này
            String[] shiftNames = {"Sáng", "Chiều", "Tối"};
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            
            // Tạo danh sách các ca đã phân công cho bác sĩ dựa trên giao diện hiện tại
            List<Map<String, String>> assignedShifts = new ArrayList<>();
            
            for (int i = 0; i < shiftNames.length; i++) {
                for (int j = 0; j < days.length; j++) {
                    String cellText = iconLabels[i][j].getText();
                    if (cellText.contains(doctorId)) {
                        // Bác sĩ này đã được phân công vào ca này
                        Map<String, String> assignment = new HashMap<>();
                        assignment.put("doctorId", doctorId);
                        assignment.put("day", days[j]);
                        assignment.put("shift", shiftNames[i]);
                        assignedShifts.add(assignment);
                    }
                }
            }
            
            // Lưu toàn bộ lịch làm việc của bác sĩ này
            boolean success = controller.updateDoctorFullSchedule(doctorId, assignedShifts);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã lưu lịch làm việc cho bác sĩ " + selectedText + " thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Tự động làm mới lịch sau khi lưu
                btnSearch.doClick();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể lưu lịch làm việc! Vui lòng thử lại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnSave);
    
        // Assemble the form - Đảm bảo thứ tự các thành phần được thêm đúng
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

        // Tải thông tin bác sĩ trực từ database khi khởi tạo màn hình
        String[] shiftNames = {"Sáng", "Chiều", "Tối"};
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
        
        // Lấy thông tin lịch trực của tất cả bác sĩ
        Map<String, Map<String, List<Doctor>>> allSchedules = controller.getAllDoctorSchedules();
        
        // Cập nhật UI dựa trên dữ liệu từ database
        for (int i = 0; i < shifts.length; i++) {
            for (int j = 0; j < days.length; j++) {
                String day = days[j];
                String shiftName = shiftNames[i];
                
                if (allSchedules.containsKey(day) && allSchedules.get(day).containsKey(shiftName)) {
                    List<Doctor> availableDoctors = allSchedules.get(day).get(shiftName);
                    
                    if (availableDoctors != null && !availableDoctors.isEmpty()) {
                        // Format text hiển thị
                        StringBuilder displayText = new StringBuilder("<html>");
                        
                        // Giới hạn số lượng bác sĩ hiển thị
                        int displayLimit = 3; // Chỉ hiển thị tối đa 3 bác sĩ
                        int totalDoctors = availableDoctors.size();
                        
                        for (int k = 0; k < Math.min(displayLimit, totalDoctors); k++) {
                            Doctor doctor = availableDoctors.get(k);
                            displayText.append("• ").append(doctor.getDoctorId()).append(": ")
                                    .append(doctor.getFullName());
                                                            
                            if (doctor.getSpecialization() != null) {
                                displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                            }
                            displayText.append("<br>");
                        }
                        
                        // Thêm dòng "Xem thêm..." nếu có nhiều hơn số lượng giới hạn
                        if (totalDoctors > displayLimit) {
                            int remainingDoctors = totalDoctors - displayLimit;
                            displayText.append("<br><font color='blue'><u>+ " + remainingDoctors + " bác sĩ khác...</u></font>");
                        }
                        
                        displayText.append("</html>");
                        
                        // Tạo tooltip để hiển thị đầy đủ danh sách
                        StringBuilder tooltipText = new StringBuilder("<html>");
                        for (int k = 0; k < totalDoctors; k++) {
                            Doctor doctor = availableDoctors.get(k);
                            if (k > 0) tooltipText.append("<br>");
                            tooltipText.append(doctor.getDoctorId()).append(": ")
                                      .append(doctor.getFullName());
                            
                            if (doctor.getSpecialization() != null) {
                                tooltipText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                            }
                        }
                        tooltipText.append("</html>");
                        
                        // Cập nhật cell và thêm tooltip
                        iconLabels[i][j].setText(displayText.toString());
                        iconLabels[i][j].setToolTipText(tooltipText.toString());
                    }
                }
            }
        }
    }

    // Phương thức để cập nhật lịch dựa trên ID bác sĩ
        // Phương thức để cập nhật lịch dựa trên ID bác sĩ
    private void updateScheduleWithDoctorId(String doctorId, JPanel[][] cellPanels, JLabel[][] iconLabels, String[] shifts) {
        if (doctorId.isEmpty()) {
            return;
        }
        
        // Chuyển đổi tên ca từ shifts (với giờ) sang shiftNames (không có giờ)
        String[] shiftNames = {"Sáng", "Chiều", "Tối"};
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
        
        // Reset all cells first
        for (int i = 0; i < shiftNames.length; i++) {
            for (int j = 0; j < days.length; j++) {
                cellPanels[i][j].setBackground(Color.WHITE);
                iconLabels[i][j].setText("<html><center>Không có bác sĩ</center></html>");
                iconLabels[i][j].setToolTipText(null); // Xóa tooltip
            }
        }
        
        // Lấy dữ liệu phân công của bác sĩ này
        Map<String, Map<String, List<Doctor>>> doctorSchedule = controller.getDoctorShiftSchedule(doctorId);
        
        // Cập nhật giao diện dựa trên dữ liệu lịch
        for (int i = 0; i < shiftNames.length; i++) {
            for (int j = 0; j < days.length; j++) {
                String day = days[j];
                String shiftName = shiftNames[i];
                
                // Kiểm tra xem có dữ liệu cho ngày và ca này không
                if (doctorSchedule.containsKey(day) && doctorSchedule.get(day).containsKey(shiftName)) {
                    List<Doctor> availableDoctors = doctorSchedule.get(day).get(shiftName);
                    
                    if (availableDoctors != null && !availableDoctors.isEmpty()) {
                        // THAY ĐỔI Ở ĐÂY: Chỉ hiển thị bác sĩ được tìm kiếm
                        Doctor foundDoctor = null;
                        for (Doctor doctor : availableDoctors) {
                            if (doctor.getDoctorId().equals(doctorId)) {
                                foundDoctor = doctor;
                                break;
                            }
                        }
                        
                        if (foundDoctor != null) {
                            // Nếu tìm thấy bác sĩ trong ca này, chỉ hiển thị bác sĩ đó
                            StringBuilder displayText = new StringBuilder("<html>");
                            displayText.append("• <b>").append(foundDoctor.getDoctorId()).append(": ")
                                     .append(foundDoctor.getFullName());
                            
                            if (foundDoctor.getSpecialization() != null) {
                                displayText.append(" (").append(foundDoctor.getSpecialization().getName()).append(")");
                            }
                            displayText.append("</b>");
                            displayText.append("</html>");
                            
                            // Cập nhật cell với màu nền highlight
                            iconLabels[i][j].setText(displayText.toString());
                            cellPanels[i][j].setBackground(new Color(40, 167, 69, 120)); // Màu xanh đậm hơn
                            
                            // Chỉ hiển thị tooltip cho bác sĩ được tìm
                            StringBuilder tooltipText = new StringBuilder("<html>");
                            tooltipText.append(foundDoctor.getDoctorId()).append(": ")
                                      .append(foundDoctor.getFullName());
                            
                            if (foundDoctor.getSpecialization() != null) {
                                tooltipText.append(" (").append(foundDoctor.getSpecialization().getName()).append(")");
                            }
                            tooltipText.append("</html>");
                            
                            iconLabels[i][j].setToolTipText(tooltipText.toString());
                        }
                    }
                }
            }
        }
    }
    
    private void showDoctorSelectorDialog(int dayIndex, int shiftIndex, JLabel infoLabel, JPanel cellPanel) {
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
        String[] shifts = {"Sáng", "Chiều", "Tối"};
        
        // Tạo dialog để hiển thị danh sách bác sĩ có thể chọn
        JDialog dialog = new JDialog(this, "Chọn bác sĩ trực " + shifts[shiftIndex] + " " + days[dayIndex], true);
        dialog.setLayout(new BorderLayout());
        
        // Lấy danh sách bác sĩ từ controller
        List<Doctor> availableDoctors = controller.getAllDoctors();
        
        // Panel chứa danh sách checkbox bác sĩ
        JPanel doctorPanel = new JPanel();
        doctorPanel.setLayout(new BoxLayout(doctorPanel, BoxLayout.Y_AXIS));
        
        // Map để lưu trạng thái checkbox
        Map<String, JCheckBox> checkboxMap = new HashMap<>();
        
        for (Doctor doctor : availableDoctors) {
            JCheckBox checkbox = new JCheckBox(
                doctor.getDoctorId() + " - " + doctor.getFullName() + 
                (doctor.getSpecialization() != null ? " (" + doctor.getSpecialization().getName() + ")" : "")
            );
            checkbox.setActionCommand(doctor.getDoctorId());
            doctorPanel.add(checkbox);
            checkboxMap.put(doctor.getDoctorId(), checkbox);
        }
        
        // Kiểm tra xem đã có bác sĩ nào được phân công chưa
        String day = days[dayIndex];
        String shift = shifts[shiftIndex];
        
        // Lấy bác sĩ đã được phân công cho ca này
        List<Doctor> assignedDoctors = controller.getDoctorsForShift(day, shift);
        
        // Tự động chọn các bác sĩ đã được phân công
        for (Doctor doctor : assignedDoctors) {
            JCheckBox checkbox = checkboxMap.get(doctor.getDoctorId());
            if (checkbox != null) {
                checkbox.setSelected(true);
            }
        }
        
        // Thêm thanh cuộn nếu có nhiều bác sĩ
        JScrollPane scrollPane = new JScrollPane(doctorPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Thêm label hiển thị số lượng bác sĩ đã chọn
        JLabel doctorCountLabel = new JLabel("Số lượng bác sĩ trực: " + assignedDoctors.size());
        doctorCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        doctorCountLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        doctorCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel phía dưới chứa nút và thông tin số lượng bác sĩ
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        // Panel cho các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Hủy");
        
        // Thêm sự kiện cập nhật số lượng bác sĩ khi check/uncheck các checkbox
        for (JCheckBox checkbox : checkboxMap.values()) {
            checkbox.addItemListener(e -> {
                int selectedCount = 0;
                for (JCheckBox cb : checkboxMap.values()) {
                    if (cb.isSelected()) {
                        selectedCount++;
                    }
                }
                doctorCountLabel.setText("Số lượng bác sĩ trực: " + selectedCount);
            });
        }
        
        okButton.addActionListener(e -> {
            // Thu thập các bác sĩ được chọn
            List<String> selectedDoctorIds = new ArrayList<>();
            StringBuilder displayText = new StringBuilder("<html>");
            
            // Đếm số lượng bác sĩ đã chọn để giới hạn hiển thị
            int totalSelected = 0;
            for (Doctor doctor : availableDoctors) {
                JCheckBox checkbox = checkboxMap.get(doctor.getDoctorId());
                if (checkbox != null && checkbox.isSelected()) {
                    totalSelected++;
                }
            }
            
            // Số lượng bác sĩ tối đa hiển thị
            int displayLimit = 3;
            int count = 0;
            
            // Thêm bác sĩ vào danh sách và hiển thị
            for (Doctor doctor : availableDoctors) {
                JCheckBox checkbox = checkboxMap.get(doctor.getDoctorId());
                if (checkbox != null && checkbox.isSelected()) {
                    selectedDoctorIds.add(doctor.getDoctorId());
                    
                    // Chỉ hiển thị trong giới hạn
                    if (count < displayLimit) {
                        // Thêm vào text hiển thị với dấu bullet
                        displayText.append("• ").append(doctor.getDoctorId()).append(": ")
                                 .append(doctor.getFullName());
                        
                        // Thêm chuyên khoa nếu có
                        if (doctor.getSpecialization() != null) {
                            displayText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                        }
                        
                        displayText.append("<br>");
                        count++;
                    }
                }
            }
            
            // Thêm dòng "Xem thêm..." nếu có nhiều hơn số lượng giới hạn
            if (totalSelected > displayLimit) {
                int remainingDoctors = totalSelected - displayLimit;
                displayText.append("<font color='blue'><u>+ " + remainingDoctors + " bác sĩ khác...</u></font>");
            }
            
            // Nếu không có bác sĩ nào được chọn, hiển thị text mặc định
            if (selectedDoctorIds.isEmpty()) {
                displayText = new StringBuilder("<html>Hiển thị tất cả</html>");
            } else {
                displayText.append("</html>");
            }
            
            // Tạo tooltip để hiển thị đầy đủ danh sách
            StringBuilder tooltipText = new StringBuilder("<html>");
            for (int k = 0; k < selectedDoctorIds.size(); k++) {
                String doctorId = selectedDoctorIds.get(k);
                Doctor doctor = null;
                for (Doctor d : availableDoctors) {
                    if (d.getDoctorId().equals(doctorId)) {
                        doctor = d;
                        break;
                    }
                }
                
                if (doctor != null) {
                    if (k > 0) tooltipText.append("<br>");
                    tooltipText.append(doctor.getDoctorId()).append(": ")
                              .append(doctor.getFullName());
                    
                    if (doctor.getSpecialization() != null) {
                        tooltipText.append(" (").append(doctor.getSpecialization().getName()).append(")");
                    }
                }
            }
            tooltipText.append("</html>");
            
            // Cập nhật label và tooltip
            infoLabel.setText(displayText.toString());
            if (!selectedDoctorIds.isEmpty()) {
                infoLabel.setToolTipText(tooltipText.toString());
            } else {
                infoLabel.setToolTipText(null);
            }
            
            // Lưu lịch vào database
            boolean success = controller.saveDoctorShiftAssignments(day, shift, selectedDoctorIds);
            if (success) {
                System.out.println("Đã lưu lịch thành công");
            } else {
                System.out.println("Lưu lịch thất bại");
                JOptionPane.showMessageDialog(dialog, 
                "Không thể lưu lịch làm việc! Vui lòng thử lại.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Thêm doctorCountLabel vào panel phía dưới
        bottomPanel.add(doctorCountLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        legendItem.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        
        legendItem.add(colorBox);
        legendItem.add(label);
        
        return legendItem;
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

        // Thêm panel chứa nút xuất báo cáo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton exportExcelBtn = new JButton("Xuất Excel");
        exportExcelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exportExcelBtn.setBackground(new Color(40, 167, 69));
        exportExcelBtn.setForeground(Color.WHITE);
        exportExcelBtn.setFocusPainted(false);
        exportExcelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton exportPdfBtn = new JButton("Xuất PDF");
        exportPdfBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exportPdfBtn.setBackground(new Color(220, 53, 69));
        exportPdfBtn.setForeground(Color.WHITE);
        exportPdfBtn.setFocusPainted(false);
        exportPdfBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(exportExcelBtn);
        buttonPanel.add(exportPdfBtn);
        
        // Thêm sự kiện cho nút xuất Excel
        exportExcelBtn.addActionListener(e -> {
            if (doctors == null || doctors.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không có dữ liệu để xuất!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu danh sách bác sĩ");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            fileChooser.setSelectedFile(new File("DanhSachBacSi.xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                boolean success = controller.exportDoctorsToExcel(doctors, filePath);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất file Excel thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất file Excel thất bại!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Thêm sự kiện cho nút xuất PDF
        exportPdfBtn.addActionListener(e -> {
            if (doctors == null || doctors.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không có dữ liệu để xuất!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu danh sách bác sĩ");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
            fileChooser.setSelectedFile(new File("DanhSachBacSi.pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                
                boolean success = controller.exportDoctorsToPdf(doctors, filePath);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất file PDF thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất file PDF thất bại!", 
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
            cell.setToolTipText("Có bác sĩ đang làm việc");
        } else if (status.equals("Hết ca làm việc")) {
            iconLabel.setIcon(finishedIcon);
            cell.setToolTipText("Ca làm việc đã kết thúc");
        } else {
            iconLabel.setIcon(notWorkingIcon);
            cell.setToolTipText("Không có bác sĩ làm việc");
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
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                String testAdminId = "ADM-001";
                AdminView adminView = new AdminView(testAdminId);
                adminView.setVisible(true);
                System.out.println("Đã khởi tạo giao diện quản trị với ID: " + testAdminId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi khởi động giao diện: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}