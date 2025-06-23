package view;

import controller.DoctorController;
import model.entity.Appointment;
import model.entity.MedicalRecord;
import model.entity.Patient;
import model.entity.VitalSign;
import model.enums.Gender;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.time.ZoneId;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class DoctorView extends JFrame {
    private JPanel contentPanel;
    private JButton btnHome, btnAdd, btnView, btnBook, btnDel, btnLogout, btnExamination;
    private JButton currentSelectedButton;
    private DoctorController controller;
    private JTextField txtName, txtAddress, txtPhone, txtDisease, txtPatientId, txtDate, txtEmail;
    private JDatePickerImpl datePicker;
    private JComboBox<Gender> cbGender;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnCheck;

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
        private String text;
        
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(new Color(0, 123, 255));
            setFocusPainted(false);
            this.text = "Chọn"; // Giá trị mặc định
        }
        
        public ButtonRenderer(String text) {
            this();
            this.text = text;
        }
        
        public void setText(String text) {
            this.text = text;
            super.setText(text);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(this.text); // Sử dụng text đã được đặt
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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        
        JLabel welcomeLabel = new JLabel("Chào mừng, BS. " + (controller.getDoctorName() != null ? controller.getDoctorName() : "Không có dữ liệu"), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(34, 45, 65));
        
        // Cập nhật ngày và giờ theo thời gian hiện tại (03:08 PM +07, 23/06/2025)
        LocalDate today = LocalDate.of(2025, 6, 23);
        LocalTime now = LocalTime.of(15, 8); // 03:08 PM
        JLabel dateLabel = new JLabel(today + " - " + now.format(DateTimeFormatter.ofPattern("HH:mm")), SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        JPanel doctorInfoPanel = new JPanel(new BorderLayout());
        doctorInfoPanel.setBackground(Color.WHITE);
        doctorInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JPanel personalPanel = new JPanel(new BorderLayout(20, 0));
        personalPanel.setOpaque(false);
        
        JPanel avatarPanel = new JPanel();
        avatarPanel.setPreferredSize(new Dimension(150, 150));
        avatarPanel.setBackground(new Color(41, 128, 185));
        JLabel avatarLabel = new JLabel("BS", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 48));
        avatarLabel.setForeground(Color.WHITE);
        avatarPanel.add(avatarLabel);
        
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel("Bác sĩ: " + (controller.getDoctorName() != null ? controller.getDoctorName() : "Không có dữ liệu"));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel specialtyLabel = new JLabel("Chuyên khoa: " + (controller.getDoctorSpecialty() != null ? controller.getDoctorSpecialty() : "Không có dữ liệu"));
        specialtyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel emailLabel = new JLabel("Email: " + (controller.getDoctorEmail() != null ? controller.getDoctorEmail() : "Không có dữ liệu"));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel phoneLabel = new JLabel("Điện thoại: " + (controller.getDoctorPhone() != null ? controller.getDoctorPhone() : "Không có dữ liệu"));
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel addressLabel = new JLabel("Địa chỉ: " + (controller.getDoctorAddress() != null ? controller.getDoctorAddress() : "Không có dữ liệu"));
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        infoPanel.add(nameLabel);
        infoPanel.add(specialtyLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(addressLabel);
        
        personalPanel.add(avatarPanel, BorderLayout.WEST);
        personalPanel.add(infoPanel, BorderLayout.CENTER);
        
        doctorInfoPanel.add(personalPanel, BorderLayout.CENTER); // Sửa từ NORTH thành CENTER để hiển thị đầy đủ

        JPanel dashboardPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));
        dashboardPanel.setOpaque(false);
        
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

        String dayOfWeek = getDayOfWeekInVietnamese(today.getDayOfWeek());
        String currentShift = getCurrentShift(now);

        JLabel todayLabel = new JLabel("Hôm nay: " + dayOfWeek + ", " + today + " - " + now.format(DateTimeFormatter.ofPattern("HH:mm")), SwingConstants.RIGHT);
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

        String[] shifts = {"Sáng (7:00-11:30)", "Chiều (10:30-17:00)", "Tối (17:00-7:00)"};
        LocalTime[] shiftStartTimes = {LocalTime.of(7, 0), LocalTime.of(13, 30), LocalTime.of(17, 0)};
        LocalTime[] shiftEndTimes = {LocalTime.of(11, 30), LocalTime.of(17, 0), LocalTime.of(7, 0)};

        String[][] scheduleData = controller.getWeeklySchedule();

        for (int i = 0; i < shifts.length; i++) {
            calendarPanel.add(createHeaderCell(shifts[i]));
            
            for (int j = 0; j < 7; j++) {
                final String initialStatus = scheduleData[i][j];
                
                String displayStatus;
                Color bgColor;
                
                if (j == today.getDayOfWeek().getValue() - 1) {
                    if (initialStatus != null && initialStatus.equals("Đang làm việc")) {
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
                        displayStatus = initialStatus != null ? initialStatus : "Không làm việc";
                        if (displayStatus.equals("Hết ca làm việc")) {
                            bgColor = new Color(255, 193, 7, 100);
                        } else {
                            bgColor = new Color(240, 240, 240);
                        }
                    }
                } else {
                    displayStatus = initialStatus != null ? initialStatus : "Không làm việc";
                    
                    LocalDate cellDate = today.minusDays(today.getDayOfWeek().getValue() - 1 - j);
                    
                    if (initialStatus != null && initialStatus.equals("Đang làm việc")) {
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
                    } else if (initialStatus != null && initialStatus.equals("Hết ca làm việc")) {
                        bgColor = new Color(255, 193, 7, 100);
                    } else {
                        bgColor = new Color(240, 240, 240);
                    }
                }
                
                boolean isCurrentShift = j == today.getDayOfWeek().getValue() - 1 && 
                                        shifts[i].startsWith(currentShift);
                
                JPanel cell = createScheduleCell(displayStatus, bgColor, isCurrentShift);
                
                calendarPanel.add(cell);
            }
        }

        schedulePanel.add(scheduleHeader, BorderLayout.NORTH);
        schedulePanel.add(calendarPanel, BorderLayout.CENTER);
        
        // Tích hợp bottomPanel từ code đầu tiên
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);
        
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel workingLegend = createLegendItemWithIcon("Ca làm việc", new Color(40, 167, 69, 60), workingIcon);
        JPanel finishedLegend = createLegendItemWithIcon("Hết ca làm việc", new Color(255, 193, 7, 100), finishedIcon);
        JPanel notWorkingLegend = createLegendItemWithIcon("Không làm việc", new Color(240, 240, 240), notWorkingIcon);
        
        legendPanel.add(workingLegend);
        legendPanel.add(finishedLegend);
        legendPanel.add(notWorkingLegend);

        JPanel currentShiftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentShiftPanel.setOpaque(false);
        currentShiftPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String currentShiftInfo = controller.getCurrentShiftInfo() != null ? controller.getCurrentShiftInfo() : "Không xác định";
        JLabel currentShiftLabel = new JLabel("Ca hiện tại: " + currentShiftInfo);
        currentShiftLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentShiftPanel.add(currentShiftLabel);

        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        
        bottomContainer.add(legendPanel);
        bottomContainer.add(currentShiftPanel);

        JPanel actionBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionBtnPanel.setOpaque(false);
        actionBtnPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // Padding top để tách nút
        
        JButton updateAllBtn = new JButton("Cập nhật lịch làm việc");
        updateAllBtn.setFont(new Font("Arial", Font.BOLD, 12));
        updateAllBtn.setForeground(Color.WHITE);
        updateAllBtn.setBackground(new Color(0, 123, 255));
        updateAllBtn.setFocusPainted(false);
        updateAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateAllBtn.setPreferredSize(new Dimension(160, 35));
        updateAllBtn.addActionListener(e -> {
            contentPanel.removeAll();
            controller.showHome();
            JOptionPane.showMessageDialog(this, 
                "Đã cập nhật hiển thị lịch làm việc!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton exportScheduleBtn = new JButton("Xuất lịch làm việc");
        exportScheduleBtn.setFont(new Font("Arial", Font.BOLD, 12));
        exportScheduleBtn.setForeground(Color.WHITE);
        exportScheduleBtn.setBackground(new Color(23, 162, 184));
        exportScheduleBtn.setFocusPainted(false);
        exportScheduleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportScheduleBtn.setPreferredSize(new Dimension(160, 35));
        
        exportScheduleBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Xuất lịch làm việc");
            
            javax.swing.filechooser.FileNameExtensionFilter excelFilter = 
                    new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
                javax.swing.filechooser.FileNameExtensionFilter pdfFilter = 
                    new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
            
            fileChooser.addChoosableFileFilter(excelFilter);
            fileChooser.addChoosableFileFilter(pdfFilter);
            fileChooser.setFileFilter(excelFilter);
            
            fileChooser.setSelectedFile(new File("LichLamViec_" + controller.getDoctorId() + ".xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean success = false;
                
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

        actionBtnPanel.add(updateAllBtn);
        actionBtnPanel.add(exportScheduleBtn);
        
        bottomPanel.add(bottomContainer, BorderLayout.CENTER);
        bottomPanel.add(actionBtnPanel, BorderLayout.SOUTH);

        schedulePanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel upcomingPanel = new JPanel(new BorderLayout());
        upcomingPanel.setBackground(Color.WHITE);
        upcomingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel upcomingLabel = new JLabel("Cuộc hẹn sắp tới", SwingConstants.LEFT);
        upcomingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        upcomingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        String[] appointmentColumns = {"Thời gian", "Mã cuộc hẹn", "Bệnh nhân", "Thao tác"};
        DefaultTableModel appointmentModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        
        JTable appointmentTable = new JTable(appointmentModel);
        appointmentTable.setRowHeight(35);
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        List<Object[]> upcomingAppointments = controller.getNextAppointments();
        
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        buttonRenderer.setText("Hủy");
        buttonRenderer.setBackground(new Color(220, 53, 69));
        
        ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
        
        appointmentTable.getColumnModel().getColumn(3).setCellRenderer(buttonRenderer);
        appointmentTable.getColumnModel().getColumn(3).setCellEditor(buttonEditor);
        
        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = appointmentTable.getSelectedRow();
                int col = appointmentTable.getSelectedColumn();
                
                if (col == 3 && row >= 0) {
                    String appointmentId = (String) appointmentTable.getValueAt(row, 1);
                    
                    int option = JOptionPane.showConfirmDialog(
                        DoctorView.this,
                        "Bạn có chắc chắn muốn hủy cuộc hẹn này không?",
                        "Xác nhận hủy lịch hẹn",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        boolean success = controller.cancelAppointment(appointmentId);
                        if (success) {
                            JOptionPane.showMessageDialog(
                                DoctorView.this,
                                "Đã hủy lịch hẹn thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            controller.showHome();
                        } else {
                            JOptionPane.showMessageDialog(
                                DoctorView.this,
                                "Không thể hủy lịch hẹn. Vui lòng thử lại sau!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            }
        });

        for (Object[] appointment : upcomingAppointments) {
            appointmentModel.addRow(appointment);
        }
        
        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentScroll.setBorder(BorderFactory.createEmptyBorder());
        
        upcomingPanel.add(upcomingLabel, BorderLayout.NORTH);
        upcomingPanel.add(appointmentScroll, BorderLayout.CENTER);
        
        JPanel quickAccessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        quickAccessPanel.setOpaque(false);
        quickAccessPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JButton newAppBtn = createQuickButton("Lịch hẹn mới", new Color(0, 123, 255));
        JButton searchBtn = createQuickButton("Tra cứu bệnh nhân", new Color(23, 162, 184));
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(180, 35));
        JButton reportBtn = createQuickButton("Báo cáo công việc", new Color(40, 167, 69));
        reportBtn.setFont(new Font("Arial", Font.BOLD, 14));
        reportBtn.setPreferredSize(new Dimension(180, 35));
        
        newAppBtn.addActionListener(e -> controller.showBookAppointment());
        searchBtn.addActionListener(e -> controller.showPatientList());
        reportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        
        quickAccessPanel.add(newAppBtn);
        quickAccessPanel.add(searchBtn);
        quickAccessPanel.add(reportBtn);
        
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

        UtilDateModel model = new UtilDateModel();
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
        
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        datePicker.getJFormattedTextField().setFont(fieldFont);
        
        txtName.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtDisease.setFont(fieldFont);
        txtEmail.setFont(fieldFont);
        cbGender.setFont(fieldFont);

        addFormField(formPanel, gbc, "Họ và tên:", txtName, 0);
        addFormField(formPanel, gbc, "Ngày sinh:", datePicker, 1);
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

        btnSave.addActionListener(e -> {
            Date selectedDate = (Date) datePicker.getModel().getValue();
            if (selectedDate != null) {
                LocalDate birthDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String dateStr = birthDate.toString();
                
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

    /* public void showBookAppointment() {
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
    
        UtilDateModel appointmentModel = new UtilDateModel();
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
        
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        timePanel.setOpaque(false);
        
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        JComboBox<String> hourComboBox = new JComboBox<>(hours);
        hourComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        
        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }
        JComboBox<String> minuteComboBox = new JComboBox<>(minutes);
        minuteComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        
        hourComboBox.setSelectedItem("08");
        minuteComboBox.setSelectedItem("00");
        
        JLabel hourLabel = new JLabel("Giờ:");
        hourLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel minuteLabel = new JLabel("Phút:");
        minuteLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JPanel hourPanel = new JPanel(new BorderLayout(5, 0));
        hourPanel.setOpaque(false);
        hourPanel.add(hourLabel, BorderLayout.WEST);
        hourPanel.add(hourComboBox, BorderLayout.CENTER);
        
        JPanel minutePanel = new JPanel(new BorderLayout(5, 0));
        minutePanel.setOpaque(false);
        minutePanel.add(minuteLabel, BorderLayout.WEST);
        minutePanel.add(minuteComboBox, BorderLayout.CENTER);
        
        timePanel.add(hourPanel);
        timePanel.add(minutePanel);
        
        addFormField(formPanel, gbc, "ID Bệnh nhân:", txtPatientId, 0);
        addFormField(formPanel, gbc, "Ngày hẹn:", appointmentDatePicker, 1);
        addFormField(formPanel, gbc, "Thời gian:", timePanel, 2);
        
        JPanel quickTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickTimePanel.setOpaque(false);
        
        JButton morning1 = new JButton("8:00");
        JButton morning2 = new JButton("9:30");
        JButton afternoon1 = new JButton("14:00");
        JButton afternoon2 = new JButton("15:30");
        
        styleQuickTimeButton(morning1);
        styleQuickTimeButton(morning2);
        styleQuickTimeButton(afternoon1);
        styleQuickTimeButton(afternoon2);
        
        morning1.addActionListener(e -> {
            hourComboBox.setSelectedItem("08");
            minuteComboBox.setSelectedItem("00");
        });
        
        morning2.addActionListener(e -> {
            hourComboBox.setSelectedItem("09");
            minuteComboBox.setSelectedItem("30");
        });
        
        afternoon1.addActionListener(e -> {
            hourComboBox.setSelectedItem("14");
            minuteComboBox.setSelectedItem("00");
        });
        
        afternoon2.addActionListener(e -> {
            hourComboBox.setSelectedItem("15");
            minuteComboBox.setSelectedItem("30");
        });
        
        quickTimePanel.add(new JLabel("Giờ phổ biến: "));
        quickTimePanel.add(morning1);
        quickTimePanel.add(morning2);
        quickTimePanel.add(afternoon1);
        quickTimePanel.add(afternoon2);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 10, 15, 10);
        formPanel.add(quickTimePanel, gbc);
        
        JButton btnBook = new JButton("Đặt lịch");
        btnBook.setFont(new Font("Arial", Font.BOLD, 16));
        btnBook.setBackground(new Color(0, 123, 255));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(200, 45));
    
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnBook, gbc);
    
        btnBook.addActionListener(e -> {
            Date selectedDate = (Date) appointmentDatePicker.getModel().getValue();
            if (selectedDate != null) {
                LocalDate appointmentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                
                String hour = (String) hourComboBox.getSelectedItem();
                String minute = (String) minuteComboBox.getSelectedItem();
                
                String dateTimeStr = appointmentDate.toString() + " " + hour + ":" + minute + ":00";
                
                controller.bookAppointment(txtPatientId.getText(), dateTimeStr);
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
        
        setSelectedButton(btnBook);
    } */

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
    
        // Thay thế JTextField bằng JComboBox
        JComboBox<Patient> patientComboBox = new JComboBox<>();
        patientComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    Patient patient = (Patient) value;
                    setText(patient.getPatientID() + " - " + patient.getFullName());
                }
                return this;
            }
        });
        patientComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Nạp danh sách bệnh nhân vào ComboBox
        List<Patient> patients = controller.getAllPatients();
        DefaultComboBoxModel<Patient> patientModel = new DefaultComboBoxModel<>();
        for (Patient patient : patients) {
            patientModel.addElement(patient);
        }
        patientComboBox.setModel(patientModel);

        // Thêm ComboBox chọn bệnh nhân vào form (không thêm dòng tìm kiếm)
        addFormField(formPanel, gbc, "Chọn bệnh nhân:", patientComboBox, 0);
        
        // Các phần tử khác như cũ
        UtilDateModel appointmentModel = new UtilDateModel();
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
        
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        timePanel.setOpaque(false);
        
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        JComboBox<String> hourComboBox = new JComboBox<>(hours);
        hourComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        
        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }
        JComboBox<String> minuteComboBox = new JComboBox<>(minutes);
        minuteComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        
        hourComboBox.setSelectedItem("08");
        minuteComboBox.setSelectedItem("00");
        
        JLabel hourLabel = new JLabel("Giờ:");
        hourLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel minuteLabel = new JLabel("Phút:");
        minuteLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JPanel hourPanel = new JPanel(new BorderLayout(5, 0));
        hourPanel.setOpaque(false);
        hourPanel.add(hourLabel, BorderLayout.WEST);
        hourPanel.add(hourComboBox, BorderLayout.CENTER);
        
        JPanel minutePanel = new JPanel(new BorderLayout(5, 0));
        minutePanel.setOpaque(false);
        minutePanel.add(minuteLabel, BorderLayout.WEST);
        minutePanel.add(minuteComboBox, BorderLayout.CENTER);
        
        timePanel.add(hourPanel);
        timePanel.add(minutePanel);
        
        addFormField(formPanel, gbc, "Ngày hẹn:", appointmentDatePicker, 2);
        addFormField(formPanel, gbc, "Thời gian:", timePanel, 3);
        
        JPanel quickTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickTimePanel.setOpaque(false);
        
        JButton morning1 = new JButton("8:00");
        JButton morning2 = new JButton("9:30");
        JButton afternoon1 = new JButton("14:00");
        JButton afternoon2 = new JButton("15:30");
        
        styleQuickTimeButton(morning1);
        styleQuickTimeButton(morning2);
        styleQuickTimeButton(afternoon1);
        styleQuickTimeButton(afternoon2);
        
        morning1.addActionListener(e -> {
            hourComboBox.setSelectedItem("08");
            minuteComboBox.setSelectedItem("00");
        });
        
        morning2.addActionListener(e -> {
            hourComboBox.setSelectedItem("09");
            minuteComboBox.setSelectedItem("30");
        });
        
        afternoon1.addActionListener(e -> {
            hourComboBox.setSelectedItem("14");
            minuteComboBox.setSelectedItem("00");
        });
        
        afternoon2.addActionListener(e -> {
            hourComboBox.setSelectedItem("15");
            minuteComboBox.setSelectedItem("30");
        });
        
        quickTimePanel.add(new JLabel("Giờ phổ biến: "));
        quickTimePanel.add(morning1);
        quickTimePanel.add(morning2);
        quickTimePanel.add(afternoon1);
        quickTimePanel.add(afternoon2);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 10, 15, 10);
        formPanel.add(quickTimePanel, gbc);
        
        JButton btnBook = new JButton("Đặt lịch");
        btnBook.setFont(new Font("Arial", Font.BOLD, 16));
        btnBook.setBackground(new Color(0, 123, 255));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(200, 45));
    
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(btnBook, gbc);
    
        btnBook.addActionListener(e -> {
            Date selectedDate = (Date) appointmentDatePicker.getModel().getValue();
            if (selectedDate != null) {
                LocalDate appointmentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                
                String hour = (String) hourComboBox.getSelectedItem();
                String minute = (String) minuteComboBox.getSelectedItem();
                
                String dateTimeStr = appointmentDate.toString() + " " + hour + ":" + minute + ":00";
                
                if (patientComboBox.getSelectedItem() != null) {
                    Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
                    controller.bookAppointment(selectedPatient.getPatientID(), dateTimeStr);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Vui lòng chọn bệnh nhân!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
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
        
        setSelectedButton(btnBook);
    }
    
    private void styleQuickTimeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(240, 240, 240));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void showDeletePatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
    
        JLabel titleLabel = new JLabel("Xóa bệnh nhân", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
    
        JPanel warningPanel = new JPanel();
        warningPanel.setLayout(new BorderLayout());
        warningPanel.setBackground(new Color(255, 243, 205));
        warningPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 5, 1, 1, new Color(255, 193, 7)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel warningIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("resources/icons/warning.png");
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                warningIcon.setIcon(new ImageIcon(img));
            } else {
                warningIcon.setText("⚠️");
                warningIcon.setFont(new Font("Arial", Font.BOLD, 24));
            }
        } catch (Exception e) {
            warningIcon.setText("⚠️");
            warningIcon.setFont(new Font("Arial", Font.BOLD, 24));
        }
        
        JLabel warningText = new JLabel("<html><b>Cảnh báo:</b> Xóa bệnh nhân sẽ xóa vĩnh viễn tất cả thông tin liên quan, bao gồm lịch sử khám bệnh và đơn thuốc.</html>");
        warningText.setFont(new Font("Arial", Font.BOLD, 16));

        warningPanel.add(warningIcon, BorderLayout.WEST);
        warningPanel.add(warningText, BorderLayout.CENTER);
        
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.insets = new Insets(0, 5, 0, 5);
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 35));
        
        JButton searchButton = new JButton("Tìm");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(100, 35));
        
        sgbc.gridx = 0;
        sgbc.gridy = 0;
        sgbc.weightx = 0;
        searchPanel.add(searchLabel, sgbc);
        
        sgbc.gridx = 1;
        sgbc.weightx = 1.0;
        searchPanel.add(searchField, sgbc);
        
        sgbc.gridx = 2;
        sgbc.weightx = 0;
        searchPanel.add(searchButton, sgbc);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JPanel idPanel = new JPanel(new BorderLayout(15, 0));
        idPanel.setOpaque(false);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel idLabel = new JLabel("ID Bệnh nhân:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        idLabel.setPreferredSize(new Dimension(130, 35));
        
        txtPatientId = new JTextField();
        txtPatientId.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPatientId.setPreferredSize(new Dimension(txtPatientId.getPreferredSize().width, 35));
        
        idPanel.add(idLabel, BorderLayout.WEST);
        idPanel.add(txtPatientId, BorderLayout.CENTER);
        
        JPanel patientInfoPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        patientInfoPanel.setOpaque(false);
        patientInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                        "Thông tin bệnh nhân",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 16)
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        patientInfoPanel.setVisible(false);
        
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font valueFont = new Font("Arial", Font.PLAIN, 16);
        
        JLabel nameLabel = new JLabel("Họ tên:");
        JLabel nameValue = new JLabel("");
        JLabel dobLabel = new JLabel("Ngày sinh:");
        JLabel dobValue = new JLabel("");
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        JLabel phoneValue = new JLabel("");
        JLabel addressLabel = new JLabel("Địa chỉ:");
        JLabel addressValue = new JLabel("");
        
        nameLabel.setFont(labelFont);
        nameValue.setFont(valueFont);
        dobLabel.setFont(labelFont);
        dobValue.setFont(valueFont);
        phoneLabel.setFont(labelFont);
        phoneValue.setFont(valueFont);
        addressLabel.setFont(labelFont);
        addressValue.setFont(valueFont);
        
        patientInfoPanel.add(nameLabel);
        patientInfoPanel.add(nameValue);
        patientInfoPanel.add(dobLabel);
        patientInfoPanel.add(dobValue);
        patientInfoPanel.add(phoneLabel);
        patientInfoPanel.add(phoneValue);
        patientInfoPanel.add(addressLabel);
        patientInfoPanel.add(addressValue);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        btnCheck = new JButton("Kiểm tra");
        btnCheck.setFont(new Font("Arial", Font.BOLD, 16));
        btnCheck.setBackground(new Color(0, 123, 255));
        btnCheck.setForeground(Color.WHITE);
        btnCheck.setFocusPainted(false);
        btnCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheck.setPreferredSize(new Dimension(150, 45));
        
        JButton btnDelete = new JButton("Xóa");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 16));
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(150, 45));
        btnDelete.setEnabled(false);
        
        buttonPanel.add(btnCheck);
        buttonPanel.add(btnDelete);
        
        formPanel.add(idPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(patientInfoPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);
        
        mainPanel.add(warningPanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(formPanel);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setOpaque(false);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 80, 60, 80));
        outerPanel.add(mainPanel, BorderLayout.CENTER);

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(outerPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    
        btnCheck.addActionListener(e -> {
            String patientId = txtPatientId.getText().trim();
            if (patientId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID bệnh nhân!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Patient patient = controller.getPatientById(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân có ID: " + patientId, "Thông báo", JOptionPane.ERROR_MESSAGE);
                patientInfoPanel.setVisible(false);
                btnDelete.setEnabled(false);
            } else {
                nameValue.setText(patient.getFullName());
                dobValue.setText(patient.getDateOfBirth().toString());
                phoneValue.setText(patient.getPhoneNumber());
                addressValue.setText(patient.getAddress());
                
                patientInfoPanel.setVisible(true);
                btnDelete.setEnabled(true);
                
                mainPanel.revalidate();
            }
        });
    
        btnDelete.addActionListener(e -> {
            String patientId = txtPatientId.getText().trim();
            String patientName = nameValue.getText();
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận xóa bệnh nhân: " + patientName + " (ID: " + patientId + ")?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.deletePatient(patientId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    txtPatientId.setText("");
                    patientInfoPanel.setVisible(false);
                    btnDelete.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog(this, "Kết quả tìm kiếm", true);
            dialog.setSize(600, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            List<Patient> patients = controller.searchPatients(keyword);
            if (patients == null || patients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] columns = {"ID", "Họ tên", "Ngày sinh", "SĐT", ""};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };
            
            JTable table = new JTable(model);
            table.setRowHeight(30);
            
            table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Chọn"));
            table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
            table.getColumnModel().getColumn(4).setPreferredWidth(60);
            
            for (Patient p : patients) {
                model.addRow(new Object[] {
                    p.getPatientID(),
                    p.getFullName(),
                    p.getDateOfBirth().toString(),
                    p.getPhoneNumber(),
                    "Chọn"
                });
            }
            
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    
                    if (col == 4 && row >= 0) {
                        txtPatientId.setText((String) table.getValueAt(row, 0));
                        dialog.dispose();
                        btnCheck.doClick();
                    }
                }
            });
            
            dialog.add(new JScrollPane(table), BorderLayout.CENTER);
            dialog.setVisible(true);
        });
    
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        setSelectedButton(btnDel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                String testDoctorId = "DOC-001";
                
                DoctorView doctorView = new DoctorView(testDoctorId);
                doctorView.setVisible(true);
                
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


//    public void showExamination() {
//        contentPanel.removeAll();
//        contentPanel.setLayout(new BorderLayout());
//    
//        JLabel titleLabel = new JLabel("Khám bệnh", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
//    
//        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//        searchPanel.setBackground(Color.WHITE);
//        searchPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                BorderFactory.createEmptyBorder(10, 10, 10, 10)
//        ));
//    
//        JLabel searchLabel = new JLabel("Tìm kiếm bệnh nhân:");
//        JTextField searchField = new JTextField(20);
//        JButton searchButton = new JButton("Tìm kiếm");
//        searchButton.setBackground(new Color(0, 123, 255));
//        searchButton.setForeground(Color.WHITE);
//        searchButton.setFocusPainted(false);
//    
//        searchPanel.add(searchLabel);
//        searchPanel.add(searchField);
//        searchPanel.add(searchButton);
//    
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.setOpaque(false);
//        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
//    
//        JPanel patientListPanel = new JPanel(new BorderLayout());
//        patientListPanel.setBackground(Color.WHITE);
//        patientListPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                BorderFactory.createEmptyBorder(10, 10, 10, 10)
//        ));
//    
//        String[] columnNames = {"ID", "Họ và tên", "Ngày sinh", "Số điện thoại", "Bệnh chính", "Trạng thái", "Chọn"};
//        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 6;
//            }
//        };
//        JTable patientTable = new JTable(tableModel);
//        patientTable.setRowHeight(35);
//        patientTable.setFont(new Font("Arial", Font.PLAIN, 14));
//        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
//        
//        List<Object[]> patientRecords = controller.getPatientsForExamination();
//        
//        if (patientRecords == null || patientRecords.isEmpty()) {
//            System.out.println("Không tìm thấy bệnh nhân chờ khám");
//        } else {
//            for (Object[] record : patientRecords) {
//                try {
//                    Patient patient = (Patient) record[0];
//                    MedicalRecord medicalRecord = (MedicalRecord) record[1];
//                    String email = (String) record[2];
//                    String appointmentStatus = record.length > 3 ? (String) record[3] : "Chờ khám";
//                    
//                    String diagnosis = "";
//                    if (medicalRecord != null && medicalRecord.getDiagnosis() != null) {
//                        diagnosis = medicalRecord.getDiagnosis();
//                    }
//                    
//                    String birthDateStr = "";
//                    if (patient.getDateOfBirth() != null) {
//                        birthDateStr = patient.getDateOfBirth().toString();
//                    }
//                    
//                    tableModel.addRow(new Object[]{
//                        patient.getPatientID(),
//                        patient.getFullName(),
//                        birthDateStr,
//                        patient.getPhoneNumber(),
//                        diagnosis,
//                        appointmentStatus,
//                        "Chọn"
//                    });
//                } catch (Exception e) {
//                    System.out.println("Lỗi khi xử lý bản ghi: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//        
//        patientTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
//        patientTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
//    
//        JScrollPane scrollPane = new JScrollPane(patientTable);
//        patientListPanel.add(scrollPane, BorderLayout.CENTER);
//        
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//        buttonPanel.setOpaque(false);
//        
//        JButton prescriptionButton = createQuickButton("Kê đơn thuốc", new Color(40, 167, 69));
//        JButton completeButton = createQuickButton("Hoàn thành khám", new Color(0, 123, 255));
//        
//        buttonPanel.add(prescriptionButton);
//        buttonPanel.add(completeButton);
//        
//        JPanel vitalSignsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
//        vitalSignsPanel.setBackground(Color.WHITE);
//        vitalSignsPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                BorderFactory.createEmptyBorder(10, 10, 10, 10)
//        ));
//    
//        JTextField tempField = new JTextField("");
////        JTextField systolicField = new JTextField("");
//        JTextField diastolicField = new JTextField("");
//        JTextField hrField = new JTextField("");
//        JTextField oxyField = new JTextField("");
//        JLabel recordedAtLabel = new JLabel("Chưa ghi nhận");
//    
//        vitalSignsPanel.add(new JLabel("Nhiệt độ (°C):"));
//        vitalSignsPanel.add(tempField);
////        vitalSignsPanel.add(new JLabel("Huyết áp tâm thu (mmHg):"));
////        vitalSignsPanel.add(systolicField);
//        vitalSignsPanel.add(new JLabel("Huyết áp tâm trương (mmHg):"));
//        vitalSignsPanel.add(diastolicField);
//        vitalSignsPanel.add(new JLabel("Nhịp tim (bpm):"));
//        vitalSignsPanel.add(hrField);
//        vitalSignsPanel.add(new JLabel("Độ bão hòa oxy (%):"));
//        vitalSignsPanel.add(oxyField);
//        vitalSignsPanel.add(new JLabel("Thời gian ghi nhận:"));
//        vitalSignsPanel.add(recordedAtLabel);
//    
//        JButton saveButton = createQuickButton("Lưu chỉ số", new Color(0, 123, 255));
//        vitalSignsPanel.add(saveButton);
//        vitalSignsPanel.add(new JLabel(""));
//    
//        patientTable.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                int row = patientTable.getSelectedRow();
//                int col = patientTable.getSelectedColumn();
//                
//                if (col == 6 && row >= 0) {
//                    String patientId = (String) patientTable.getValueAt(row, 0);
//                    VitalSign vitalSign = controller.getVitalSigns(patientId);
//                    
//                    if (vitalSign != null) {
//                        tempField.setText(String.format("%.1f", vitalSign.getTemperature()));
////                        systolicField.setText(String.valueOf(vitalSign.getSystolicPressure()));
//                        diastolicField.setText(String.valueOf(vitalSign.getBloodPressure()));
//                        hrField.setText(String.valueOf(vitalSign.getHeartRate()));
//                        oxyField.setText(String.format("%.1f", vitalSign.getOxygenSaturation()));
//                        recordedAtLabel.setText(vitalSign.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                    } else {
//                        tempField.setText("");
////                        systolicField.setText("");
//                        diastolicField.setText("");
//                        hrField.setText("");
//                        oxyField.setText("");
//                        recordedAtLabel.setText("Chưa ghi nhận");
//                    }
//                }
//            }
//        });
//
//        saveButton.addActionListener(e -> {
//            int row = patientTable.getSelectedRow();
//            if (row < 0) {
//                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bệnh nhân!", "Lỗi", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            String patientId = (String) patientTable.getValueAt(row, 0);
//            try {
//                double temperature = Double.parseDouble(tempField.getText());
////                int systolicBP = Integer.parseInt(systolicField.getText());
//                int diastolicBP = Integer.parseInt(diastolicField.getText());
//                int heartRate = Integer.parseInt(hrField.getText());
//                double oxygenSat = Double.parseDouble(oxyField.getText());
//
//                // Sử dụng constructor mặc định và gán giá trị bằng setter
//                VitalSign vitalSign = new VitalSign();
//                vitalSign.setPatientID(patientId);
//                vitalSign.setTemperature(temperature);
////                vitalSign.setSystolicPressure(systolicBP);
//                vitalSign.setBloodPressure(diastolicBP);
//                vitalSign.setHeartRate(heartRate);
//                vitalSign.setOxygenSaturation(oxygenSat);
//                vitalSign.setRecordedAt(LocalDateTime.now()); // 01:25 AM +07, 05/06/2025
//
//                boolean success = controller.saveVitalSigns(patientId, vitalSign);
//
//                if (success) {
//                    recordedAtLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                    JOptionPane.showMessageDialog(this, "Lưu chỉ số sức khỏe thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
//                } else {
//                    JOptionPane.showMessageDialog(this, "Lưu chỉ số sức khỏe thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                }
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho các chỉ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            } catch (IllegalArgumentException ex) {
//                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        // Xử lý tìm kiếm bệnh nhân
//        searchButton.addActionListener(e -> {
//            String keyword = searchField.getText().trim();
//            if (keyword.isEmpty()) {
//                // Nếu từ khóa trống, hiển thị lại tất cả bệnh nhân
//                tableModel.setRowCount(0);
//                List<Object[]> allPatients = controller.getPatientsForExamination();
//                populateTable(tableModel, allPatients);
//                return;
//            }
//            
//            // Gọi controller để tìm kiếm
//            List<Object[]> searchResults = controller.searchPatientsForExamination(keyword);
//            
//            // Cập nhật bảng
//            tableModel.setRowCount(0);
//            
//            if (searchResults == null || searchResults.isEmpty()) {
//                JOptionPane.showMessageDialog(this, 
//                    "Không tìm thấy bệnh nhân nào phù hợp", 
//                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                populateTable(tableModel, searchResults);
//            }
//        });
//
//
//     // Xử lý sự kiện khi nhấn nút kê đơn thuốc
//        prescriptionButton.addActionListener(e -> prescribeForSelectedPatient(patientTable));
//        
//        // Xử lý sự kiện khi nhấn nút hoàn thành khám
//        completeButton.addActionListener(e -> {
//            int selectedRow = patientTable.getSelectedRow();
//            if (selectedRow == -1) {
//                JOptionPane.showMessageDialog(this, 
//                    "Vui lòng chọn bệnh nhân để hoàn thành khám", 
//                    "Thông báo", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            
//            String patientId = patientTable.getValueAt(selectedRow, 0).toString();
//            controller.completeExamination(patientId);
//            
//            // Cập nhật trạng thái trong bảng
//            tableModel.setValueAt("Đã hoàn thành", selectedRow, 5);
//            JOptionPane.showMessageDialog(this, "Đã hoàn thành khám", "Thành công", JOptionPane.INFORMATION_MESSAGE);
//        });
//
//        JPanel rightPanel = new JPanel(new BorderLayout());
//        rightPanel.setOpaque(false);
//        rightPanel.add(vitalSignsPanel, BorderLayout.NORTH);
//        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        mainPanel.add(searchPanel, BorderLayout.NORTH);
//        mainPanel.add(patientListPanel, BorderLayout.CENTER);
//        mainPanel.add(rightPanel, BorderLayout.EAST);
//
//        contentPanel.add(titleLabel, BorderLayout.NORTH);
//        contentPanel.add(mainPanel, BorderLayout.CENTER);
//        contentPanel.revalidate();
//        contentPanel.repaint();
//
//        setSelectedButton(btnExamination);
//    }
    public void showExamination() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Khám bệnh", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        JPanel patientListPanel = new JPanel(new BorderLayout());
        patientListPanel.setBackground(Color.WHITE);
        patientListPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));

        String[] columnNames = {"ID", "Họ và tên", "Ngày sinh", "Số điện thoại", "Bệnh chính", "Trạng thái", "Chọn"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        JTable patientTable = new JTable(tableModel);
        patientTable.setRowHeight(35); // Đảm bảo chiều cao hàng đủ để hiển thị nội dung
        patientTable.setFont(new Font("Arial", Font.PLAIN, 14));
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

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

                    String diagnosis = "";
                    if (medicalRecord != null && medicalRecord.getDiagnosis() != null) {
                        diagnosis = medicalRecord.getDiagnosis();
                    }

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
                        "Chọn"
                    });
                } catch (Exception e) {
                    System.out.println("Lỗi khi xử lý bản ghi: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Áp dụng renderer và editor cho cột "Chọn"
        patientTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        patientTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Tự động điều chỉnh chiều rộng cột
        autoAdjustColumnWidths(patientTable);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        patientListPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton prescriptionButton = createQuickButton("Kê đơn thuốc", new Color(40, 167, 69));
        JButton completeButton = createQuickButton("Hoàn thành khám", new Color(0, 123, 255));

        buttonPanel.add(prescriptionButton);
        buttonPanel.add(completeButton);

        JPanel vitalSignsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        vitalSignsPanel.setBackground(Color.WHITE);
        vitalSignsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JTextField tempField = new JTextField("");
        JTextField systolicField = new JTextField("");
        JTextField diastolicField = new JTextField("");
        JTextField hrField = new JTextField("");
        JTextField oxyField = new JTextField("");
        JLabel recordedAtLabel = new JLabel("Chưa ghi nhận");

        vitalSignsPanel.add(new JLabel("Nhiệt độ (°C):"));
        vitalSignsPanel.add(tempField);
        vitalSignsPanel.add(new JLabel("Huyết áp tâm thu (mmHg):"));
        vitalSignsPanel.add(systolicField);
        vitalSignsPanel.add(new JLabel("Huyết áp tâm trương (mmHg):"));
        vitalSignsPanel.add(diastolicField);
        vitalSignsPanel.add(new JLabel("Nhịp tim (bpm):"));
        vitalSignsPanel.add(hrField);
        vitalSignsPanel.add(new JLabel("Độ bão hòa oxy (%):"));
        vitalSignsPanel.add(oxyField);
        vitalSignsPanel.add(new JLabel("Thời gian ghi nhận:"));
        vitalSignsPanel.add(recordedAtLabel);

        JButton saveButton = createQuickButton("Lưu chỉ số", new Color(0, 123, 255));
        vitalSignsPanel.add(saveButton);
        vitalSignsPanel.add(new JLabel(""));

        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = patientTable.getSelectedRow();
                int col = patientTable.getSelectedColumn();

                if (col == 6 && row >= 0) {
                    String patientId = (String) patientTable.getValueAt(row, 0);
                    VitalSign vitalSign = controller.getVitalSigns(patientId);

                    if (vitalSign != null) {
                        tempField.setText(String.format("%.1f", vitalSign.getTemperature()));
                        systolicField.setText(String.valueOf(vitalSign.getSystolicPressure()));
                        diastolicField.setText(String.valueOf(vitalSign.getDiastolicPressure()));
                        hrField.setText(String.valueOf(vitalSign.getHeartRate()));
                        oxyField.setText(String.format("%.1f", vitalSign.getOxygenSaturation()));
                        recordedAtLabel.setText(vitalSign.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else {
                        tempField.setText("");
                        systolicField.setText("");
                        diastolicField.setText("");
                        hrField.setText("");
                        oxyField.setText("");
                        recordedAtLabel.setText("Chưa ghi nhận");
                    }
                }
            }
        });

        saveButton.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bệnh nhân!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String patientId = (String) patientTable.getValueAt(row, 0);
            try {
                double temperature = Double.parseDouble(tempField.getText());
                int systolicPressure = Integer.parseInt(systolicField.getText());
                int diastolicBP = Integer.parseInt(diastolicField.getText());
                int heartRate = Integer.parseInt(hrField.getText());
                double oxygenSat = Double.parseDouble(oxyField.getText());

                VitalSign vitalSign = new VitalSign();
                vitalSign.setPatientID(patientId);
                vitalSign.setTemperature(temperature);
                vitalSign.setSystolicPressure(systolicPressure);
                vitalSign.setDiastolicPressure(diastolicBP);
                vitalSign.setHeartRate(heartRate);
                vitalSign.setOxygenSaturation(oxygenSat);
                vitalSign.setRecordedAt(LocalDateTime.now());

                boolean success = controller.saveVitalSigns(patientId, vitalSign);

                if (success) {
                    recordedAtLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    JOptionPane.showMessageDialog(this, "Lưu chỉ số sức khỏe thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lưu chỉ số sức khỏe thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho các chỉ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý tìm kiếm bệnh nhân
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                // Nếu từ khóa trống, hiển thị lại tất cả bệnh nhân
                tableModel.setRowCount(0);
                List<Object[]> allPatients = controller.getPatientsForExamination();
                for (Object[] record : allPatients) {
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

                        tableModel.addRow(new Object[]{
                            patient.getPatientID(),
                            patient.getFullName(),
                            birthDateStr,
                            patient.getPhoneNumber(),
                            diagnosis,
                            appointmentStatus,
                            "Chọn"
                        });
                    } catch (Exception ex) {
                        System.out.println("Lỗi khi xử lý bản ghi: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                autoAdjustColumnWidths(patientTable); // Điều chỉnh lại kích thước cột sau khi điền dữ liệu
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
                for (Object[] record : searchResults) {
                    try {
                        Patient patient = (Patient) record[0];
                        MedicalRecord medicalRecord = (MedicalRecord) record[1];
                        Appointment appointment = (Appointment) record[2]; // Giả định cấu trúc mảng từ search
                        String appointmentStatus = record.length > 3 ? (String) record[3] : "Chờ khám";

                        String diagnosis = "";
                        if (medicalRecord != null && medicalRecord.getDiagnosis() != null) {
                            diagnosis = medicalRecord.getDiagnosis();
                        }

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
                            "Chọn"
                        });
                    } catch (Exception ex) {
                        System.out.println("Lỗi khi xử lý bản ghi: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                autoAdjustColumnWidths(patientTable); // Điều chỉnh lại kích thước cột sau khi điền dữ liệu
            }
        });

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

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(vitalSignsPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(patientListPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        setSelectedButton(btnExamination);
    }

    // Hàm tự động điều chỉnh kích thước cột
    private void autoAdjustColumnWidths(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Tắt tự động điều chỉnh của JTable
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int maxWidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
            }
            // Kiểm tra chiều rộng của tiêu đề cột
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, tableColumn.getHeaderValue(), false, false, 0, column);
            maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);
            // Đặt chiều rộng tối thiểu và tối đa (thêm padding 10 pixel)
            tableColumn.setPreferredWidth(maxWidth + 2);
            // Đặt giới hạn tối đa để tránh cột quá rộng
            if (tableColumn.getPreferredWidth() > 300) {
                tableColumn.setPreferredWidth(300);
            }
        }
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