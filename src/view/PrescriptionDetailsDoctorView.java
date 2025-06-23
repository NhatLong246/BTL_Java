package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.PrescriptionController;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class PrescriptionDetailsDoctorView extends JFrame {
    private JPanel contentPanel;
    private JTextField txtPrescriptionId, txtPatientId, txtPatientName, txtDiagnosis, txtDate;
    private JTextArea txtNotes;
    private DefaultTableModel medicineTableModel;
    private JTable medicineTable;
    private JButton btnSave, btnCancel, btnBack;
    private JButton btnAddMedicine, btnEditMedicine, btnDeleteMedicine;
    private String doctorId; // Thêm biến để lưu mã bác sĩ
    private JTextArea txtTreatmentPlan;

    private String patientId;
    private String patientName;
    private String prescriptionId;
    private boolean isNewPrescription;
    private PrescriptionController controller;

    public PrescriptionDetailsDoctorView(String doctorId, String patientId, String patientName, 
                                        String prescriptionId, boolean isNew) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.prescriptionId = prescriptionId;
        this.isNewPrescription = isNew;

        this.controller = new PrescriptionController(this);

        initUI();

        if (isNewPrescription) {
            if (prescriptionId == null || prescriptionId.equals("[Tự động tạo]")) {
                this.prescriptionId = controller.generatePrescriptionId();
            }
            
            txtPrescriptionId.setText(this.prescriptionId);
            txtPatientId.setText(patientId);
            txtPatientName.setText(patientName);
            txtDate.setText(LocalDate.now().toString());
            
            // Lấy chẩn đoán gần nhất từ hồ sơ y tế
            String latestDiagnosis = controller.getLatestDiagnosis(patientId);
            if (latestDiagnosis != null && !latestDiagnosis.isEmpty()) {
                txtDiagnosis.setText(latestDiagnosis);
            }
        } else {
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
    }

    private void initUI() {
        setTitle("Kê đơn thuốc");
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
        btnSave.addActionListener(e -> savePrescription());
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
        medicineTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setRowHeight(30);
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.getTableHeader().setReorderingAllowed(false);
    
        // Đặt độ rộng cho các cột
        medicineTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        medicineTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên thuốc
        medicineTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Đơn vị
        medicineTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        medicineTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Liều dùng
        medicineTable.getColumnModel().getColumn(5).setPreferredWidth(250); // Cách dùng
    
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
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

    /* private JPanel createBottomPanel() {
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
    } */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Tạo panel chứa cả ghi chú và phác đồ điều trị
        JPanel notesPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        
        // Panel phác đồ điều trị
        JPanel treatmentPanel = new JPanel(new BorderLayout());
        treatmentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Phác đồ điều trị"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        txtTreatmentPlan = new JTextArea(4, 40);
        txtTreatmentPlan.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTreatmentPlan.setLineWrap(true);
        txtTreatmentPlan.setWrapStyleWord(true);
        JScrollPane treatmentScroll = new JScrollPane(txtTreatmentPlan);
        treatmentPanel.add(treatmentScroll);
        
        // Panel ghi chú
        JPanel oldNotesPanel = new JPanel(new BorderLayout());
        oldNotesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Ghi chú"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        txtNotes = new JTextArea(4, 40);
        txtNotes.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        oldNotesPanel.add(notesScroll);
        
        // Thêm cả 2 panel vào notesPanel
        notesPanel.add(treatmentPanel);
        notesPanel.add(oldNotesPanel);

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

    private void loadPrescriptionData(String prescriptionId) {
        // TODO: Load prescription data from database
        // This is sample data
        txtPrescriptionId.setText(prescriptionId);
        txtPatientId.setText("BN001");
        txtPatientName.setText("Nguyễn Văn A");
        txtDate.setText(LocalDate.now().toString());
        txtDiagnosis.setText("Viêm họng cấp");
        txtTreatmentPlan.setText("Nghỉ ngơi, uống nhiều nước, tránh thức ăn cay nóng"); // Thêm phác đồ điều trị mẫu
        txtNotes.setText("Uống thuốc đều đặn, nghỉ ngơi nhiều");

        // Sample medicine data
        medicineTableModel.addRow(new Object[]{1, "Paracetamol", "Viên", "20", "2 viên/lần", "Ngày uống 3 lần sau ăn"});
        medicineTableModel.addRow(new Object[]{2, "Vitamin C", "Viên", "10", "1 viên/lần", "Ngày uống 1 lần sau ăn sáng"});
    }

    private void showAddMedicineDialog() {
        JDialog dialog = new JDialog(this, "Thêm thuốc", true);
        dialog.setSize(500, 550); // Tăng kích thước để đủ chỗ hiển thị
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel tìm kiếm thuốc
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Tìm", new Color(0, 123, 255));
        searchPanel.add(new JLabel("Tìm thuốc: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Bảng danh sách thuốc
        String[] medColumns = {"Mã thuốc", "Tên thuốc", "Đơn vị", "Mô tả"};
        DefaultTableModel medicationsModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable medicationsTable = new JTable(medicationsModel);
        medicationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(medicationsTable);
        tableScroll.setPreferredSize(new Dimension(450, 200));
        
        // Panel thông tin thuốc đã chọn
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thuốc"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        
        JTextField txtSelectedMedicine = createStyledTextField();
        txtSelectedMedicine.setEditable(false);
        JTextField txtUnit = createStyledTextField();
        JTextField txtQuantity = createStyledTextField();
        JTextField txtDosage = createStyledTextField();
        JTextField txtUsage = createStyledTextField();
        
        addFormField(infoPanel, gbc, "Tên thuốc:", txtSelectedMedicine, 0, 0);
        addFormField(infoPanel, gbc, "Đơn vị:", txtUnit, 1, 0);
        addFormField(infoPanel, gbc, "Số lượng:", txtQuantity, 2, 0);
        addFormField(infoPanel, gbc, "Liều dùng:", txtDosage, 3, 0);
        addFormField(infoPanel, gbc, "Cách dùng:", txtUsage, 4, 0);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = createStyledButton("Thêm vào đơn", new Color(40, 167, 69));
        JButton btnCancel = createStyledButton("Hủy", new Color(108, 117, 125));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAdd);
        
        // Tổ chức các panel
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Panel chứa tất cả
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
        
        // Load dữ liệu thuốc từ cơ sở dữ liệu
        loadMedicationsData(medicationsModel);
        
        // Xử lý sự kiện khi chọn thuốc từ bảng
        medicationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = medicationsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String medicineName = medicationsModel.getValueAt(selectedRow, 1).toString();
                    String unit = medicationsModel.getValueAt(selectedRow, 2).toString();
                    
                    txtSelectedMedicine.setText(medicineName);
                    txtUnit.setText(unit);
                    txtQuantity.setText("1"); // Giá trị mặc định
                    
                    // Tự động điền các giá trị mặc định
                    if (medicineName.equalsIgnoreCase("Amoxicillin")) {
                        txtDosage.setText("500mg x 3 lần/ngày");
                        txtUsage.setText("Uống sau ăn");
                    } else if (medicineName.equalsIgnoreCase("Paracetamol")) {
                        txtDosage.setText("500mg x 3 lần/ngày");
                        txtUsage.setText("Uống khi sốt > 38.5°C");
                    } else {
                        txtDosage.setText("Theo chỉ định"); // Giá trị mặc định
                        txtUsage.setText("Uống sau ăn");    // Giá trị mặc định
                    }
                }
            }
        });
        
        // Xử lý sự kiện tìm kiếm
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            filterMedicationsTable(medicationsModel, keyword);
        });
        
        // Xử lý thêm thuốc vào đơn
        btnAdd.addActionListener(e -> {
            if (txtSelectedMedicine.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn thuốc", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtQuantity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số lượng", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtQuantity.requestFocus();
                return;
            }
            
            if (txtDosage.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập liều dùng", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtDosage.requestFocus();
                return;
            }
            
            if (txtUsage.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập cách dùng", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtUsage.requestFocus();
                return;
            }
            
            int rowCount = medicineTableModel.getRowCount();
            medicineTableModel.addRow(new Object[]{
                rowCount + 1,
                txtSelectedMedicine.getText(),
                txtUnit.getText(),
                txtQuantity.getText(),
                txtDosage.getText(),
                txtUsage.getText()
            });
            dialog.dispose();
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    /**
     * Load dữ liệu thuốc từ cơ sở dữ liệu
     */
    private void loadMedicationsData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        List<Map<String, Object>> medications = controller.getAllMedications();
        if (medications != null) {
            for (Map<String, Object> med : medications) {
                model.addRow(new Object[]{
                    med.get("medicationId"),
                    med.get("medicineName"),
                    med.get("dosageForm") != null ? med.get("dosageForm") : "Viên",
                    med.get("description") != null ? med.get("description") : ""
                });
            }
        }
    }
    
    /**
     * Lọc bảng thuốc theo từ khóa
     */
    private void filterMedicationsTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        List<Map<String, Object>> medications = controller.searchMedications(keyword);
        if (medications != null) {
            for (Map<String, Object> med : medications) {
                model.addRow(new Object[]{
                    med.get("medicationId"),
                    med.get("medicineName"),
                    med.get("dosageForm") != null ? med.get("dosageForm") : "Viên",
                    med.get("description") != null ? med.get("description") : ""
                });
            }
        }
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

    /**
     * Tạo đơn thuốc mới từ form
     */
    private void savePrescription() {
        try {
            String diagnosis = txtDiagnosis.getText().trim();
            String treatmentPlan = txtTreatmentPlan.getText().trim(); // Lấy phác đồ điều trị
            String notes = txtNotes.getText().trim();
            
            if (diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập chẩn đoán", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtDiagnosis.requestFocus();
                return;
            }
            
            if (treatmentPlan.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập phác đồ điều trị", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtTreatmentPlan.requestFocus();
                return;
            }
            
            // Kiểm tra xem có thuốc nào không
            if (medicineTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Đơn thuốc cần có ít nhất một loại thuốc", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Thu thập dữ liệu thuốc từ bảng
            List<Map<String, Object>> medicineList = new ArrayList<>();
            for (int i = 0; i < medicineTableModel.getRowCount(); i++) {
                Map<String, Object> medicine = new HashMap<>();
                medicine.put("name", medicineTableModel.getValueAt(i, 1));
                medicine.put("unit", medicineTableModel.getValueAt(i, 2));
                medicine.put("quantity", medicineTableModel.getValueAt(i, 3));
                medicine.put("dosage", medicineTableModel.getValueAt(i, 4)); // Liều dùng
                medicine.put("instruction", medicineTableModel.getValueAt(i, 5)); // Cách dùng
                medicineList.add(medicine);
            }
            
            // Tạo đối tượng đơn thuốc
            Map<String, Object> prescriptionData = new HashMap<>();
            prescriptionData.put("prescriptionId", prescriptionId);
            prescriptionData.put("patientId", patientId);
            prescriptionData.put("diagnosis", diagnosis);
            prescriptionData.put("treatmentPlan", treatmentPlan); // Thêm phác đồ điều trị
            prescriptionData.put("date", txtDate.getText());
            prescriptionData.put("notes", notes);
            
            // Gọi controller để lưu đơn thuốc
            boolean success = controller.savePrescription(doctorId, prescriptionData, medicineList);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đơn thuốc đã được lưu thành công", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Đóng cửa sổ sau khi lưu thành công
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Có lỗi xảy ra khi lưu đơn thuốc", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + e.getMessage(), 
                "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            PrescriptionDetailsDoctorView frame = new PrescriptionDetailsDoctorView(
                "DOC001", // doctorId
                "P001",   // patientId 
                "Nguyễn Văn A", // patientName
                "PRE-00001", // prescriptionId
                true // isNew
            );
            frame.setVisible(true);
        });
    }
    
} 