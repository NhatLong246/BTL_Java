package view;

import controller.PatientController;
import model.entity.Patient;
import model.enums.Gender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        this.controller = new PatientController(this, patient);
        setTitle("Patient Dashboard");
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
        gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        controller.showHome();

        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnHome.addActionListener(e -> controller.showHome());
        btnViewInfo.addActionListener(e -> controller.showPatientInfo());
        btnViewAppointments.addActionListener(e -> controller.showAppointments());
        btnViewMedicalHistory.addActionListener(e -> controller.showMedicalHistory());
        btnPayFees.addActionListener(e -> controller.showPayFees());
        btnPaymentHistory.addActionListener(e -> controller.showPaymentHistory());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setPreferredSize(new Dimension(250, 60));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentSelectedButton) {
                    button.setBackground(new Color(41, 128, 185));
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

    public void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(new Color(52, 152, 219));
            currentSelectedButton.setPreferredSize(new Dimension(250, 60));
            currentSelectedButton.revalidate();
        }
        currentSelectedButton = selectedButton;
        currentSelectedButton.setBackground(Color.GRAY);
        currentSelectedButton.setPreferredSize(new Dimension(350, 70));
        currentSelectedButton.revalidate();
    }

    // Các phương thức hiển thị giao diện
    public void showHome() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        JLabel lblWelcome = new JLabel("Welcome to Patient Dashboard, " + patient.getFullName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(44, 62, 80));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showPatientInfo() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Your Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblPatientID = new JLabel("Patient ID:");
        lblPatientID.setFont(labelFont);
        lblPatientID.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblPatientID, gbc);

        gbc.gridx = 1;
        JLabel lblPatientIDValue = new JLabel(patient.getPatientID());
        lblPatientIDValue.setFont(fieldFont);
        contentPanel.add(lblPatientIDValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(labelFont);
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblName, gbc);

        gbc.gridx = 1;
        JLabel lblNameValue = new JLabel(patient.getFullName());
        lblNameValue.setFont(fieldFont);
        contentPanel.add(lblNameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblBirthDate = new JLabel("Date of Birth:");
        lblBirthDate.setFont(labelFont);
        lblBirthDate.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblBirthDate, gbc);

        gbc.gridx = 1;
        JLabel lblBirthDateValue = new JLabel(patient.getDateOfBirth().toString());
        lblBirthDateValue.setFont(fieldFont);
        contentPanel.add(lblBirthDateValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(labelFont);
        lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblAddress, gbc);

        gbc.gridx = 1;
        JLabel lblAddressValue = new JLabel(patient.getAddress() != null ? patient.getAddress() : "N/A");
        lblAddressValue.setFont(fieldFont);
        contentPanel.add(lblAddressValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        lblGender.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblGender, gbc);

        gbc.gridx = 1;
        JLabel lblGenderValue = new JLabel(patient.getGender().toString());
        lblGenderValue.setFont(fieldFont);
        contentPanel.add(lblGenderValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(labelFont);
        lblPhone.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblPhone, gbc);

        gbc.gridx = 1;
        JLabel lblPhoneValue = new JLabel(patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "N/A");
        lblPhoneValue.setFont(fieldFont);
        contentPanel.add(lblPhoneValue, gbc);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showAppointments(List<String[]> appointments) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Doctor", "Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String[] appointment : appointments) {
                tableModel.addRow(appointment);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showMedicalHistory(List<String[]> medicalHistory) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Medical History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Date", "Diagnosis", "Treatment", "Doctor"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable medicalHistoryTable = new JTable(tableModel);

        if (medicalHistory.isEmpty()) {
            tableModel.addRow(new Object[]{"No records found", "", "", ""});
        } else {
            for (String[] record : medicalHistory) {
                tableModel.addRow(record);
            }
        }

        JScrollPane scrollPane = new JScrollPane(medicalHistoryTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showPayFees() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Bill ID", "Amount", "Status", "Action"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 20));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        table.setRowHeight(30);

        List<Object[]> bills = controller.getBills();
        if (bills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No outstanding bills found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Object[] bill : bills) {
                tableModel.addRow(new Object[]{bill[0], bill[1], bill[2], "Make Payment"});
            }
        }

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), table));

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showPaymentHistory(Object[][] paymentHistory) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel historyLabel = new JLabel("Payment History", SwingConstants.CENTER);
        historyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(historyLabel, BorderLayout.NORTH);

        String[] columns = {"Bill ID", "Date", "Amount", "Method", "Status"};
        JTable table = new JTable(paymentHistory, columns);

        if (paymentHistory.length == 0) {
            JOptionPane.showMessageDialog(this, "No payment history found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPaymentForm(String billID, double amount) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Make Payment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        JTextField txtBillId = new JTextField(billID);
        txtBillId.setEditable(false);
        addFormField(formPanel, gbc, "Bill ID:", txtBillId, 1);

        JTextField txtBillingAmount = new JTextField(String.valueOf(amount));
        txtBillingAmount.setEditable(false);
        addFormField(formPanel, gbc, "Amount:", txtBillingAmount, 2);

        JComboBox<String> cbPaymentMethod = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        addFormField(formPanel, gbc, "Payment Method:", cbPaymentMethod, 3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSubmit = new JButton("Submit Payment");
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnSubmit);
        styleButton(btnCancel);

        btnSubmit.addActionListener(e -> {
            String method = (String) cbPaymentMethod.getSelectedItem();
            if (controller.payBill(billID, method)) {
                JOptionPane.showMessageDialog(this, "Payment processed successfully!");
                showPayFees();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to process payment!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> showPayFees());

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addFormField(JPanel formPanel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblField, gbc);

        gbc.gridx = 1;
        formPanel.add(field, gbc);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
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
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String billID = table.getValueAt(table.getSelectedRow(), 0).toString();
                double amount = (Double) table.getValueAt(table.getSelectedRow(), 1);
                showPaymentForm(billID, amount);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Patient patient = new Patient("USER001", "PAT-001", "Trần Thị B", LocalDate.of(1990, 5, 15),
                    "456 Đường XYZ, Hà Nội", Gender.FEMALE, "0976543210", LocalDate.now());
            SwingUtilities.invokeLater(() -> new PatientView(patient).setVisible(true));
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating patient: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}