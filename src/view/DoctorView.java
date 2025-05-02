package view;

import controller.DoctorController;
import model.entity.MedicalRecord;
import model.entity.Patient;
import model.enums.Gender;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;
import java.util.Date;
import java.time.ZoneId;

public class DoctorView extends JFrame {
    private JPanel contentPanel;
    private JButton btnHome, btnAdd, btnView, btnBook, btnDel, btnLogout;
    private JButton currentSelectedButton;
    private DoctorController controller;
    // private JTextField txtName, txtBirthDate, txtAddress, txtPhone, txtDisease, txtPatientId, txtDate, txtEmail;
    private JTextField txtName, txtAddress, txtPhone, txtDisease, txtPatientId, txtDate, txtEmail;
    private JDatePickerImpl datePicker;
    private JComboBox<Gender> cbGender;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnExamination;

    private ImageIcon workingIcon;
    private ImageIcon finishedIcon;
    private ImageIcon notWorkingIcon;

    public class DateLabelFormatter extends AbstractFormatter {
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

        /**
     * Renderer cho nút trong JTable
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(new Color(0, 123, 255));
            setFocusPainted(false);
        }
    
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Chọn");
            return this;
        }
    }
    
    /**
     * Editor cho nút trong JTable
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;
        private JTable table;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 123, 255));
            button.setFocusPainted(false);
            
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            this.selectedRow = row;
            label = "Chọn";
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Chọn hàng khi nhấn nút
                table.setRowSelectionInterval(selectedRow, selectedRow);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    public DoctorView(String doctorId) {
        this.controller = new DoctorController(this, doctorId);
        setTitle("Bảng điều khiển bác sĩ");
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
                
                // Thay đổi kích thước icon lớn hơn
                workingIcon = new ImageIcon(workingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                finishedIcon = new ImageIcon(finishedIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                notWorkingIcon = new ImageIcon(notWorkingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            } else {
                System.out.println("Không tìm thấy một hoặc nhiều file icons.");
                // createFallbackIcons();
            }
        } catch (Exception e) {
            System.out.println("Không thể tải icons: " + e.getMessage());
            e.printStackTrace();
            // createFallbackIcons();
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

        JLabel menuTitle = new JLabel("Doctor Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10);
        gbc.weighty = 0.1;
        leftPanel.add(menuTitle, gbc);

        btnHome = createButton("Trang chủ");
        btnAdd = createButton("Thêm bệnh nhân");
        btnView = createButton("Xem danh sách bệnh nhân");
        btnBook = createButton("Đặt lịch hẹn");
        btnDel = createButton("Xóa bệnh nhân");
        btnExamination = createButton("Khám bệnh");
        btnLogout = createButton("Đăng xuất");

        setSelectedButton(btnHome);

        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.weighty = 0.0;

        gbc.gridy = 1;
        leftPanel.add(btnHome, gbc);
        gbc.gridy = 2;
        leftPanel.add(btnAdd, gbc);
        gbc.gridy = 3;
        leftPanel.add(btnView, gbc);
        gbc.gridy = 4;
        leftPanel.add(btnBook, gbc);
        gbc.gridy = 5;
        leftPanel.add(btnExamination, gbc);
        gbc.gridy = 6;
        leftPanel.add(btnDel, gbc);
        
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
        btnAdd.addActionListener(e -> controller.showAddPatientForm());
        btnView.addActionListener(e -> controller.showPatientList());
        btnBook.addActionListener(e -> controller.showBookAppointment());
        btnExamination.addActionListener(e -> controller.showExamination());
        btnDel.addActionListener(e -> controller.showDeletePatientForm());
        btnLogout.addActionListener(e -> logout());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    public JButton getBtnExamination() {
        return btnExamination;
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

    public void showHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(new Color(245, 245, 245));

        // Header section - Welcome message and date
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        
        // 1. Thông tin bác sĩ và tiêu đề
        JLabel welcomeLabel = new JLabel("Chào mừng, BS. " + controller.getDoctorName(), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(34, 45, 65));
        
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString(), SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        // 2. Thông tin chi tiết bác sĩ
        JPanel doctorInfoPanel = new JPanel(new BorderLayout());
        doctorInfoPanel.setBackground(Color.WHITE);
        doctorInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Avatar và thông tin cá nhân
        JPanel personalPanel = new JPanel(new BorderLayout(20, 0));
        personalPanel.setOpaque(false);
        
        // Avatar placeholder
        JPanel avatarPanel = new JPanel();
        avatarPanel.setPreferredSize(new Dimension(150, 150));
        avatarPanel.setBackground(new Color(41, 128, 185));
        JLabel avatarLabel = new JLabel("BS", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 48));
        avatarLabel.setForeground(Color.WHITE);
        avatarPanel.add(avatarLabel);
        
        // Thông tin cá nhân
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel("Bác sĩ: " + controller.getDoctorName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel specialtyLabel = new JLabel("Chuyên khoa: " + controller.getDoctorSpecialty());
        specialtyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel emailLabel = new JLabel("Email: " + controller.getDoctorEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel phoneLabel = new JLabel("Điện thoại: " + controller.getDoctorPhone());
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel addressLabel = new JLabel("Địa chỉ: " + controller.getDoctorAddress());
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        infoPanel.add(nameLabel);
        infoPanel.add(specialtyLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(addressLabel);
        
        personalPanel.add(avatarPanel, BorderLayout.WEST);
        personalPanel.add(infoPanel, BorderLayout.CENTER);
        
        doctorInfoPanel.add(personalPanel, BorderLayout.NORTH);

        // 3. Dashboard - Thống kê
        JPanel dashboardPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));
        dashboardPanel.setOpaque(false);
        
        // Lấy số liệu thống kê thực từ controller
        int totalPatients = controller.getTotalPatients();
        int todayAppointments = controller.getTodayAppointments();
        int waitingPatients = controller.getWaitingPatients();
        int completedToday = controller.getCompletedAppointments();

        JPanel patientsCard = createDashboardCard("Bệnh nhân đã khám", String.valueOf(totalPatients), new Color(41, 128, 185));
        JPanel appointmentsCard = createDashboardCard("Cuộc hẹn hôm nay", String.valueOf(todayAppointments), new Color(39, 174, 96));
        JPanel pendingCard = createDashboardCard("Đang chờ khám", String.valueOf(waitingPatients), new Color(230, 126, 34));
        JPanel completedCard = createDashboardCard("Hoàn thành hôm nay", String.valueOf(completedToday), new Color(142, 68, 173));

        dashboardPanel.add(patientsCard);
        dashboardPanel.add(appointmentsCard);
        dashboardPanel.add(pendingCard);
        dashboardPanel.add(completedCard);
        
        // 4. Lịch làm việc của bác sĩ theo thời gian thực
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel scheduleHeader = new JPanel(new BorderLayout());
        scheduleHeader.setOpaque(false);
        scheduleHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel scheduleLabel = new JLabel("Lịch làm việc trong tuần", SwingConstants.LEFT);
        scheduleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Hiển thị ngày hiện tại và giờ hiện tại
        LocalDate today = LocalDate.now();
        String dayOfWeek = getDayOfWeekInVietnamese(today.getDayOfWeek());
        LocalTime now = LocalTime.now();
        String currentShift = getCurrentShift(now);

        JLabel todayLabel = new JLabel("Hôm nay: " + dayOfWeek + ", " + today + " - " + now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), SwingConstants.RIGHT);
        todayLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        scheduleHeader.add(scheduleLabel, BorderLayout.WEST);
        scheduleHeader.add(todayLabel, BorderLayout.EAST);

        // Tạo lịch hiển thị theo dạng lưới
        JPanel calendarPanel = new JPanel(new GridLayout(4, 8));
        calendarPanel.setBackground(Color.WHITE);

        // Header cho lịch
        calendarPanel.add(createHeaderCell("Ca / Ngày"));
        calendarPanel.add(createHeaderCell("Thứ Hai"));
        calendarPanel.add(createHeaderCell("Thứ Ba"));
        calendarPanel.add(createHeaderCell("Thứ Tư"));
        calendarPanel.add(createHeaderCell("Thứ Năm"));
        calendarPanel.add(createHeaderCell("Thứ Sáu"));
        calendarPanel.add(createHeaderCell("Thứ Bảy"));
        calendarPanel.add(createHeaderCell("Chủ Nhật"));

        // Định nghĩa ca làm việc với giờ chính xác
        String[] shifts = {
            "Sáng (7:00-11:30)", 
            "Chiều (13:30-17:00)", 
            "Tối (17:00-7:00)"
        };
        LocalTime[] shiftStartTimes = {
            LocalTime.of(7, 0),
            LocalTime.of(13, 30),
            LocalTime.of(17, 0)
        };
        LocalTime[] shiftEndTimes = {
            LocalTime.of(11, 30),
            LocalTime.of(17, 0),
            LocalTime.of(7, 0) // ca tối kéo dài đến 7h sáng hôm sau
        };

        // Lấy dữ liệu lịch làm việc từ controller
        String[][] scheduleData = controller.getWeeklySchedule();

        // Thêm các ca làm việc với giờ chính xác và trạng thái theo thời gian thực
        for (int i = 0; i < shifts.length; i++) {
            calendarPanel.add(createHeaderCell(shifts[i]));
            
            for (int j = 0; j < 7; j++) {
                // Lưu trạng thái ban đầu từ cơ sở dữ liệu
                final String initialStatus = scheduleData[i][j];
                
                // Biến để lưu trạng thái hiển thị và màu sắc
                String displayStatus;
                Color bgColor;
                
                // Xác định trạng thái theo thời gian thực nếu là ngày hiện tại
                if (j == today.getDayOfWeek().getValue() - 1) { // Ngày hiện tại
                    if (initialStatus.equals("Đang làm việc")) {
                        // Kiểm tra xem có đang trong ca làm việc hiện tại không
                        LocalTime startTime = shiftStartTimes[i];
                        LocalTime endTime = shiftEndTimes[i];
                        
                        // Xử lý đặc biệt cho ca tối (qua đêm)
                        boolean isInShift;
                        if (i == 2) { // Ca tối
                            isInShift = (now.isAfter(startTime) || now.equals(startTime)) || 
                                        (now.isBefore(endTime) || now.equals(endTime));
                        } else {
                            isInShift = (now.isAfter(startTime) || now.equals(startTime)) && 
                                        (now.isBefore(endTime) || now.equals(endTime));
                        }
                        
                        if (isInShift) {
                            displayStatus = "Đang làm việc";
                            bgColor = new Color(40, 167, 69, 80); // Xanh lá đậm hơn
                        } else {
                            displayStatus = "Hết ca làm việc";
                            bgColor = new Color(255, 193, 7, 100); // Xám
                        }
                    // } else if (initialStatus.equals("Hết ca làm việc")) {
                    //     displayStatus = "Hết ca làm việc";
                    //     bgColor = new Color(255, 193, 7, 100); // Vàng
                    } else {
                        // displayStatus = "Không làm việc";
                        displayStatus = initialStatus;
                        if (displayStatus.equals("Hết ca làm việc")) {
                            bgColor = new Color(255, 193, 7, 100);
                        } else {
                            bgColor = new Color(240, 240, 240); // Xám nhạt
                        }
                    }
                } 
                else {
                    // Các ngày khác cũng cần kiểm tra thời gian thực
                    displayStatus = initialStatus;
                    
                    // Chỉ cập nhật hiển thị (không cập nhật database)
                    LocalDate cellDate = today.minusDays(today.getDayOfWeek().getValue() - 1 - j);
                    
                    if (initialStatus.equals("Đang làm việc")) {
                        // Kiểm tra xem đây có phải là ngày đã qua hoặc ca đã qua trong ngày hiện tại không
                        boolean isPastDay = cellDate.isBefore(today);
                        boolean isPastShiftOnToday = cellDate.equals(today) && (
                            (i == 0 && now.isAfter(LocalTime.of(11, 30))) || 
                            (i == 1 && now.isAfter(LocalTime.of(17, 0)))
                        );
                        
                        if (isPastDay || isPastShiftOnToday) {
                            displayStatus = "Hết ca làm việc";
                            bgColor = new Color(255, 193, 7, 100); // Màu vàng không trong suốt
                        } else {
                            bgColor = new Color(40, 167, 69, 80); // Màu xanh không trong suốt
                        }
                    } else if (initialStatus.equals("Hết ca làm việc")) {
                        bgColor = new Color(255, 193, 7, 100); // Màu vàng không trong suốt
                    } else {
                        bgColor = new Color(240, 240, 240); // Màu xám nhạt không trong suốt
                    }
                }
                
                // Đánh dấu ca hiện tại
                boolean isCurrentShift = j == today.getDayOfWeek().getValue() - 1 && 
                                        shifts[i].startsWith(currentShift);
                
                // Tạo cell với trạng thái đã tính toán
                JPanel cell = createScheduleCell(displayStatus, bgColor, isCurrentShift);
                
                calendarPanel.add(cell);
            }
        }

        schedulePanel.add(scheduleHeader, BorderLayout.NORTH);
        schedulePanel.add(calendarPanel, BorderLayout.CENTER);

        // Thêm phần chú thích cho lịch làm việc bên dưới lịch
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel workingLegend = createLegendItemWithIcon("Ca làm việc", new Color(40, 167, 69, 60), workingIcon);
        JPanel finishedLegend = createLegendItemWithIcon("Hết ca làm việc", new Color(255, 193, 7, 100), finishedIcon);
        JPanel notWorkingLegend = createLegendItemWithIcon("Không làm việc", new Color(240, 240, 240), notWorkingIcon);
        
        legendPanel.add(workingLegend);
        legendPanel.add(finishedLegend);
        legendPanel.add(notWorkingLegend);

        // Thông tin về ca làm việc hiện tại
        JPanel currentShiftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentShiftPanel.setOpaque(false);
        currentShiftPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String currentShiftInfo = controller.getCurrentShiftInfo();
        JLabel currentShiftLabel = new JLabel("Ca hiện tại: " + currentShiftInfo);
        currentShiftLabel.setFont(new Font("Arial", Font.BOLD, 14));

        currentShiftPanel.add(currentShiftLabel);

        // Tạo một panel để chứa cả legend và thông tin ca hiện tại
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        // Thêm nút cập nhật tổng ở góc dưới bên phải
        JButton updateAllBtn = new JButton("Cập nhật lịch làm việc");
        updateAllBtn.setFont(new Font("Arial", Font.BOLD, 12));
        updateAllBtn.setForeground(Color.WHITE);
        updateAllBtn.setBackground(new Color(0, 123, 255));
        updateAllBtn.setFocusPainted(false);
        updateAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateAllBtn.addActionListener(e -> {
            // Không gọi controller.updateAllPassedShifts() nữa
            // Chỉ làm mới giao diện để áp dụng logic hiển thị mới
            contentPanel.removeAll();
            controller.showHome();
            JOptionPane.showMessageDialog(this, 
                "Đã cập nhật hiển thị lịch làm việc!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Panel cho nút cập nhật
        JPanel updateBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        updateBtnPanel.setOpaque(false);
        updateBtnPanel.add(updateAllBtn);
        
        // Thêm nút xuất lịch làm việc
        JButton exportScheduleBtn = new JButton("Xuất lịch làm việc");
        exportScheduleBtn.setFont(new Font("Arial", Font.BOLD, 12));
        exportScheduleBtn.setForeground(Color.WHITE);
        exportScheduleBtn.setBackground(new Color(23, 162, 184));
        exportScheduleBtn.setFocusPainted(false);
        exportScheduleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        exportScheduleBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Xuất lịch làm việc");
            
            // Tạo filter cho file Excel và PDF
            javax.swing.filechooser.FileNameExtensionFilter excelFilter = 
                new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
            javax.swing.filechooser.FileNameExtensionFilter pdfFilter = 
                new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
            
            fileChooser.addChoosableFileFilter(excelFilter);
            fileChooser.addChoosableFileFilter(pdfFilter);
            fileChooser.setFileFilter(excelFilter); // Mặc định là Excel
            
            fileChooser.setSelectedFile(new File("LichLamViec_" + controller.getDoctorId() + ".xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean success = false;
                
                // Lấy boolean[][] schedule từ controller
                boolean[][] schedule = convertScheduleToBoolean(controller.getWeeklySchedule());
                
                if (fileChooser.getFileFilter().equals(excelFilter)) {
                    if (!filePath.toLowerCase().endsWith(".xlsx")) {
                        filePath += ".xlsx";
                    }
                    success = controller.exportScheduleToExcel(
                        controller.getDoctorId(), 
                        controller.getDoctorName(), 
                        schedule, 
                        filePath
                    );
                } else {
                    if (!filePath.toLowerCase().endsWith(".pdf")) {
                        filePath += ".pdf";
                    }
                    success = controller.exportScheduleToPdf(
                        controller.getDoctorId(), 
                        controller.getDoctorName(), 
                        schedule, 
                        filePath
                    );
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất lịch làm việc thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xuất lịch làm việc thất bại!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Thêm nút vào updateBtnPanel
        updateBtnPanel.add(exportScheduleBtn);

        // Thêm các thành phần vào bottomPanel
        bottomPanel.add(legendPanel, BorderLayout.WEST);
        bottomPanel.add(currentShiftPanel, BorderLayout.CENTER);
        bottomPanel.add(updateBtnPanel, BorderLayout.EAST);

        

        // Thêm cả hai panel vào bottomPanel
        bottomPanel.add(legendPanel, BorderLayout.NORTH);
        bottomPanel.add(currentShiftPanel, BorderLayout.SOUTH);

        // Thêm bottomPanel vào schedulePanel
        schedulePanel.add(bottomPanel, BorderLayout.SOUTH);

        // 5. Cuộc hẹn sắp tới
        JPanel upcomingPanel = new JPanel(new BorderLayout());
        upcomingPanel.setBackground(Color.WHITE);
        upcomingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel upcomingLabel = new JLabel("Cuộc hẹn sắp tới", SwingConstants.LEFT);
        upcomingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        upcomingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        String[] appointmentColumns = {"Thời gian", "Mã cuộc hẹn", "Thao tác"};
        DefaultTableModel appointmentModel = new DefaultTableModel(appointmentColumns, 0);
        JTable appointmentTable = new JTable(appointmentModel);
        appointmentTable.setRowHeight(35);
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Lấy dữ liệu cuộc hẹn sắp tới từ controller
        List<Object[]> upcomingAppointments = controller.getNextAppointments();
        for (Object[] appointment : upcomingAppointments) {
            appointmentModel.addRow(appointment);
        }
        
        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentScroll.setBorder(BorderFactory.createEmptyBorder());
        
        upcomingPanel.add(upcomingLabel, BorderLayout.NORTH);
        upcomingPanel.add(appointmentScroll, BorderLayout.CENTER);
        
        // 6. Quick Access
        JPanel quickAccessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        quickAccessPanel.setOpaque(false);
        quickAccessPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JButton newAppBtn = createQuickButton("Lịch hẹn mới", new Color(0, 123, 255));
        JButton searchBtn = createQuickButton("Tra cứu bệnh nhân", new Color(23, 162, 184));
        JButton reportBtn = createQuickButton("Báo cáo công việc", new Color(40, 167, 69));
        
        newAppBtn.addActionListener(e -> controller.showBookAppointment());
        searchBtn.addActionListener(e -> controller.showPatientList());
        reportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        
        quickAccessPanel.add(newAppBtn);
        quickAccessPanel.add(searchBtn);
        quickAccessPanel.add(reportBtn);
        
        // Sắp xếp các panel thành layout chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(doctorInfoPanel, BorderLayout.CENTER);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(dashboardPanel, BorderLayout.NORTH);
        
        JPanel mainBottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainBottomPanel.setOpaque(false);
        mainBottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        mainBottomPanel.add(schedulePanel);
        mainBottomPanel.add(upcomingPanel); 
        
        // Add all sections to main panel
        homePanel.add(headerPanel, BorderLayout.NORTH);
        homePanel.add(topPanel, BorderLayout.CENTER);
        homePanel.add(centerPanel, BorderLayout.SOUTH);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(quickAccessPanel, BorderLayout.NORTH);
        southPanel.add(mainBottomPanel, BorderLayout.CENTER);
        
        homePanel.add(southPanel, BorderLayout.SOUTH);
        
        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /* private JButton createQuickButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    } */

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

    public void showAddPatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Thêm bệnh nhân mới", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtName = new JTextField(40);
        txtAddress = new JTextField(40);
        txtPhone = new JTextField(40);
        txtDisease = new JTextField(40);
        txtEmail = new JTextField(40);
        cbGender = new JComboBox<>(Gender.values());

        // Thay thế JTextField bằng JDatePicker
        UtilDateModel model = new UtilDateModel();
        // Đặt giá trị mặc định là ngày hiện tại trừ 18 năm
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        model.setValue(calendar.getTime());
        
        Properties properties = new Properties();
        properties.put("text.today", "Hôm nay");
        properties.put("text.month", "Tháng");
        properties.put("text.year", "Năm");
        
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBackground(Color.WHITE);
        datePicker.setOpaque(false);
        
        // Thiết lập font cho JDatePicker
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        datePicker.getJFormattedTextField().setFont(fieldFont);
        
        txtName.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtDisease.setFont(fieldFont);
        txtEmail.setFont(fieldFont);
        cbGender.setFont(fieldFont);

        addFormField(formPanel, gbc, "Họ và tên:", txtName, 0);
        addFormField(formPanel, gbc, "Ngày sinh:", datePicker, 1);  // Thay đổi từ txtBirthDate sang datePicker
        addFormField(formPanel, gbc, "Địa chỉ:", txtAddress, 2);
        addFormField(formPanel, gbc, "Giới tính:", cbGender, 3);
        addFormField(formPanel, gbc, "Số điện thoại:", txtPhone, 4);
        addFormField(formPanel, gbc, "Email:", txtEmail, 5);
        addFormField(formPanel, gbc, "Bệnh:", txtDisease, 6);

        JButton btnSave = new JButton("Lưu");
        btnSave.setFont(new Font("Arial", Font.BOLD, 16));
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(200, 45));

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnSave, gbc);

        // Sửa đổi action listener để lấy giá trị từ JDatePicker
        btnSave.addActionListener(e -> {
            // Chuyển đổi từ java.util.Date sang LocalDate
            Date selectedDate = (Date) datePicker.getModel().getValue();
            if (selectedDate != null) {
                LocalDate birthDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String dateStr = birthDate.toString(); // Chuyển sang định dạng YYYY-MM-DD
                
                controller.addPatient(txtName.getText(), dateStr, 
                                        txtAddress.getText(), txtPhone.getText(), 
                                        (Gender) cbGender.getSelectedItem(), 
                                        txtDisease.getText(), txtEmail.getText());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn ngày sinh!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Đặt nút hiện tại là nút "Thêm bệnh nhân"
        setSelectedButton(btnAdd);
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

    public void showPatientList(java.util.List<Patient> patients) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
    
        JLabel titleLabel = new JLabel("Danh sách bệnh nhân", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        String[] columnNames = {"ID Bệnh nhân", "Họ và tên", "Ngày sinh", "Địa chỉ", "Giới tính", "Số điện thoại"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(173, 216, 230));
    
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                    p.getPatientID(),
                    p.getFullName(),
                    p.getDateOfBirth().toString(),
                    p.getAddress(),
                    p.getGender().toString(),
                    p.getPhoneNumber()
                });
            }
        }
    
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton exportExcelBtn = createQuickButton("Xuất Excel", new Color(40, 167, 69));
        JButton exportPdfBtn = createQuickButton("Xuất PDF", new Color(220, 53, 69));
        
        buttonPanel.add(exportExcelBtn);
        buttonPanel.add(exportPdfBtn);
        
        // Thêm sự kiện cho nút xuất Excel
        exportExcelBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu danh sách bệnh nhân");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            fileChooser.setSelectedFile(new File("DanhSachBenhNhan.xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                boolean success = controller.exportPatientsToExcel(patients, filePath);
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
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu danh sách bệnh nhân");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
            fileChooser.setSelectedFile(new File("DanhSachBenhNhan.pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                
                boolean success = controller.exportPatientsToPdf(patients, filePath);
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
    
        // Tạo tablePanel và thêm các thành phần
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

    private JButton createQuickButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    // public void showBookAppointment() {
    //     contentPanel.removeAll();
    //     contentPanel.setLayout(new BorderLayout());

    //     JLabel titleLabel = new JLabel("Đặt lịch hẹn", SwingConstants.CENTER);
    //     titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    //     titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

    //     JPanel formPanel = new JPanel(new GridBagLayout());
    //     formPanel.setBackground(Color.WHITE);
    //     formPanel.setBorder(BorderFactory.createCompoundBorder(
    //             BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
    //             BorderFactory.createEmptyBorder(30, 50, 30, 50)
    //     ));

    //     GridBagConstraints gbc = new GridBagConstraints();
    //     gbc.insets = new Insets(15, 10, 15, 10);
    //     gbc.fill = GridBagConstraints.HORIZONTAL;
    //     gbc.weightx = 1.0;

    //     txtPatientId = new JTextField(40);
    //     txtDate = new JTextField(40);

    //     txtPatientId.setFont(new Font("Arial", Font.PLAIN, 16));
    //     txtDate.setFont(new Font("Arial", Font.PLAIN, 16));

    //     addFormField(formPanel, gbc, "ID Bệnh nhân:", txtPatientId, 0);
    //     addFormField(formPanel, gbc, "Ngày hẹn (YYYY-MM-DD):", txtDate, 1);

    //     JButton btnBook = new JButton("Đặt lịch");
    //     btnBook.setFont(new Font("Arial", Font.BOLD, 16));
    //     btnBook.setBackground(new Color(0, 123, 255));
    //     btnBook.setForeground(Color.WHITE);
    //     btnBook.setFocusPainted(false);
    //     btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
    //     btnBook.setPreferredSize(new Dimension(200, 45));

    //     gbc.gridx = 0;
    //     gbc.gridy = 2;
    //     gbc.gridwidth = 3;
    //     gbc.anchor = GridBagConstraints.CENTER;
    //     gbc.insets = new Insets(30, 10, 10, 10);
    //     formPanel.add(btnBook, gbc);

    //     btnBook.addActionListener(e -> controller.bookAppointment(txtPatientId.getText(), txtDate.getText()));

    //     JPanel wrapperPanel = new JPanel(new BorderLayout());
    //     wrapperPanel.setOpaque(false);
    //     wrapperPanel.add(formPanel, BorderLayout.CENTER);
    //     wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

    //     contentPanel.add(titleLabel, BorderLayout.NORTH);
    //     contentPanel.add(wrapperPanel, BorderLayout.CENTER);
    //     contentPanel.revalidate();
    //     contentPanel.repaint();
    // }

        public void showBookAppointment() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
    
        JLabel titleLabel = new JLabel("Đặt lịch hẹn", SwingConstants.CENTER);
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
    
        txtPatientId = new JTextField(40);
        txtPatientId.setFont(new Font("Arial", Font.PLAIN, 16));
    
        // Thay thế JTextField bằng JDatePicker cho ngày hẹn
        UtilDateModel appointmentModel = new UtilDateModel();
        // Đặt giá trị mặc định là ngày hiện tại
        appointmentModel.setValue(Calendar.getInstance().getTime());
        
        Properties properties = new Properties();
        properties.put("text.today", "Hôm nay");
        properties.put("text.month", "Tháng");
        properties.put("text.year", "Năm");
        
        JDatePanelImpl appointmentDatePanel = new JDatePanelImpl(appointmentModel, properties);
        JDatePickerImpl appointmentDatePicker = new JDatePickerImpl(appointmentDatePanel, new DateLabelFormatter());
        appointmentDatePicker.setBackground(Color.WHITE);
        appointmentDatePicker.setOpaque(false);
        appointmentDatePicker.getJFormattedTextField().setFont(new Font("Arial", Font.PLAIN, 16));
    
        addFormField(formPanel, gbc, "ID Bệnh nhân:", txtPatientId, 0);
        addFormField(formPanel, gbc, "Ngày hẹn:", appointmentDatePicker, 1);
    
        JButton btnBook = new JButton("Đặt lịch");
        btnBook.setFont(new Font("Arial", Font.BOLD, 16));
        btnBook.setBackground(new Color(0, 123, 255));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(200, 45));
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnBook, gbc);
    
        // Sửa action listener để lấy giá trị từ JDatePicker
        btnBook.addActionListener(e -> {
            Date selectedDate = (Date) appointmentDatePicker.getModel().getValue();
            if (selectedDate != null) {
                LocalDate appointmentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String dateStr = appointmentDate.toString(); // Chuyển sang định dạng YYYY-MM-DD
                controller.bookAppointment(txtPatientId.getText(), dateStr);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn ngày hẹn!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));
    
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Đặt nút hiện tại là nút "Đặt lịch hẹn"
        // setSelectedButton(btnBook);
    }

    public void showDeletePatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Xóa bệnh nhân", SwingConstants.CENTER);
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

        txtPatientId = new JTextField(40);
        txtPatientId.setFont(new Font("Arial", Font.PLAIN, 16));
        
        addFormField(formPanel, gbc, "ID Bệnh nhân:", txtPatientId, 0);

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 16));
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(200, 45));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnDelete, gbc);

        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa bệnh nhân này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.deletePatient(txtPatientId.getText());
            }
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

    /**
     * Phương thức main để test DoctorView
     * Phương thức này tạo một giao diện DoctorView với một ID bác sĩ mẫu
     */
    public static void main(String[] args) {
        try {
            // Thiết lập look and feel để có giao diện đẹp hơn
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy giao diện trong Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Tạo một ID bác sĩ mẫu để test
                String testDoctorId = "DOC-001";
                
                // Tạo giao diện bác sĩ
                DoctorView doctorView = new DoctorView(testDoctorId);
                doctorView.setVisible(true);
                
                // In thông báo để biết đã chạy thành công
                System.out.println("Đã khởi tạo giao diện bác sĩ với ID: " + testDoctorId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khởi động giao diện: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }

    public JButton getBtnHome() {
        return btnHome;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnView() {
        return btnView;
    }

    public JButton getBtnBook() {
        return btnBook;
    }

    public JButton getBtnDel() {
        return btnDel;
    }


    /**
     * Lấy tên tiếng Việt của ngày trong tuần
     * @param dow ngày trong tuần (DayOfWeek enum)
     * @return tên tiếng Việt
     */
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

    /**
     * Xác định ca làm việc hiện tại dựa trên giờ
     * @param time thời gian hiện tại
     * @return tên ca làm việc
     */
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

    /**
     * Tạo ô tiêu đề cho lịch
     * @param text Nội dung tiêu đề
     * @return Panel chứa tiêu đề
     */
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

    // Thay đổi phương thức createScheduleCell
        private JPanel createScheduleCell(String status, Color bgColor, boolean isCurrentShift) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(bgColor);
        cell.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Nếu là ca hiện tại, thêm viền đậm
        if (isCurrentShift) {
            cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
            ));
        }
        
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setOpaque(false);
        
        // Tạo JLabel cho icon
        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);
        
        // Sử dụng icon dựa trên trạng thái
        if (status.equals("Đang làm việc")) {
            iconLabel.setIcon(workingIcon);
        } else if (status.equals("Hết ca làm việc")) {
            iconLabel.setIcon(finishedIcon);
        } else {
            iconLabel.setIcon(notWorkingIcon);
        }
        
        // CHỈ hiển thị biểu tượng, không hiển thị text
        contentPanel.add(iconLabel, BorderLayout.CENTER);
        cell.add(contentPanel, BorderLayout.CENTER);
        
        return cell;
    }


    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(colorBox);
        panel.add(label);
        
        return panel;
    }

    // Thêm phương thức mới vào DoctorView
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

    /**
     * Tạo ImageIcon từ JLabel
     */
    private ImageIcon createIconFromLabel(JLabel label) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        label.setSize(16, 16);
        label.paint(g2);
        g2.dispose();
        return new ImageIcon(image);
    }

    // Thêm phương thức này và gọi trong constructor để debug
    private void debugIconPaths() {
        File resourceDir = new File("D:/codejava/BTL_QuanLyBenhNhan/resources/icons");
        System.out.println("Thư mục icons có tồn tại: " + resourceDir.exists());
        if (resourceDir.exists()) {
            System.out.println("Danh sách files trong thư mục:");
            for (File file : resourceDir.listFiles()) {
                System.out.println(" - " + file.getName());
            }
        }
    }

    /**
     * Hiển thị giao diện khám bệnh, cho phép bác sĩ xem danh sách bệnh nhân chờ khám
     * và kê đơn thuốc cho họ
     */
    /* public void showExamination() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
    
        JLabel titleLabel = new JLabel("Khám bệnh", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
    
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    
        JLabel searchLabel = new JLabel("Tìm kiếm bệnh nhân:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
    
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
    
        // Panel danh sách bệnh nhân
        JPanel patientListPanel = new JPanel(new BorderLayout());
        patientListPanel.setBackground(Color.WHITE);
        patientListPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    
        // Tạo bảng danh sách bệnh nhân
        String[] columnNames = {"ID", "Họ và tên", "Ngày sinh", "Số điện thoại", "Bệnh chính", "Trạng thái", "Chọn"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho phép chỉnh sửa cột "Chọn"
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) {
                    return Boolean.class; // Cột checkbox
                }
                return String.class;
            }
        };
        
        JTable patientTable = new JTable(tableModel);
        patientTable.setRowHeight(35);
        patientTable.setFont(new Font("Arial", Font.PLAIN, 14));
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load dữ liệu bệnh nhân từ controller
        List<Object[]> patientRecords = controller.getPatientsForExamination();
        if (patientRecords != null && !patientRecords.isEmpty()) {
            for (Object[] record : patientRecords) {
                Patient patient = (Patient) record[0];
                MedicalRecord medicalRecord = (MedicalRecord) record[1];
                String email = (String) record[2];
                
                // Lấy thông tin bệnh lý từ MedicalRecord nếu có
                String diagnosis = "";
                if (medicalRecord != null) {
                    diagnosis = medicalRecord.getDiagnosis();
                }
                
                tableModel.addRow(new Object[]{
                    patient.getPatientID(),
                    patient.getFullName(),
                    patient.getDateOfBirth().toString(),
                    patient.getPhoneNumber(),
                    diagnosis,
                    "Chờ khám",
                    Boolean.FALSE
                });
            }
        }
    
        JScrollPane scrollPane = new JScrollPane(patientTable);
        patientListPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton prescriptionButton = createQuickButton("Kê đơn thuốc", new Color(40, 167, 69));
        JButton completeButton = createQuickButton("Hoàn thành khám", new Color(0, 123, 255));
        
        buttonPanel.add(prescriptionButton);
        buttonPanel.add(completeButton);
        
        // Xử lý sự kiện khi nhấn nút kê đơn thuốc
        prescriptionButton.addActionListener(e -> {
            // Kiểm tra xem có bệnh nhân nào được chọn không
            boolean hasSelection = false;
            String selectedPatientId = null;
            String patientName = null;
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 6);
                if (isSelected) {
                    hasSelection = true;
                    selectedPatientId = (String) tableModel.getValueAt(i, 0);
                    patientName = (String) tableModel.getValueAt(i, 1);
                    break;
                }
            }
            
            if (!hasSelection) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn bệnh nhân cần kê đơn", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Mở giao diện kê đơn thuốc
            openPrescriptionView(selectedPatientId, patientName);
        });
        
        // Xử lý sự kiện khi nhấn nút hoàn thành khám
        completeButton.addActionListener(e -> {
            int selectedCount = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 6);
                if (isSelected) {
                    selectedCount++;
                    String patientId = (String) tableModel.getValueAt(i, 0);
                    tableModel.setValueAt("Đã khám xong", i, 5);
                    controller.completeExamination(patientId);
                }
            }
            
            if (selectedCount == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn bệnh nhân đã hoàn thành khám", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Đã cập nhật " + selectedCount + " bệnh nhân hoàn thành khám", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Xử lý tìm kiếm bệnh nhân
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập từ khóa tìm kiếm", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Gọi controller để tìm kiếm
            List<Object[]> searchResults = controller.searchPatientsForExamination(keyword);
            
            // Cập nhật bảng
            tableModel.setRowCount(0);
            
            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy bệnh nhân nào phù hợp", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Object[] record : searchResults) {
                    Patient patient = (Patient) record[0];
                    MedicalRecord medicalRecord = (MedicalRecord) record[1];
                    String email = (String) record[2];
                    
                    String diagnosis = "";
                    if (medicalRecord != null) {
                        diagnosis = medicalRecord.getDiagnosis();
                    }
                    
                    tableModel.addRow(new Object[]{
                        patient.getPatientID(),
                        patient.getFullName(),
                        patient.getDateOfBirth().toString(),
                        patient.getPhoneNumber(),
                        diagnosis,
                        "Chờ khám",
                        Boolean.FALSE
                    });
                }
            }
        });
    
        // Tạo layout chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(patientListPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);
    
        setSelectedButton(btnExamination);
        contentPanel.revalidate();
        contentPanel.repaint();
    } */

    /**
     * Hiển thị giao diện khám bệnh, cho phép bác sĩ xem danh sách bệnh nhân chờ khám
     * và kê đơn thuốc cho họ
     */
    public void showExamination() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
    
        // Tiêu đề
        JLabel titleLabel = new JLabel("Khám bệnh", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
    
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    
        JLabel searchLabel = new JLabel("Tìm kiếm bệnh nhân:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
    
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
    
        // Panel danh sách bệnh nhân
        JPanel patientListPanel = new JPanel(new BorderLayout());
        patientListPanel.setBackground(Color.WHITE);
        patientListPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    
        // Tạo bảng danh sách bệnh nhân
        String[] columnNames = {"ID", "Họ và tên", "Ngày sinh", "Số điện thoại", "Bệnh chính", "Trạng thái", "Chọn"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable patientTable = new JTable(tableModel);
        patientTable.setRowHeight(35);
        patientTable.setFont(new Font("Arial", Font.PLAIN, 14));
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load dữ liệu bệnh nhân từ controller
        List<Object[]> patientRecords = controller.getPatientsForExamination();
        
        if (patientRecords == null || patientRecords.isEmpty()) {
            System.out.println("Không tìm thấy bệnh nhân chờ khám");
        } else {
            for (Object[] record : patientRecords) {
                try {
                    Patient patient = (Patient) record[0];
                    MedicalRecord medicalRecord = (MedicalRecord) record[1];
                    String email = (String) record[2];
                    String appointmentStatus = record.length > 3 ? (String) record[3] : "Chờ khám";
                    
                    // Lấy thông tin bệnh lý từ MedicalRecord nếu có
                    String diagnosis = "";
                    if (medicalRecord != null && medicalRecord.getDiagnosis() != null) {
                        diagnosis = medicalRecord.getDiagnosis();
                    }
                    
                    // Format lại ngày sinh
                    String birthDateStr = "";
                    if (patient.getDateOfBirth() != null) {
                        birthDateStr = patient.getDateOfBirth().toString();
                    }
                    
                    tableModel.addRow(new Object[]{
                        patient.getPatientID(),
                        patient.getFullName(),
                        birthDateStr,
                        patient.getPhoneNumber(),
                        diagnosis,
                        appointmentStatus,
                        new JButton("Chọn")
                    });
                } catch (Exception e) {
                    System.out.println("Lỗi khi xử lý bản ghi: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        // Thiết lập renderer cho cột "Chọn"
        patientTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        patientTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
    
        JScrollPane scrollPane = new JScrollPane(patientTable);
        patientListPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton prescriptionButton = createQuickButton("Kê đơn thuốc", new Color(40, 167, 69));
        JButton completeButton = createQuickButton("Hoàn thành khám", new Color(0, 123, 255));
        
        buttonPanel.add(prescriptionButton);
        buttonPanel.add(completeButton);
        
        // Xử lý sự kiện khi nhấn nút kê đơn thuốc
        prescriptionButton.addActionListener(e -> prescribeForSelectedPatient(patientTable));
        
        // Xử lý sự kiện khi nhấn nút hoàn thành khám
        completeButton.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn bệnh nhân để hoàn thành khám", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = patientTable.getValueAt(selectedRow, 0).toString();
            controller.completeExamination(patientId);
            
            // Cập nhật trạng thái trong bảng
            tableModel.setValueAt("Đã hoàn thành", selectedRow, 5);
            JOptionPane.showMessageDialog(this, "Đã hoàn thành khám", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Xử lý tìm kiếm bệnh nhân
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                // Nếu từ khóa trống, hiển thị lại tất cả bệnh nhân
                tableModel.setRowCount(0);
                List<Object[]> allPatients = controller.getPatientsForExamination();
                populateTable(tableModel, allPatients);
                return;
            }
            
            // Gọi controller để tìm kiếm
            List<Object[]> searchResults = controller.searchPatientsForExamination(keyword);
            
            // Cập nhật bảng
            tableModel.setRowCount(0);
            
            if (searchResults == null || searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy bệnh nhân nào phù hợp", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                populateTable(tableModel, searchResults);
            }
        });
    
        // Tạo layout chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(patientListPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);
    
        setSelectedButton(btnExamination);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Mở giao diện kê đơn thuốc cho bệnh nhân đã chọn
     * @param patientTable Bảng chứa danh sách bệnh nhân
     */
    private void prescribeForSelectedPatient(JTable patientTable) {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn bệnh nhân để kê đơn thuốc", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientId = patientTable.getValueAt(selectedRow, 0).toString();
        String patientName = patientTable.getValueAt(selectedRow, 1).toString();
        
        // Kiểm tra xem có phải cuộc hẹn quá hạn không
        String appointmentStatus = patientTable.getValueAt(selectedRow, 5).toString();
        if (appointmentStatus.startsWith("Quá hạn")) {
            int option = JOptionPane.showConfirmDialog(this,
                "Bệnh nhân này có lịch hẹn đã quá hạn. Bạn vẫn muốn kê đơn thuốc?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Mở giao diện kê đơn thuốc
        openPrescriptionView(patientId, patientName);
    }
    
    /**
     * Điền dữ liệu vào bảng từ danh sách kết quả
     */
    private void populateTable(DefaultTableModel model, List<Object[]> records) {
        if (records != null) {
            for (Object[] record : records) {
                try {
                    Patient patient = (Patient) record[0];
                    MedicalRecord medicalRecord = (MedicalRecord) record[1];
                    String email = (String) record[2];
                    String appointmentStatus = record.length > 3 ? (String) record[3] : "Chờ khám";
                    
                    String diagnosis = "";
                    if (medicalRecord != null && medicalRecord.getDiagnosis() != null) {
                        diagnosis = medicalRecord.getDiagnosis();
                    }
                    
                    String birthDateStr = "";
                    if (patient.getDateOfBirth() != null) {
                        birthDateStr = patient.getDateOfBirth().toString();
                    }
                    
                    model.addRow(new Object[]{
                        patient.getPatientID(),
                        patient.getFullName(),
                        birthDateStr,
                        patient.getPhoneNumber(),
                        diagnosis,
                        appointmentStatus,
                        new JButton("Chọn")
                    });
                } catch (Exception e) {
                    System.out.println("Lỗi khi xử lý bản ghi: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Mở giao diện kê đơn thuốc từ PrescriptionDetailsDoctorView với thông tin bệnh nhân đã chọn
     * @param patientId ID của bệnh nhân
     * @param patientName Tên của bệnh nhân
     */
    private void openPrescriptionView(String patientId, String patientName) {
        // Tạo một ID đơn thuốc mới
        String newPrescriptionId = controller.generateNewPrescriptionId();
        
        // Tạo một instance của PrescriptionDetailsDoctorView
        PrescriptionDetailsDoctorView prescriptionView = new PrescriptionDetailsDoctorView(
            controller.getDoctorId(),     // ID của bác sĩ
            patientId,                    // ID của bệnh nhân
            patientName,                  // Tên của bệnh nhân
            newPrescriptionId,            // ID đơn thuốc mới
            true                          // Chế độ tạo mới
        );
        
        // Hiển thị cửa sổ mới
        prescriptionView.setVisible(true);
        
        // Thêm listener để cập nhật lại danh sách khi đóng cửa sổ kê đơn
        prescriptionView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // Làm mới giao diện khám bệnh
                controller.showExamination();
            }
        });
    }

    // Thêm phương thức helper để chuyển đổi từ String[][] sang boolean[][]
    private boolean[][] convertScheduleToBoolean(String[][] scheduleData) {
        boolean[][] schedule = new boolean[3][7];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                schedule[i][j] = "Đang làm việc".equals(scheduleData[i][j]);
            }
        }
        return schedule;
    }
}