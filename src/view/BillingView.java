package view;

import controller.BillingController;
import model.entity.Patient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
    private boolean paymentSuccessful = false;
    private JPanel paymentDetailsPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> paymentPanels;
    private JPanel selectedMethodPanel;

    // Payment method constants
    private static final String CASH = "Tiền mặt";
    private static final String CREDIT_CARD = "Thẻ tín dụng";
    private static final String BANK_TRANSFER = "Chuyển khoản ngân hàng";
    private static final String MOMO = "Ví MoMo";
    private static final String ZALOPAY = "ZaloPay";
    private static final String VNPAY = "VNPay";

    // Navigation buttons and related methods
    private JButton btnViewInfo;
    private JButton btnPayment;
    private JButton btnHistory;

    public BillingView(Patient patient, String billId, String service, double amount) {
        this.patient = patient;
        this.billId = billId;
        this.service = service;
        this.amount = amount;
        this.controller = new BillingController(this, patient);
        
        setTitle("Thanh toán hóa đơn");
        setupWindowProperties();
        initializeUI();
    }
        
    private void setupWindowProperties() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.55);
        int height = (int)(screenSize.height * 0.8);
        setSize(width, height);
        setLocation(screenSize.width - width - 50, 50);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initializeUI() {
        contentPanel = createMainPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel);
        initializePaymentPanels();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    private JPanel createHeaderPanel() {
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

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Bill Information Panel
        JPanel billInfoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        billInfoPanel.setBackground(Color.WHITE);
        billInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            "Thông tin hóa đơn",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            SECONDARY_COLOR
        ));

        addFormField(billInfoPanel, "ID Hóa đơn:", billId);
        addFormField(billInfoPanel, "Dịch vụ:", service);
        addFormField(billInfoPanel, "Số tiền:", String.format("%,.0f VND", amount));

        // Payment Methods Panel
        JPanel paymentMethodsPanel = createPaymentMethodsPanel();

        // Combine panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(billInfoPanel, BorderLayout.NORTH);
        topPanel.add(paymentMethodsPanel, BorderLayout.CENTER);

        formPanel.add(topPanel, BorderLayout.NORTH);

        // Payment Details Panel with CardLayout
        paymentDetailsPanel = new JPanel();
        cardLayout = new CardLayout();
        paymentDetailsPanel.setLayout(cardLayout);
        formPanel.add(paymentDetailsPanel, BorderLayout.CENTER);

        return formPanel;
    }

    private void addFormField(JPanel panel, String label, String value) {
        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblField.setForeground(TEXT_COLOR);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblValue.setForeground(TEXT_COLOR);

        panel.add(lblField);
        panel.add(lblValue);
    }

    private JPanel createPaymentMethodsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            "Chọn phương thức thanh toán",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            SECONDARY_COLOR
        ));

        String[] methods = {CASH, CREDIT_CARD, BANK_TRANSFER, MOMO, ZALOPAY, VNPAY};
        ButtonGroup group = new ButtonGroup();

        for (String method : methods) {
            JRadioButton rb = createPaymentMethodButton(method);
            group.add(rb);
            panel.add(rb);
        }

        return panel;
    }

    private JRadioButton createPaymentMethodButton(String method) {
        JRadioButton rb = new JRadioButton(method);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rb.setBackground(Color.WHITE);
        rb.setForeground(TEXT_COLOR);
        rb.setFocusPainted(false);
        rb.addActionListener(e -> handlePaymentMethodSelection(method));
        return rb;
    }

    private void initializePaymentPanels() {
        paymentPanels = new HashMap<>();
        
        // Initialize different payment panels
        paymentPanels.put(CASH, createCashPanel());
        paymentPanels.put(CREDIT_CARD, createCreditCardPanel());
        paymentPanels.put(BANK_TRANSFER, createBankTransferPanel());
        paymentPanels.put(MOMO, createEWalletPanel(MOMO));
        paymentPanels.put(ZALOPAY, createEWalletPanel(ZALOPAY));
        paymentPanels.put(VNPAY, createEWalletPanel(VNPAY));

        // Add all panels to the card layout
        for (Map.Entry<String, JPanel> entry : paymentPanels.entrySet()) {
            paymentDetailsPanel.add(entry.getValue(), entry.getKey());
        }
    }

    private JPanel createCashPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Vui lòng thanh toán tại quầy thu ngân");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label);
        
        return panel;
    }

    private JPanel createCreditCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField cardNumber = createStyledTextField("Số thẻ");
        JTextField cardHolder = createStyledTextField("Tên chủ thẻ");
        JTextField expiry = createStyledTextField("MM/YY");
        JTextField cvv = createStyledTextField("CVV");

        panel.add(cardNumber, gbc);
        panel.add(cardHolder, gbc);
        
        JPanel expiryAndCvv = new JPanel(new GridLayout(1, 2, 10, 0));
        expiryAndCvv.setBackground(Color.WHITE);
        expiryAndCvv.add(expiry);
        expiryAndCvv.add(cvv);
        
        panel.add(expiryAndCvv, gbc);

        return panel;
    }

    private JPanel createBankTransferPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        String bankInfo = "<html>"
            + "<div style='font-size:13px;'>"
            + "<b>Ngân hàng:</b> MBBank"
            + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "<b>Số tài khoản:</b> 0812299544"
            + "</div>"
            + "<br>"
            + "<b>Chủ tài khoản:</b> BỆNH VIỆN ABC<br>"
            + "<b>Nội dung:</b> " + billId + "<br><br>"
            + "<b>Số tiền:</b> " + String.format("%,.0f VND", amount)
            + "</html>";

        JLabel label = new JLabel(bankInfo);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);

        // Add QR code for bank transfer using VietQR
        try {
            String qrUrl = "https://img.vietqr.io/image/MB-0812299544-compact2.png?amount="
                + String.format("%.0f", amount) + "&addInfo=" + billId;
            ImageIcon qrIcon = new ImageIcon(new java.net.URL(qrUrl));
            Image image = qrIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            qrIcon = new ImageIcon(image);
            JLabel qrLabel = new JLabel(qrIcon);
            panel.add(qrLabel, gbc);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Không thể tải mã QR");
            panel.add(errorLabel, gbc);
        }

        return panel;
    }

    private JPanel createEWalletPanel(String walletType) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        String walletInfo = "<html>" +
            "Quét mã QR bằng ứng dụng " + walletType + " để thanh toán<br>" +
            "Số tiền: " + String.format("%,.0f VND", amount) +
            "</html>";

        JLabel label = new JLabel(walletInfo);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);

        // Nếu là MoMo thì chỉ hiển thị placeholder để chèn ảnh QR thủ công
        if (walletType.equals(MOMO)) {
        ImageIcon qrIcon = new ImageIcon("resources/img/momo_qr.png");
        Image image = qrIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        qrIcon = new ImageIcon(image);
        JLabel qrLabel = new JLabel(qrIcon);
        panel.add(qrLabel, gbc);
        return panel;
}

        // Add QR code for VNPay, ZaloPay (QR thật)
        try {
            String qrUrl = null;
            if (walletType.equals(VNPAY) || walletType.equals(ZALOPAY)) {
                qrUrl = "https://img.vietqr.io/image/MB-0812299544-compact2.png?amount="
                    + String.format("%.0f", amount) + "&addInfo=" + billId;
            } else {
                // Fallback: QR code tự tạo
                BufferedImage qrImage = generateQRCode(walletType + ":" + billId + ":" + amount);
                ImageIcon qrIcon = new ImageIcon(qrImage.getScaledInstance(180, 180, Image.SCALE_SMOOTH));
                JLabel qrLabel = new JLabel(qrIcon);
                panel.add(qrLabel, gbc);
                return panel;
            }
            ImageIcon qrIcon = new ImageIcon(new java.net.URL(qrUrl));
            Image image = qrIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            qrIcon = new ImageIcon(image);
            JLabel qrLabel = new JLabel(qrIcon);
            panel.add(qrLabel, gbc);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Không thể tạo mã QR");
            panel.add(errorLabel, gbc);
        }

        return panel;
    }

    private BufferedImage generateQRCode(String content) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add placeholder
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        
        return textField;
    }

    private void handlePaymentMethodSelection(String method) {
        cardLayout.show(paymentDetailsPanel, method);
        selectedMethodPanel = paymentPanels.get(method);
        revalidate();
        repaint();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        JButton btnSubmit = createStyledButton("Thanh toán", SUCCESS_COLOR);
        JButton btnBack = createStyledButton("Quay lại", SECONDARY_COLOR);

        btnSubmit.addActionListener(e -> handlePayment());
        btnBack.addActionListener(e -> dispose());

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnBack);

        return buttonPanel;
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

    private void handlePayment() {
        if (selectedMethodPanel == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn phương thức thanh toán",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
                return;
            }

        // Validate payment details based on selected method
        if (!validatePaymentDetails()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            String.format("<html><body style='width: 200px; padding: 10px;'>" +
                         "<div style='font-family: Segoe UI; font-size: 14px;'>" +
                         "Xác nhận thanh toán <b>%,.0f VND</b>?</div></body></html>",
                         amount),
            "Xác nhận thanh toán",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            processPayment();
        }
    }

    private boolean validatePaymentDetails() {
        // Add validation logic based on payment method
        return true;
    }

    private void processPayment() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            if (controller.payBill(billId, getSelectedPaymentMethod())) {
                paymentSuccessful = true;
                showSuccessMessage();
                dispose();
            } else {
                showErrorMessage();
            }
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private String getSelectedPaymentMethod() {
        for (Map.Entry<String, JPanel> entry : paymentPanels.entrySet()) {
            if (entry.getValue() == selectedMethodPanel) {
                return entry.getKey();
        }
        }
        return "";
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

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    // Navigation buttons and related methods
    public JButton getBtnViewInfo() {
        if (btnViewInfo == null) {
            btnViewInfo = createStyledButton("Xem thông tin", SECONDARY_COLOR);
        }
        return btnViewInfo;
    }

    public JButton getBtnPayment() {
        if (btnPayment == null) {
            btnPayment = createStyledButton("Thanh toán", SECONDARY_COLOR);
        }
        return btnPayment;
    }

    public JButton getBtnHistory() {
        if (btnHistory == null) {
            btnHistory = createStyledButton("Lịch sử", SECONDARY_COLOR);
        }
        return btnHistory;
    }

    public void setSelectedButton(JButton button) {
        // Reset all buttons to default style
        JButton[] buttons = {getBtnViewInfo(), getBtnPayment(), getBtnHistory()};
        for (JButton btn : buttons) {
            if (btn != null) {
                btn.setBackground(SECONDARY_COLOR);
            }
        }
        // Highlight selected button
        if (button != null) {
            button.setBackground(PRIMARY_COLOR);
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
        
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            Patient dummyPatient = new Patient();
            BillingView billingView = new BillingView(dummyPatient, "BILL001", "Khám bệnh", 500000.00);
            billingView.setVisible(true);
        });
    }
}