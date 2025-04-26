package view;

import controller.BillingController;
import model.entity.Patient;
import model.enums.Gender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

public class BillingView extends JFrame {
    private JPanel contentPanel;
    private JButton btnViewInfo, btnPayment, btnHistory, btnBack;
    private JButton currentSelectedButton;
    private Patient patient;
    private BillingController controller;
    private JTextField txtBillingAmount;
    private JComboBox<String> cbPaymentMethod;

    public BillingView(Patient patient) {
        this.patient = patient;
        this.controller = new BillingController(this, patient);
        setTitle("Billing Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.backToPatientUI();
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

        btnViewInfo = createMenuButton("View Info");
        btnPayment = createMenuButton("Make Payment");
        btnHistory = createMenuButton("Payment History");
        btnBack = createMenuButton("Back to Patient Dashboard");

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
        controller.showWelcomeMessage();

        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnViewInfo.addActionListener(e -> controller.showViewInfo());
        btnPayment.addActionListener(e -> controller.showPaymentForm());
        btnHistory.addActionListener(e -> controller.showPaymentHistory());
        btnBack.addActionListener(e -> controller.backToPatientUI());

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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (button != currentSelectedButton) {
                    button.setBackground(new Color(41, 128, 185));
                }
            }

            public void mouseExited(MouseEvent evt) {
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
        }
        currentSelectedButton = selectedButton;
        currentSelectedButton.setBackground(Color.GRAY);
        currentSelectedButton.setPreferredSize(new Dimension(280, 70));
    }

    // Các phương thức hiển thị giao diện (thay thế cho các panel)
    public void showWelcomeMessage() {
        contentPanel.removeAll();
        JLabel welcomeLabel = new JLabel("Welcome to Billing Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showViewInfo(String totalBills, String paidBills, String pendingBills) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Billing Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        addInfoField(gbc, "Patient ID:", patient.getPatientID(), 1);
        addInfoField(gbc, "Patient Name:", patient.getFullName(), 2);
        addInfoField(gbc, "Total Bills:", totalBills, 3);
        addInfoField(gbc, "Paid Bills:", paidBills, 4);
        addInfoField(gbc, "Pending Bills:", pendingBills, 5);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showPaymentForm() {
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
        JTextField txtPatientId = new JTextField(patient.getPatientID());
        txtPatientId.setEditable(false);
        addFormField(formPanel, gbc, "Patient ID:", txtPatientId, 1);
        addFormField(formPanel, gbc, "Amount:", txtBillingAmount = new JTextField(20), 2);
        addFormField(formPanel, gbc, "Payment Method:", 
                     cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Cash", "Bank Transfer"}), 3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSubmit = new JButton("Submit Payment");
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnSubmit);
        styleButton(btnCancel);

        btnSubmit.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtBillingAmount.getText());
                String method = (String) cbPaymentMethod.getSelectedItem();
                if (controller.processPayment(amount, method)) {
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
        contentPanel.repaint();
    }

    public void showPaymentHistory(Object[][] paymentHistory) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel historyLabel = new JLabel("Payment History", SwingConstants.CENTER);
        historyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(historyLabel, BorderLayout.NORTH);

        String[] columns = {"Payment ID", "Date", "Amount", "Method", "Status"};
        JTable table = new JTable(paymentHistory, columns);

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
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

    // Getter cho Controller
    public JPanel getContentPanel() {
        return contentPanel;
    }

    public JButton getBtnViewInfo() {
        return btnViewInfo;
    }

    public JButton getBtnPayment() {
        return btnPayment;
    }

    public JButton getBtnHistory() {
        return btnHistory;
    }

    public Patient getPatient() {
        return patient;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Patient testPatient = new Patient("user123", "patient123", "John Doe", LocalDate.of(1990, 5, 15), "123 Main St", Gender.MALE, "0912345678", LocalDate.now());
        SwingUtilities.invokeLater(() -> new BillingView(testPatient).setVisible(true));
    }
}