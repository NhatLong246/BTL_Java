package view;

import controller.BillingController;
import model.entity.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BillingView extends JFrame {
    // Constants for colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 250);
    private static final Color HEADER_COLOR = new Color(34, 49, 63);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    private JPanel contentPanel;
    private Patient patient;
    private BillingController controller;
    private String billId;
    private String service;
    private double amount;
    private JComboBox<String> cbPaymentMethod;
    private boolean paymentSuccessful = false;
    private JPanel cardDetailsPanel;

    public BillingView(Patient patient, String billId, String service, double amount) {
        this.patient = patient;
        this.billId = billId;
        this.service = service;
        this.amount = amount;
        this.controller = new BillingController(this, patient);
        
        setTitle("Thanh toán hóa đơn");
        
        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        
        // Calculate size (75% of screen width)
        int width = (int)(screenWidth * 0.3); // Giảm kích thước xuống 30% màn hình
        int height = (int)(screenHeight * 0.6); // Giảm chiều cao xuống 60% màn hình
        setSize(width, height);
        
        // Position on right side
        setLocation(screenWidth - width - 50, 50); // Đặt vị trí cách phải 50px và cách trên 50px
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initializeUI();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!paymentSuccessful) {
                    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                }
            }
        });
    }

    private void initializeUI() {
        // Main content panel with shadow border
        contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Header Panel with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, HEADER_COLOR,
                    getWidth(), 0, new Color(52, 73, 94)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel titleLabel = new JLabel("Thanh toán hóa đơn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Form Panel with rounded corners
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2d.dispose();
            }
        };
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.weightx = 1.0;

        // Bill Information with styled labels
        addFormField(formPanel, gbc, "ID Hóa đơn:", createStyledLabel(billId), 0);
        addFormField(formPanel, gbc, "Dịch vụ:", createStyledLabel(service), 1);
        addFormField(formPanel, gbc, "Số tiền:", createStyledLabel(String.format("%,.0f VND", amount)), 2);

        // Styled Payment Method Dropdown
        cbPaymentMethod = createStyledComboBox(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản ngân hàng"});
        addFormField(formPanel, gbc, "Phương thức thanh toán:", cbPaymentMethod, 3);

        // Card Details Panel with animation
        cardDetailsPanel = createCardDetailsPanel();
        gbc.gridy = 4;
        formPanel.add(cardDetailsPanel, gbc);

        // Payment Method Change Listener
        cbPaymentMethod.addActionListener(e -> {
            boolean isCardPayment = "Thẻ tín dụng".equals(cbPaymentMethod.getSelectedItem());
            animateCardDetailsPanel(isCardPayment);
        });

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        JButton btnSubmit = createStyledButton("Thanh toán", SUCCESS_COLOR);
        JButton btnBack = createStyledButton("Quay lại", SECONDARY_COLOR);

        // Add button listeners
        addButtonListeners(btnSubmit, btnBack);

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnBack);

        // Layout assembly
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(wrapperPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(300, 35));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
        return comboBox;
    }

    private JPanel createCardDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setVisible(false);

        JTextField cardNumber = createStyledTextField();
        JTextField cardExpiry = createStyledTextField();
        
        panel.add(createStyledLabel("Số thẻ:"));
        panel.add(cardNumber);
        panel.add(createStyledLabel("Hạn thẻ (MM/YY):"));
        panel.add(cardExpiry);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 35));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(130, 40));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void animateCardDetailsPanel(boolean show) {
        Timer timer = new Timer(10, null);
        final int targetHeight = show ? 80 : 0;
        final int startHeight = cardDetailsPanel.getHeight();
        final int distance = targetHeight - startHeight;
        final int steps = 20;
        final int[] step = {0};

        timer.addActionListener(e -> {
            step[0]++;
            float progress = (float) step[0] / steps;
            int currentHeight = startHeight + (int) (distance * progress);
            
            cardDetailsPanel.setPreferredSize(new Dimension(cardDetailsPanel.getWidth(), currentHeight));
            cardDetailsPanel.setVisible(true);
            cardDetailsPanel.revalidate();
            cardDetailsPanel.repaint();

            if (step[0] >= steps) {
                timer.stop();
                cardDetailsPanel.setVisible(show);
            }
        });
        timer.start();
    }

    private void addButtonListeners(JButton btnSubmit, JButton btnBack) {
        btnSubmit.addActionListener(e -> handlePayment(btnSubmit, btnBack));
        btnBack.addActionListener(e -> dispose());
    }

    private void handlePayment(JButton btnSubmit, JButton btnBack) {
        String selectedMethod = (String) cbPaymentMethod.getSelectedItem();
        
        if ("Thẻ tín dụng".equals(selectedMethod)) {
            JTextField cardNumber = (JTextField) ((JPanel)cardDetailsPanel.getComponent(1)).getComponent(0);
            JTextField cardExpiry = (JTextField) ((JPanel)cardDetailsPanel.getComponent(3)).getComponent(0);
            
            if (!validateCardDetails(cardNumber.getText(), cardExpiry.getText())) {
                return;
            }
        }

        int confirm = showConfirmDialog(selectedMethod);

        if (confirm == JOptionPane.YES_OPTION) {
            processPayment(btnSubmit, btnBack);
        }
    }

    private int showConfirmDialog(String paymentMethod) {
        return JOptionPane.showConfirmDialog(
            this,
            String.format("<html><body style='width: 200px; padding: 10px;'>" +
                         "<div style='font-family: Segoe UI; font-size: 14px;'>" +
                         "Xác nhận thanh toán <b>%,.0f VND</b><br>bằng <b>%s</b>?</div></body></html>",
                         amount, paymentMethod),
            "Xác nhận thanh toán",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
    }

    private void processPayment(JButton btnSubmit, JButton btnBack) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnSubmit.setEnabled(false);
        btnBack.setEnabled(false);
        
        try {
            if (controller.payBill(billId, (String) cbPaymentMethod.getSelectedItem())) {
                paymentSuccessful = true;
                showSuccessMessage();
                dispose();
            } else {
                showErrorMessage();
            }
        } finally {
            setCursor(Cursor.getDefaultCursor());
            btnSubmit.setEnabled(true);
            btnBack.setEnabled(true);
        }
    }

    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(this,
            "<html><body style='width: 200px; padding: 10px;'>" +
            "<div style='font-family: Segoe UI; font-size: 14px; color: " + String.format("#%02x%02x%02x", 
                SUCCESS_COLOR.getRed(), SUCCESS_COLOR.getGreen(), SUCCESS_COLOR.getBlue()) + ";'>" +
            "✓ Thanh toán thành công!</div></body></html>",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage() {
        JOptionPane.showMessageDialog(this,
            "<html><body style='width: 200px; padding: 10px;'>" +
            "<div style='font-family: Segoe UI; font-size: 14px; color: " + String.format("#%02x%02x%02x",
                ERROR_COLOR.getRed(), ERROR_COLOR.getGreen(), ERROR_COLOR.getBlue()) + ";'>" +
            "⚠ Thanh toán thất bại. Vui lòng thử lại sau.</div></body></html>",
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }

    private boolean validateCardDetails(String cardNumber, String expiryDate) {
        if (cardNumber.isEmpty() || expiryDate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin thẻ tín dụng.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this,
                "Số thẻ không hợp lệ. Vui lòng nhập 16 chữ số.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Định dạng hạn thẻ không hợp lệ. Vui lòng nhập theo định dạng MM/YY.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    // Additional view methods called by controller
    private JButton btnViewInfo;
    private JButton btnPayment;
    private JButton btnHistory;

    public JButton getBtnViewInfo() {
        return btnViewInfo;
    }

    public JButton getBtnPayment() {
        return btnPayment;
    }

    public JButton getBtnHistory() {
        return btnHistory;
    }

    public void setSelectedButton(JButton button) {
        // Reset all buttons to default style
        for (JButton btn : new JButton[]{btnViewInfo, btnPayment, btnHistory}) {
            if (btn != null) {
                btn.setBackground(new Color(52, 152, 219));
            }
        }
        // Highlight selected button
        if (button != null) {
            button.setBackground(new Color(41, 128, 185));
        }
    }

    public void showWelcomeMessage() {
        JOptionPane.showMessageDialog(this,
            "Chào mừng đến với hệ thống thanh toán!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void showViewInfo(String totalBills, String paidBills, String pendingBills) {
        String message = String.format("""
            Thông tin thanh toán:
            Tổng số hóa đơn: %s
            Đã thanh toán: %s
            Chưa thanh toán: %s""",
            totalBills, paidBills, pendingBills);
            
        JOptionPane.showMessageDialog(this,
            message,
            "Thông tin thanh toán",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void showPaymentForm() {
        // Current view is already payment form
        // This method exists for compatibility with controller
    }

    public void showPaymentHistory(Object[][] history) {
        if (history == null || history.length == 0) {
            JOptionPane.showMessageDialog(this,
                "Không có lịch sử thanh toán",
                "Lịch sử thanh toán",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"Mã hóa đơn", "Ngày thanh toán", "Dịch vụ", "Số tiền", "Phương thức"};
        JTable table = new JTable(history, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JDialog dialog = new JDialog(this, "Lịch sử thanh toán", true);
        dialog.setLayout(new BorderLayout());
        
        // Panel chính chứa bảng và nút
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        JButton closeButton = createStyledButton("Đóng", SECONDARY_COLOR);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        
        // Khôi phục kích thước và vị trí ban đầu
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}