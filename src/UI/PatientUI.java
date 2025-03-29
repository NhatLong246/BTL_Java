package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import entity.Patient;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PatientUI extends JFrame {
    private JPanel contentPanel;
    private Patient patient; // The logged-in patient
    private JButton btnViewInfo, btnBookAppointment, btnViewAppointments, btnPayFees;
    private JButton currentSelectedButton;

    public PatientUI(Patient patient) {
        this.patient = patient; // The logged-in patient
        setTitle("Patient Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int leftPanelWidth = (int) (screenSize.width * 0.25); // 25% of screen for left panel

        // Left panel (Menu)
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(leftPanelWidth, screenSize.height));
        leftPanel.setBackground(new Color(34, 45, 65)); // Dark blue background
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Menu title
        JLabel menuTitle = new JLabel("Patient Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10);
        gbc.weighty = 0.1;
        leftPanel.add(menuTitle, gbc);

        // Menu buttons
        btnViewInfo = createButton("View Info");
        btnBookAppointment = createButton("Book Appointment");
        btnViewAppointments = createButton("View Appointments");
        btnPayFees = createButton("Pay Fees");

        // Set initial selected button
        setSelectedButton(btnViewInfo);

        // Add buttons with spacing
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.weighty = 0.0;

        gbc.gridy = 1;
        leftPanel.add(btnViewInfo, gbc);

        gbc.gridy = 2;
        leftPanel.add(btnBookAppointment, gbc);

        gbc.gridy = 3;
        leftPanel.add(btnViewAppointments, gbc);

        gbc.gridy = 4;
        leftPanel.add(btnPayFees, gbc);

        // Add empty space at the bottom
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245)); // Light background
        resetToHome();

        // Add panels to frame
        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Event listeners for buttons
        btnViewInfo.addActionListener(e -> {
            setSelectedButton(btnViewInfo);
            showPatientInfo();
        });
        btnBookAppointment.addActionListener(e -> {
            setSelectedButton(btnBookAppointment);
            showBookAppointmentForm();
        });
        btnViewAppointments.addActionListener(e -> {
            setSelectedButton(btnViewAppointments);
            showAppointments();
        });
        btnPayFees.addActionListener(e -> {
            setSelectedButton(btnPayFees);
            showPayFeesForm();
        });
    }

    // Create a styled button
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219)); // Blue background
        button.setPreferredSize(new Dimension(250, 60));
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

    // Set the selected button style
    private void setSelectedButton(JButton selectedButton) {
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

    // Home screen
    private void resetToHome() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        JLabel lblWelcome = new JLabel("Welcome to Patient Dashboard", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(44, 62, 80));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Show patient information (only fields from Patient/Person class)
    private void showPatientInfo() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Your Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        // Patient info labels
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
        JLabel lblPatientIDValue = new JLabel(patient.getPatientID() != null ? patient.getPatientID() : "N/A");
        lblPatientIDValue.setFont(fieldFont);
        contentPanel.add(lblPatientIDValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(labelFont);
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblName, gbc);

        gbc.gridx = 1;
        JLabel lblNameValue = new JLabel(patient.getFullName() != null ? patient.getFullName() : "N/A");
        lblNameValue.setFont(fieldFont);
        contentPanel.add(lblNameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblBirthDate = new JLabel("Date of Birth:");
        lblBirthDate.setFont(labelFont);
        lblBirthDate.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblBirthDate, gbc);

        gbc.gridx = 1;
        JLabel lblBirthDateValue = new JLabel(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "N/A");
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
        JLabel lblGenderValue = new JLabel(patient.getGender() != null ? patient.getGender().toString() : "N/A");
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

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel lblCreatedAt = new JLabel("Created At:");
        lblCreatedAt.setFont(labelFont);
        lblCreatedAt.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblCreatedAt, gbc);

        gbc.gridx = 1;
        JLabel lblCreatedAtValue = new JLabel(patient.getCreatedAt() != null ? patient.getCreatedAt().toString() : "N/A");
        lblCreatedAtValue.setFont(fieldFont);
        contentPanel.add(lblCreatedAtValue, gbc);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Form to book an appointment
    private void showBookAppointmentForm() {
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
        JTextField txtDate = new JTextField(40);
        txtDate.setFont(new Font("Arial", Font.PLAIN, 16));
        txtDate.setPreferredSize(new Dimension(400, 60));

        // Placeholder list of doctors (you can fetch this from the database if needed)
        String[] doctors = {"Dr. Smith", "Dr. Johnson", "Dr. Lee"};
        JComboBox<String> cbDoctor = new JComboBox<>(doctors);
        cbDoctor.setFont(new Font("Arial", Font.PLAIN, 16));
        cbDoctor.setPreferredSize(new Dimension(400, 60));

        JButton btnBook = createButton("Book");
        btnBook.setPreferredSize(new Dimension(200, 60));

        Font labelFont = new Font("Arial", Font.PLAIN, 18);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblDate = new JLabel("Appointment Date (YYYY-MM-DD):");
        lblDate.setFont(labelFont);
        lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblDate, gbc);
        gbc.gridx = 1;
        contentPanel.add(txtDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblDoctor = new JLabel("Doctor:");
        lblDoctor.setFont(labelFont);
        lblDoctor.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblDoctor, gbc);
        gbc.gridx = 1;
        contentPanel.add(cbDoctor, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnBook, gbc);

        btnBook.addActionListener(e -> {
            String dateStr = txtDate.getText().trim();
            String doctor = (String) cbDoctor.getSelectedItem();

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate appointmentDate = LocalDate.parse(dateStr);
                boolean booked = bookAppointment(patient.getPatientID(), doctor, appointmentDate);
                if (booked) {
                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    resetToHome();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to book appointment!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format (YYYY-MM-DD)!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Book an appointment in the database
    private boolean bookAppointment(String patientID, String doctor, LocalDate appointmentDate) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb?autoReconnect=true&useSSL=false", "root", "2005")) {
            String query = "INSERT INTO appointments (patient_id, doctor_name, appointment_date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            stmt.setString(2, doctor);
            stmt.setString(3, appointmentDate.toString());
            stmt.setString(4, "Scheduled");
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Show scheduled appointments
    private void showAppointments() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Doctor", "Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        List<String[]> appointments = getAppointmentsFromDatabase(patient.getPatientID());
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

    // Retrieve appointments from the database
    private List<String[]> getAppointmentsFromDatabase(String patientID) {
        List<String[]> appointments = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb?autoReconnect=true&useSSL=false", "root", "2005")) {
            String query = "SELECT doctor_name, appointment_date, status FROM appointments WHERE patient_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new String[]{
                    rs.getString("doctor_name"),
                    rs.getString("appointment_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // Form to pay hospital fees
    private void showPayFeesForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Bill ID", "Amount", "Status", "Action"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only the "Action" column is editable
            }
        };
        JTable table = new JTable(tableModel);

        List<Object[]> bills = getBillsFromDatabase(patient.getPatientID());
        if (bills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No outstanding bills found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Object[] bill : bills) {
                tableModel.addRow(new Object[]{bill[0], bill[1], bill[2], "Pay"});
            }
        }

        // Add a button renderer/editor for the "Pay" button in the table
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), table, this));

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Retrieve bills from the database
    private List<Object[]> getBillsFromDatabase(String patientID) {
        List<Object[]> bills = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb?autoReconnect=true&useSSL=false", "root", "2005")) {
            String query = "SELECT id, amount, status FROM bills WHERE patient_id = ? AND status = 'Unpaid'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bills.add(new Object[]{
                    rs.getString("id"),
                    rs.getDouble("amount"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    // Pay a bill
    private boolean payBill(String billID) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb?autoReconnect=true&useSSL=false", "root", "2005")) {
            String query = "UPDATE bills SET status = 'Paid' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, billID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Button renderer for the "Pay" button in the table
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

    // Button editor for the "Pay" button in the table
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private PatientUI patientUI;

        public ButtonEditor(JCheckBox checkBox, JTable table, PatientUI patientUI) {
            super(checkBox);
            this.table = table;
            this.patientUI = patientUI;
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
                boolean paid = patientUI.payBill(billID);
                if (paid) {
                    JOptionPane.showMessageDialog(patientUI, "Bill paid successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    patientUI.showPayFeesForm(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(patientUI, "Failed to pay bill!", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

    public static void main(String[] args) {
        try {
            // Example patient for testing with a valid phone number
            Patient patient = new Patient("John Doe", LocalDate.of(1990, 5, 15), "123 Main St", enums.Gender.MALE, "0912345678", LocalDate.now());
            SwingUtilities.invokeLater(() -> new PatientUI(patient).setVisible(true));
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating patient: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}