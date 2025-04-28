package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class PrescriptionDetailsPatientView extends JDialog {
    private JPanel contentPanel;
    private JLabel lblPrescriptionId, lblPatientId, lblPatientName, lblDiagnosis, lblDate;
    private JTextArea txtNotes;
    private DefaultTableModel medicineTableModel;
    private JTable medicineTable;
    private JButton btnPrint;

    public PrescriptionDetailsPatientView(JFrame parent, String prescriptionId) {
        super(parent, "Chi tiết đơn thuốc", true);
        setSize(800, 700);
        setLocationRelativeTo(parent);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel - Prescription Information
        JPanel infoPanel = createInfoPanel();
        
        // Center Panel - Medicine Table
        JPanel medicinePanel = createMedicinePanel();
        
        // Bottom Panel - Notes and Buttons
        JPanel bottomPanel = createBottomPanel();

        contentPanel.add(infoPanel, BorderLayout.NORTH);
        contentPanel.add(medicinePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel);

        // Load prescription data
        loadPrescriptionData(prescriptionId);
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
        lblPrescriptionId = createStyledLabel("");
        lblPatientId = createStyledLabel("");
        addFormField(panel, gbc, "Mã đơn thuốc:", lblPrescriptionId, 0, 0);
        addFormField(panel, gbc, "Mã bệnh nhân:", lblPatientId, 0, 2);

        // Row 2
        lblPatientName = createStyledLabel("");
        lblDate = createStyledLabel("");
        addFormField(panel, gbc, "Tên bệnh nhân:", lblPatientName, 1, 0);
        addFormField(panel, gbc, "Ngày kê đơn:", lblDate, 1, 2);

        // Row 3
        lblDiagnosis = createStyledLabel("");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        panel.add(new JLabel("Chẩn đoán:"), gbc);
        gbc.gridy = 3;
        panel.add(lblDiagnosis, gbc);

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
                return false; // Make table read-only
            }
        };
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setRowHeight(30);
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Set preferred column widths
        medicineTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        medicineTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên thuốc
        medicineTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Đơn vị
        medicineTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        medicineTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Liều dùng
        medicineTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Cách dùng

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        txtNotes.setEditable(false);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesPanel.add(notesScroll);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPrint = createStyledButton("In đơn thuốc", new Color(0, 123, 255));
        buttonPanel.add(btnPrint);

        panel.add(notesPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener
        btnPrint.addActionListener(e -> printPrescription());

        return panel;
    }

    private void printPrescription() {
        // TODO: Implement print functionality
        JOptionPane.showMessageDialog(this,
            "Đang chuẩn bị in đơn thuốc...",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadPrescriptionData(String prescriptionId) {
        // TODO: Load prescription data from database
        // This is sample data
        lblPrescriptionId.setText(prescriptionId);
        lblPatientId.setText("BN001");
        lblPatientName.setText("Nguyễn Văn A");
        lblDate.setText(LocalDate.now().toString());
        lblDiagnosis.setText("Viêm họng cấp");
        txtNotes.setText("Uống thuốc đều đặn, nghỉ ngơi nhiều");

        // Sample medicine data
        medicineTableModel.addRow(new Object[]{1, "Paracetamol", "Viên", "20", "2 viên/lần", "Ngày uống 3 lần sau ăn"});
        medicineTableModel.addRow(new Object[]{2, "Vitamin C", "Viên", "10", "1 viên/lần", "Ngày uống 1 lần sau ăn sáng"});
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
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
            JFrame frame = new JFrame();
            PrescriptionDetailsPatientView dialog = new PrescriptionDetailsPatientView(frame, "DT001");
            dialog.setVisible(true);
        });
    }
} 