package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import enums.Gender;
import utils.ScannerUtils;
import entity.Patient;
import UI.PatientManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class DoctorUI extends JFrame {
    private JPanel contentPanel;
    private JTextField txtName, txtBirthDate, txtAddress, txtPhone, txtMedicalHistory, txtID;
    private DefaultTableModel tableModel;
    private JTable table;
    private PatientManager patientManager;
    private List<Patient> patientList = new ArrayList<>();
    private JComboBox<Gender> cbGender;
    private JButton btnHome, btnAdd, btnView,btnBook, btnDel; // Các nút trong menu
    private JButton currentSelectedButton; // Theo dõi nút đang được chọn

    public List<Patient> getPatientList() {
        return patientList;
    }

    public DoctorUI() {
        setTitle("Doctor Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        patientManager = new PatientManager();

        // Lấy kích thước màn hình
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int leftPanelWidth = (int) (screenSize.width * 0.25); // 25% màn hình cho panel trái

        // Panel bên trái (Menu)
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(leftPanelWidth, screenSize.height));
        leftPanel.setBackground(new Color(34, 45, 65)); // Màu xanh đậm nhạt
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Tiêu đề menu
        JLabel menuTitle = new JLabel("Doctor Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 50));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10); // Khoảng cách lớn từ đỉnh
        gbc.weighty = 0.1; // Phân bổ không gian dọc
        leftPanel.add(menuTitle, gbc);

        // Các nút chức năng
        btnHome = createButton("Home");
        btnAdd = createButton("Add Patient");
        btnView = createButton("View Patients");
        btnBook = createButton("Book Appointment");
        btnDel = createButton("Delete Patient");
        

        // Đặt trạng thái ban đầu: Home được chọn
        setSelectedButton(btnHome);

        // Thêm các nút với khoảng cách tùy chỉnh
        gbc.insets = new Insets(20, 10, 20, 10); // Khoảng cách giữa các nút
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
        leftPanel.add(btnDel, gbc);

        // Thêm khoảng trống ở dưới cùng
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        // Panel chính (Nội dung)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245)); // Màu nền sáng
        resetToHome();

        // Thêm panel vào frame
        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Xử lý sự kiện
        btnHome.addActionListener(e -> {
            setSelectedButton(btnHome);
            resetToHome();
        });
        btnAdd.addActionListener(e -> {
            setSelectedButton(btnAdd);
            showAddPatientForm();
        });
        btnView.addActionListener(e -> {
            setSelectedButton(btnView);
            showPatientList();
        });
        btnBook.addActionListener(e -> {
            setSelectedButton(btnBook);
            showBookAppointment();
        });
    }

    // Tạo nút với phong cách đồng bộ
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219)); // Màu xanh dương
        button.setPreferredSize(new Dimension(250, 60)); // Tăng kích thước nút
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentSelectedButton) {
                    button.setBackground(new Color(41, 128, 185)); // Hover effect
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentSelectedButton) {
                    button.setBackground(new Color(52, 152, 219));
                }
            }
        });
        return button;
    }

    // Đặt trạng thái nút được chọn
    private void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(new Color(52, 152, 219));
            currentSelectedButton.setPreferredSize(new Dimension(250, 60));
            currentSelectedButton.revalidate();
        }

        currentSelectedButton = selectedButton;
        currentSelectedButton.setBackground(Color.GRAY);
        currentSelectedButton.setPreferredSize(new Dimension(280, 70));
        currentSelectedButton.revalidate();
    }

    // Trang chủ
    private void resetToHome() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout()); // Explicitly reset to BorderLayout
        JLabel lblWelcome = new JLabel("Welcome to Doctor Dashboard", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(44, 62, 80));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Form thêm bệnh nhân
    private void showAddPatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        // Panel con để chứa form nhập liệu
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Tăng khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề "Enter Patient Information"
        JLabel titleLabel = new JLabel("Enter Patient Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Khởi tạo các trường nhập liệu với kích thước đồng bộ
        txtName = new JTextField(40);
        txtBirthDate = new JTextField(40);
        txtAddress = new JTextField(40);
        txtPhone = new JTextField(40);
        txtMedicalHistory = new JTextField(40);
        cbGender = new JComboBox<>(Gender.values());

        // Đặt kích thước đồng bộ cho tất cả các ô nhập liệu
        txtName.setPreferredSize(new Dimension(400, 60));
        txtBirthDate.setPreferredSize(new Dimension(400, 60));
        txtAddress.setPreferredSize(new Dimension(400, 60));
        txtPhone.setPreferredSize(new Dimension(400, 60));
        txtMedicalHistory.setPreferredSize(new Dimension(400, 60));
        cbGender.setPreferredSize(new Dimension(400, 60));

        JButton btnSave = createButton("Save");
        btnSave.setPreferredSize(new Dimension(300, 80));

        // Tăng kích thước phông chữ cho các nhãn và trường nhập liệu
        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        txtName.setFont(fieldFont);
        txtBirthDate.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtMedicalHistory.setFont(fieldFont);
        cbGender.setFont(fieldFont);

        // Thêm các nhãn và trường nhập liệu
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(labelFont);
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblName, gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblBirthDate = new JLabel("Birth Date (YYYY-MM-DD):");
        lblBirthDate.setFont(labelFont);
        lblBirthDate.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblBirthDate, gbc);
        gbc.gridx = 1;
        formPanel.add(txtBirthDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(labelFont);
        lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblAddress, gbc);
        gbc.gridx = 1;
        formPanel.add(txtAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        lblGender.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblGender, gbc);
        gbc.gridx = 1;
        formPanel.add(cbGender, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(labelFont);
        lblPhone.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblPhone, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblMedicalHistory = new JLabel("Medical History:");
        lblMedicalHistory.setFont(labelFont);
        lblMedicalHistory.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblMedicalHistory, gbc);
        gbc.gridx = 1;
        formPanel.add(txtMedicalHistory, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnSave, gbc);

        btnSave.addActionListener(e -> addPatient());

        // Thêm formPanel vào contentPanel
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Thêm bệnh nhân
    private void addPatient() {
        String name = txtName.getText();
        String birthDateStr = txtBirthDate.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();
        String medicalHistory = txtMedicalHistory.getText();

        if (name.isEmpty() || birthDateStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            Gender gender = (Gender) cbGender.getSelectedItem();
            LocalDate createdAt = LocalDate.now();

            Patient newPatient = new Patient(name, birthDate, address, gender, phone, createdAt);

            // Thêm vào danh sách trong bộ nhớ trước
            patientManager.addPatient(newPatient);

            // Lưu vào cơ sở dữ liệu
            boolean savedToDatabase = addPatientToDatabase(name, birthDateStr, gender.toString(), phone, address, medicalHistory);

            if (savedToDatabase) {
                // Nếu lưu vào cơ sở dữ liệu thành công, hiển thị thông báo thành công
                JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetToHome();
            } else {
                // Nếu lưu vào cơ sở dữ liệu thất bại, xóa bệnh nhân khỏi danh sách trong bộ nhớ
                patientManager.removePatientByID(newPatient.getPatientID());
                JOptionPane.showMessageDialog(this, "Failed to save patient to database!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format (YYYY-MM-DD)!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm bệnh nhân vào cơ sở dữ liệu và trả về kết quả thành công/thất bại
    private boolean addPatientToDatabase(String name, String birthDate, String gender, String phone, String address, String medicalHistory) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb?autoReconnect=true&useSSL=false", "root", "20055")) {
            String query = "INSERT INTO patients (name, birthdate, gender, phone, address, medical_history) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, birthDate);
            stmt.setString(3, gender);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, medicalHistory);

            stmt.executeUpdate();
            return true; // Trả về true nếu lưu thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Trả về false nếu lưu thất bại
        }
    }

    // Hiển thị danh sách bệnh nhân
    private void showPatientList() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Full Name", "Birth Date", "Address", "Gender", "Phone", "Medical History"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        List<Patient> patients = patientManager.getAllPatients();
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                    p.getFullName() != null ? p.getFullName() : "N/A",
                    p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "N/A",
                    p.getAddress() != null ? p.getAddress() : "N/A",
                    p.getGender() != null ? p.getGender() : "N/A",
                    p.getPhoneNumber() != null ? p.getPhoneNumber() : "N/A",
                    "N/A" // Medical history not available in Patient class; adjust if needed
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    

    

    private void showBookAppointment() {
    	ScannerUtils sc = new ScannerUtils();
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Book an Appointment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        // Form fields
        JTextField txtPatientId = new JTextField(40);
        txtPatientId.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPatientId.setPreferredSize(new Dimension(400, 60));

        JTextField txtDate = new JTextField(40);
        txtDate.setFont(new Font("Arial", Font.PLAIN, 16));
        txtDate.setPreferredSize(new Dimension(400, 60));

        JButton btnBook = createButton("Book");
        btnBook.setPreferredSize(new Dimension(200, 60));

        Font labelFont = new Font("Arial", Font.PLAIN, 18);

        // Patient ID
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblPatientId = new JLabel("Patient ID:");
        lblPatientId.setFont(labelFont);
        lblPatientId.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblPatientId, gbc);
        gbc.gridx = 1;
        contentPanel.add(txtPatientId, gbc);

        // Appointment Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblDate = new JLabel("Appointment Date (YYYY-MM-DD):");
        lblDate.setFont(labelFont);
        lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblDate, gbc);
        gbc.gridx = 1;
        contentPanel.add(txtDate, gbc);

        // Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnBook, gbc);

        // Event for Booking Appointment
        btnBook.addActionListener(e -> {
            String patientIdStr = txtPatientId.getText().trim();
            String dateStr = txtDate.getText().trim();

            if (patientIdStr.isEmpty() || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Patient ID and Date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

//            try {
//                int patientId = Integer.parseInt(patientIdStr);
//                LocalDate appointmentDate = LocalDate.parse(dateStr);
//
//                boolean booked = sc.bookAppointment(patientId, appointmentDate);
//                if (booked) {
//                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                    resetToHome();
//                } else {
//                    JOptionPane.showMessageDialog(this, "Failed to book appointment!", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(this, "Invalid Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
//            } catch (DateTimeParseException ex) {
//                JOptionPane.showMessageDialog(this, "Invalid date format (YYYY-MM-DD)!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorUI().setVisible(true));
    }
}