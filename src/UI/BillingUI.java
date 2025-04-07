package UI;

import javax.swing.*;



import java.awt.*;
import java.awt.event.*;
import entity.Patient;
import java.sql.*;
import java.time.LocalDate;

public class BillingUI extends JFrame {
    private JPanel contentPanel;
    private JTextField txtPatientId, txtBillingAmount;
    private JComboBox<String> cbPaymentMethod;
    private JButton btnSubmit, btnCancel;
    private JButton currentSelectedButton;
    private Patient patient;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2005";

    public BillingUI(Patient patient) {
        this.patient = patient;
        setTitle("Billing Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backToPatientUI();
            }
        });

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

        JLabel menuTitle = new JLabel("Billing Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 50));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10);
        gbc.weighty = 0.1;
        leftPanel.add(menuTitle, gbc);

        JButton btnViewInfo = createMenuButton("View Info");
        JButton btnPayment = createMenuButton("Make Payment");
        JButton btnHistory = createMenuButton("Payment History");
        JButton btnBack = createMenuButton("Back to Patient Dashboard");

        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.weighty = 0.0;

        gbc.gridy = 1;
        leftPanel.add(btnViewInfo, gbc);

        gbc.gridy = 2;
        leftPanel.add(btnPayment, gbc);

        gbc.gridy = 3;
        leftPanel.add(btnHistory, gbc);

        gbc.gridy = 4;
        leftPanel.add(btnBack, gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        showWelcomeMessage();

        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnViewInfo.addActionListener(e -> {
            setSelectedButton(btnViewInfo);
            showViewInfo();
        });

        btnPayment.addActionListener(e -> {
            setSelectedButton(btnPayment);
            showPaymentForm();
        });

        btnHistory.addActionListener(e -> {
            setSelectedButton(btnHistory);
            showPaymentHistory();
        });

        btnBack.addActionListener(e -> backToPatientUI());

        setSelectedButton(btnViewInfo);
    }

    private JButton createMenuButton(String text) {
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

    private void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(new Color(52, 152, 219));
            currentSelectedButton.setPreferredSize(new Dimension(250, 60));
        }
        currentSelectedButton = selectedButton;
        currentSelectedButton.setBackground(Color.GRAY);
        currentSelectedButton.setPreferredSize(new Dimension(280, 70));
    }

    private void showWelcomeMessage() {
        contentPanel.removeAll();
        JLabel welcomeLabel = new JLabel("Welcome to Billing Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewInfo() {
        contentPanel.removeAll();  // Xóa hết nội dung cũ

        contentPanel.setLayout(new GridBagLayout());  // Đảm bảo sử dụng GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Khoảng cách giữa các phần tử
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Căn chỉnh các phần tử theo chiều ngang

        // Tiêu đề
        JLabel titleLabel = new JLabel("Billing Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        contentPanel.add(titleLabel, gbc);

        // Các trường thông tin
        gbc.gridwidth = 1;  // Chỉ chiếm 1 cột cho các trường
        addInfoField(gbc, "Patient ID:", patient.getPatientID(), 1);
        addInfoField(gbc, "Patient Name:", patient.getFullName(), 2);
        addInfoField(gbc, "Total Bills:", getTotalBills(patient.getPatientID()), 3);
        addInfoField(gbc, "Paid Bills:", getPaidBills(patient.getPatientID()), 4);
        addInfoField(gbc, "Pending Bills:", getPendingBills(patient.getPatientID()), 5);

        // Cập nhật lại giao diện
        contentPanel.revalidate();
        contentPanel.repaint();
    }



    private void showPaymentForm() {
        contentPanel.removeAll();  // Xóa hết nội dung cũ
        contentPanel.setLayout(new BorderLayout());  // Thiết lập lại layout

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
        txtPatientId = new JTextField(patient.getPatientID());
        txtPatientId.setEditable(false);
        addFormField(formPanel, gbc, "Patient ID:", txtPatientId, 1);
        addFormField(formPanel, gbc, "Amount:", txtBillingAmount = new JTextField(20), 2);
        addFormField(formPanel, gbc, "Payment Method:", 
                    cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Cash", "Bank Transfer"}), 3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSubmit = new JButton("Submit Payment");
        btnCancel = new JButton("Cancel");
        styleButton(btnSubmit);
        styleButton(btnCancel);

        btnSubmit.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtBillingAmount.getText());
                String method = (String) cbPaymentMethod.getSelectedItem();
                if (processPayment(amount, method)) {
                    JOptionPane.showMessageDialog(this, "Payment processed successfully!");
                    txtBillingAmount.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
            }
        });

        btnCancel.addActionListener(e -> txtBillingAmount.setText(""));

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();  // Cập nhật lại giao diện
    }


    private void showPaymentHistory() {
        contentPanel.removeAll();  // Xóa hết nội dung cũ
        contentPanel.setLayout(new BorderLayout());  // Thiết lập lại layout

        JLabel historyLabel = new JLabel("Payment History", SwingConstants.CENTER);
        historyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(historyLabel, BorderLayout.NORTH);
        
        String[] columns = {"Payment ID", "Date", "Amount", "Method", "Status", "Description"};
        Object[][] data = getPaymentHistory(patient.getPatientID());
        JTable table = new JTable(data, columns);
        
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();  // Cập nhật lại giao diện
    }


    private void addInfoField(GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(lblField, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 16));
        contentPanel.add(lblValue, gbc);
    }

    private String getTotalBills(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT SUM(TotalAmount) FROM Billing WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("$%.2f", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }


    private String getPaidBills(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT SUM(TotalAmount) FROM Billing WHERE PatientID = ? AND Status = 'Đã thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("$%.2f", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }


    private String getPendingBills(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT SUM(TotalAmount) FROM Billing WHERE PatientID = ? AND Status = 'Chưa thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("$%.2f", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }


    private boolean processPayment(double amount, String method) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Tạo BillID mới cho hóa đơn
            String billID = generateBillID();  // Tạo mã hóa đơn duy nhất

            // Thêm hóa đơn vào bảng Billing
            String query = "INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, billID);
            stmt.setString(2, patient.getPatientID());
            stmt.setDouble(3, amount);
            stmt.setString(4, method);
            stmt.setString(5, "Chưa thanh toán");  // Trạng thái mặc định là chưa thanh toán

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // Nếu thêm hóa đơn thành công, ta thêm chi tiết vào bảng BillingDetails
                addBillingDetails(billID, amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addBillingDetails(String billID, double amount) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Thêm chi tiết hóa đơn vào bảng BillingDetails
            String query = "INSERT INTO BillingDetails (BillID, ServiceID, Amount) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, billID);
            stmt.setString(2, "SERVICE001");  // Sử dụng một ID dịch vụ cố định hoặc nhập từ UI
            stmt.setDouble(3, amount);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateBillID() {
        // Tạo BillID duy nhất, có thể sử dụng mã tự động tăng hoặc một cách tạo mã riêng
        return "BILL" + System.currentTimeMillis();  // Ví dụ tạo BillID từ thời gian hiện tại
    }


    private Object[][] getPaymentHistory(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT BillID, CreatedAt, TotalAmount, PaymentMethod, Status FROM Billing WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            // Build result set
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][5];
            int rowIndex = 0;
            while (rs.next()) {
                data[rowIndex][0] = rs.getString("BillID");
                data[rowIndex][1] = rs.getDate("CreatedAt");
                data[rowIndex][2] = rs.getDouble("TotalAmount");
                data[rowIndex][3] = rs.getString("PaymentMethod");
                data[rowIndex][4] = rs.getString("Status");
                rowIndex++;
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[0][0];  // Return empty array if error occurs
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

    private void backToPatientUI() {
        // Code to go back to the Patient Dashboard UI
        System.exit(0); // Just an example, you should navigate to the patient UI
    }
    
    
    public static void main(String[] args) {
        // Example patient for testing
        Patient testPatient = new Patient("John Doe", LocalDate.of(1990, 5, 15), "123 Main St", enums.Gender.MALE, "0912345678", LocalDate.now());
        SwingUtilities.invokeLater(() -> new BillingUI(testPatient).setVisible(true));
    }
}
