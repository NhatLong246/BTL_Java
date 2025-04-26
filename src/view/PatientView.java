package view;

import controller.PatientController;
import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender;
import model.repository.PatientRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PatientView extends JFrame {
    private JPanel contentPanel;
    private Patient patient;
    private JButton btnHome, btnViewInfo, btnViewAppointments, btnViewMedicalHistory, btnPayFees, btnPaymentHistory;
    private JButton currentSelectedButton;
    private PatientController controller;

    public PatientView(Patient patient) {
        this.patient = patient;

        try {
            // Khởi tạo controller - có thể gây ra SQLException hoặc ClassNotFoundException
            this.controller = new PatientController(this, patient);

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

            JLabel menuTitle = new JLabel("Patient Menu", SwingConstants.CENTER);
            menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
            menuTitle.setForeground(Color.WHITE);
            gbc.gridy = 0;
            gbc.insets = new Insets(50, 10, 50, 10);
            gbc.weighty = 0.1;
            leftPanel.add(menuTitle, gbc);

            btnHome = createButton("Home");
            btnViewInfo = createButton("View Info");
            btnViewAppointments = createButton("View Appointments");
            btnViewMedicalHistory = createButton("View Medical History");
            btnPayFees = createButton("Pay Fees");
            btnPaymentHistory = createButton("Payment History");
            JButton btnLogout = createButton("Logout");

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
            leftPanel.add(btnPayFees, gbc);
            gbc.gridy = 6;
            leftPanel.add(btnPaymentHistory, gbc);

            gbc.gridy = 7;
            gbc.weighty = 0.0;
            leftPanel.add(btnLogout, gbc);

            gbc.gridy = 8;
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
            btnPayFees.addActionListener(e -> controller.showPayFees());
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
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đã xảy ra lỗi không xác định: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            // Đóng cửa sổ sau khi hiển thị lỗi
            dispose();
        }
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

    // Các phương thức hiển thị giao diện
    public void showHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(new Color(245, 245, 245));

        JLabel welcomeLabel = new JLabel("Chào mừng, " + patient.getFullName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(34, 45, 65));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        infoPanel.setOpaque(false);

        // Các thẻ thông tin
        addInfoCard(infoPanel, "ID Bệnh nhân", patient.getPatientID());
        addInfoCard(infoPanel, "Họ và tên", patient.getFullName());
        addInfoCard(infoPanel, "Số điện thoại", patient.getPhoneNumber());
        addInfoCard(infoPanel, "Địa chỉ", patient.getAddress());

        homePanel.add(welcomeLabel, BorderLayout.NORTH);
        homePanel.add(infoPanel, BorderLayout.CENTER);

        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
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

    public void showPatientInfo() {
        contentPanel.removeAll();

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Thông tin cá nhân", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // ID Bệnh nhân
        JTextField patientIdField = new JTextField(patient.getPatientID());
        patientIdField.setEditable(false);
        addFormField(formPanel, gbc, "ID Bệnh nhân:", patientIdField, 0);

        // Họ và tên
        JTextField nameField = new JTextField(patient.getFullName());
        nameField.setEditable(false);
        addFormField(formPanel, gbc, "Họ và tên:", nameField, 1);

        // Ngày sinh
        JTextField dobField = new JTextField(patient.getDateOfBirth().toString());
        dobField.setEditable(false);
        addFormField(formPanel, gbc, "Ngày sinh:", dobField, 2);

        // Giới tính
        JTextField genderField = new JTextField(patient.getGender().toString());
        genderField.setEditable(false);
        addFormField(formPanel, gbc, "Giới tính:", genderField, 3);

        // Địa chỉ
        JTextField addressField = new JTextField(patient.getAddress());
        addressField.setEditable(false);
        addFormField(formPanel, gbc, "Địa chỉ:", addressField, 4);

        // Số điện thoại
        JTextField phoneField = new JTextField(patient.getPhoneNumber());
        phoneField.setEditable(false);
        addFormField(formPanel, gbc, "Số điện thoại:", phoneField, 5);

        // Ngày đăng ký
        JTextField regDateField = new JTextField(patient.getRegistrationDate().toString());
        regDateField.setEditable(false);
        addFormField(formPanel, gbc, "Ngày đăng ký:", regDateField, 6);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(wrapperPanel, BorderLayout.CENTER);

        contentPanel.add(infoPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showAppointments(List<String[]> appointments) {
        contentPanel.removeAll();

        JPanel appointmentsPanel = new JPanel(new BorderLayout());
        appointmentsPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Lịch hẹn của bạn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        String[] columnNames = {"ID Lịch hẹn", "Ngày hẹn", "Thời gian", "Bác sĩ", "Phòng", "Trạng thái"};

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        if (appointments != null) {
            for (String[] appointment : appointments) {
                model.addRow(appointment);
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

        appointmentsPanel.add(titleLabel, BorderLayout.NORTH);
        appointmentsPanel.add(wrapperPanel, BorderLayout.CENTER);

        contentPanel.add(appointmentsPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showMedicalHistory(List<String[]> medicalHistory) {
        contentPanel.removeAll();

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Lịch sử khám bệnh", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        String[] columnNames = {"ID", "Ngày khám", "Bác sĩ", "Chẩn đoán", "Điều trị", "Ghi chú"};

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        if (medicalHistory != null) {
            for (String[] record : medicalHistory) {
                model.addRow(record);
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

    public void showPayFees() {
        contentPanel.removeAll();

        JPanel feesPanel = new JPanel(new BorderLayout());
        feesPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Thanh toán viện phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        String[] columnNames = {"ID Hóa đơn", "Ngày", "Dịch vụ", "Số tiền", "Trạng thái", "Thanh toán"};

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cho phép chỉnh sửa cột "Thanh toán"
            }
        };
        model.setColumnIdentifiers(columnNames);

        List<Object[]> bills = controller.getBills();
        if (bills != null) {
            for (Object[] bill : bills) {
                if ("Chưa thanh toán".equals(bill[4])) {
                    Object[] rowData = new Object[6];
                    System.arraycopy(bill, 0, rowData, 0, 5);
                    rowData[5] = "Thanh toán";
                    model.addRow(rowData);
                }
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Thiết lập renderer và editor cho nút thanh toán
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), table));

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

        feesPanel.add(titleLabel, BorderLayout.NORTH);
        feesPanel.add(wrapperPanel, BorderLayout.CENTER);

        contentPanel.add(feesPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
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

    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(new Color(0, 123, 255));
            setForeground(Color.WHITE);
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setBackground(new Color(0, 123, 255));
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;

            if ("Thanh toán".equals(label)) {
                String billID = table.getValueAt(table.getSelectedRow(), 0).toString();
                double amount = Double.parseDouble(table.getValueAt(table.getSelectedRow(), 3).toString()
                        .replace("VND", "").replace(",", "").trim());
                showPaymentForm(billID, amount);
            }

            return super.stopCellEditing();
        }
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
     * Main method to start the patient application
     * This would typically be called from a launcher or login screen
     * For testing purposes, this creates a sample patient
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Để kiểm thử: Tạo một bệnh nhân mẫu
                // Trong ứng dụng thực tế, thông tin này sẽ được lấy từ xác thực đăng nhập
                Patient samplePatient = new Patient(
                    "USER001", // userID
                    "PAT-001", // patientID 
                    "Nguyễn Văn A", // fullName
                    LocalDate.of(1990, 5, 15), // dateOfBirth
                    "123 Đường ABC, Quận 1, TP.HCM", // address
                    Gender.MALE, // gender
                    "0901234567", // phoneNumber
                    LocalDate.now().minusYears(1) // createdAt (ngày đăng ký cách đây 1 năm)
                );
                
                // Create the patient view
                new PatientView(samplePatient);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khởi động ứng dụng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }

        /**
     * Main method to start the patient application
     * This would typically be called from a launcher or login screen
     */
    /*public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Không thể thiết lập giao diện: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Kiểm tra kết nối cơ sở dữ liệu trước khi mở LoginView
                try (Connection testConnection = DatabaseConnection.getConnection()) {
                    System.out.println("Kết nối cơ sở dữ liệu thành công");
                } catch (SQLException e) {
                    throw new Exception("Không thể kết nối đến cơ sở dữ liệu: " + e.getMessage(), e);
                }
                
                // Hiển thị màn hình đăng nhập sau khi đã kiểm tra kết nối thành công
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khởi động ứng dụng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }*/
}