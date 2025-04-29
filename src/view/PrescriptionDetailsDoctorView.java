package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class PrescriptionDetailsDoctorView extends JFrame {
    private JPanel contentPanel;
    private JTextField txtPrescriptionId, txtPatientId, txtPatientName, txtDiagnosis, txtDate;
    private JTextArea txtNotes;
    private DefaultTableModel medicineTableModel;
    private JTable medicineTable;
    private JButton btnSave, btnCancel, btnBack;
    private JButton btnAddMedicine, btnEditMedicine, btnDeleteMedicine;
    private String doctorId; // Thêm biến để lưu mã bác sĩ

    public PrescriptionDetailsDoctorView(String prescriptionId, String doctorId) {
        this.doctorId = doctorId;
        setTitle("Chi tiết đơn thuốc");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Info Panel
        JPanel infoPanel = createInfoPanel();
        
        // Center Panel - Medicine Table
        JPanel medicinePanel = createMedicinePanel();
        
        // Bottom Panel - Notes and Buttons
        JPanel bottomPanel = createBottomPanel();

        contentPanel.add(infoPanel, BorderLayout.NORTH);
        contentPanel.add(medicinePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel);

        // Add action listeners
        btnBack.addActionListener(e -> handleBack());
        btnCancel.addActionListener(e -> handleCancel());

        // Load prescription data
        loadPrescriptionData(prescriptionId);
    }

    private void handleBack() {
        dispose(); // Đóng cửa sổ hiện tại
        DoctorView doctorView = new DoctorView(doctorId);
        doctorView.setVisible(true);
    }

    private void handleCancel() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn hủy thay đổi?",
            "Xác nhận hủy",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Thông tin đơn thuốc"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1
        txtPrescriptionId = createStyledTextField();
        txtPatientId = createStyledTextField();
        addFormField(panel, gbc, "Mã đơn thuốc:", txtPrescriptionId, 0, 0);
        addFormField(panel, gbc, "Mã bệnh nhân:", txtPatientId, 0, 2);

        // Row 2
        txtPatientName = createStyledTextField();
        txtDate = createStyledTextField();
        addFormField(panel, gbc, "Tên bệnh nhân:", txtPatientName, 1, 0);
        addFormField(panel, gbc, "Ngày kê đơn:", txtDate, 1, 2);

        // Row 3
        txtDiagnosis = createStyledTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        panel.add(new JLabel("Chẩn đoán:"), gbc);
        gbc.gridy = 3;
        panel.add(txtDiagnosis, gbc);

        return panel;
    }

    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Danh sách thuốc"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Table
        String[] columns = {"STT", "Tên thuốc", "Đơn vị", "Số lượng", "Liều dùng", "Cách dùng"};
        medicineTableModel = new DefaultTableModel(columns, 0);
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setRowHeight(30);
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddMedicine = createStyledButton("Thêm thuốc", new Color(40, 167, 69));
        btnEditMedicine = createStyledButton("Sửa thuốc", new Color(0, 123, 255));
        btnDeleteMedicine = createStyledButton("Xóa thuốc", new Color(220, 53, 69));

        buttonPanel.add(btnAddMedicine);
        buttonPanel.add(btnEditMedicine);
        buttonPanel.add(btnDeleteMedicine);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        btnAddMedicine.addActionListener(e -> showAddMedicineDialog());
        btnEditMedicine.addActionListener(e -> showEditMedicineDialog());
        btnDeleteMedicine.addActionListener(e -> deleteMedicine());

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Notes Panel
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Ghi chú"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        txtNotes = new JTextArea(4, 40);
        txtNotes.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesPanel.add(notesScroll);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Left side - Back button
        btnBack = createStyledButton("← Quay về danh sách bệnh nhân", new Color(108, 117, 125));
        btnBack.setPreferredSize(new Dimension(250, 40));
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(btnBack);

        // Right side - Cancel and Save buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancel = createStyledButton("Hủy", new Color(108, 117, 125));
        btnSave = createStyledButton("Lưu đơn thuốc", new Color(40, 167, 69));
        
        // Set same size for all buttons
        Dimension buttonSize = new Dimension(150, 40);
        btnCancel.setPreferredSize(buttonSize);
        btnSave.setPreferredSize(buttonSize);
        
        rightButtons.add(btnCancel);
        rightButtons.add(Box.createHorizontalStrut(10)); // Add some spacing
        rightButtons.add(btnSave);

        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);

        panel.add(notesPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener
        btnSave.addActionListener(e -> savePrescription());

        return panel;
    }

    private void showAddMedicineDialog() {
        JDialog dialog = new JDialog(this, "Thêm thuốc", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtMedicineName = createStyledTextField();
        JTextField txtUnit = createStyledTextField();
        JTextField txtQuantity = createStyledTextField();
        JTextField txtDosage = createStyledTextField();
        JTextField txtUsage = createStyledTextField();
        
        addFormField(panel, gbc, "Tên thuốc:", txtMedicineName, 0, 0);
        addFormField(panel, gbc, "Đơn vị:", txtUnit, 1, 0);
        addFormField(panel, gbc, "Số lượng:", txtQuantity, 2, 0);
        addFormField(panel, gbc, "Liều dùng:", txtDosage, 3, 0);
        addFormField(panel, gbc, "Cách dùng:", txtUsage, 4, 0);
        
        JButton btnAdd = createStyledButton("Thêm", new Color(40, 167, 69));
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(btnAdd, gbc);
        
        btnAdd.addActionListener(e -> {
            int rowCount = medicineTableModel.getRowCount();
            medicineTableModel.addRow(new Object[]{
                rowCount + 1,
                txtMedicineName.getText(),
                txtUnit.getText(),
                txtQuantity.getText(),
                txtDosage.getText(),
                txtUsage.getText()
            });
            dialog.dispose();
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditMedicineDialog() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn thuốc cần sửa", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa thuốc", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtMedicineName = createStyledTextField();
        JTextField txtUnit = createStyledTextField();
        JTextField txtQuantity = createStyledTextField();
        JTextField txtDosage = createStyledTextField();
        JTextField txtUsage = createStyledTextField();
        
        // Fill current values
        txtMedicineName.setText((String) medicineTable.getValueAt(selectedRow, 1));
        txtUnit.setText((String) medicineTable.getValueAt(selectedRow, 2));
        txtQuantity.setText((String) medicineTable.getValueAt(selectedRow, 3));
        txtDosage.setText((String) medicineTable.getValueAt(selectedRow, 4));
        txtUsage.setText((String) medicineTable.getValueAt(selectedRow, 5));
        
        addFormField(panel, gbc, "Tên thuốc:", txtMedicineName, 0, 0);
        addFormField(panel, gbc, "Đơn vị:", txtUnit, 1, 0);
        addFormField(panel, gbc, "Số lượng:", txtQuantity, 2, 0);
        addFormField(panel, gbc, "Liều dùng:", txtDosage, 3, 0);
        addFormField(panel, gbc, "Cách dùng:", txtUsage, 4, 0);
        
        JButton btnSave = createStyledButton("Lưu", new Color(0, 123, 255));
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(btnSave, gbc);
        
        btnSave.addActionListener(e -> {
            medicineTableModel.setValueAt(txtMedicineName.getText(), selectedRow, 1);
            medicineTableModel.setValueAt(txtUnit.getText(), selectedRow, 2);
            medicineTableModel.setValueAt(txtQuantity.getText(), selectedRow, 3);
            medicineTableModel.setValueAt(txtDosage.getText(), selectedRow, 4);
            medicineTableModel.setValueAt(txtUsage.getText(), selectedRow, 5);
            dialog.dispose();
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn thuốc cần xóa", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa thuốc này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            medicineTableModel.removeRow(selectedRow);
            // Update STT column
            for (int i = 0; i < medicineTableModel.getRowCount(); i++) {
                medicineTableModel.setValueAt(i + 1, i, 0);
            }
        }
    }

    private void savePrescription() {
        // TODO: Implement save prescription logic
        JOptionPane.showMessageDialog(this,
            "Đã lưu đơn thuốc thành công!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadPrescriptionData(String prescriptionId) {
        // TODO: Load prescription data from database
        // This is sample data
        txtPrescriptionId.setText(prescriptionId);
        txtPatientId.setText("BN001");
        txtPatientName.setText("Nguyễn Văn A");
        txtDate.setText(LocalDate.now().toString());
        txtDiagnosis.setText("Viêm họng cấp");
        txtNotes.setText("Uống thuốc đều đặn, nghỉ ngơi nhiều");

        // Sample medicine data
        medicineTableModel.addRow(new Object[]{1, "Paracetamol", "Viên", "20", "2 viên/lần", "Ngày uống 3 lần sau ăn"});
        medicineTableModel.addRow(new Object[]{2, "Vitamin C", "Viên", "10", "1 viên/lần", "Ngày uống 1 lần sau ăn sáng"});
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, 
                            JComponent field, int row, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = col + 1;
        gbc.gridwidth = 1;
        panel.add(field, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            PrescriptionDetailsDoctorView frame = new PrescriptionDetailsDoctorView("DT001", "DOC001");
            frame.setVisible(true);
        });
    }
} 