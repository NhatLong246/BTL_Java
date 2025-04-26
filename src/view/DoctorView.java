package view;

import controller.DoctorController;
import model.entity.Patient;
import model.enums.Gender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DoctorView extends JFrame {
    private JPanel contentPanel;
    private JButton btnHome, btnAdd, btnView, btnBook, btnDel;
    private JButton currentSelectedButton;
    private DoctorController controller;
    private JTextField txtName, txtBirthDate, txtAddress, txtPhone, txtDisease, txtPatientId, txtDate;
    private JComboBox<Gender> cbGender;
    private DefaultTableModel tableModel;
    private JTable table;

    public DoctorView(String doctorId) {
        this.controller = new DoctorController(this, doctorId);
        setTitle("Doctor Dashboard");
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

        JLabel menuTitle = new JLabel("Doctor Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 50));
        menuTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 50, 10);
        gbc.weighty = 0.1;
        leftPanel.add(menuTitle, gbc);

        btnHome = createButton("Home");
        btnAdd = createButton("Add Patient");
        btnView = createButton("View Patients");
        btnBook = createButton("Book Appointment");
        btnDel = createButton("Delete Patient");

        setSelectedButton(btnHome);

        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.weighty = 0.0;

        gbc.gridy = 1;
        leftPanel.add(btnHome, gbc);
        gbc.gridy = 2;
        leftPanel.add(btnAdd, gbc);
        gbc.gridy = 3;
        leftPanel.add(btnView, gbc);
        gbc.gridy = 4;
        leftPanel.add(btnBook, gbc);
        gbc.gridy = 5;
        leftPanel.add(btnDel, gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        controller.showHome();

        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnHome.addActionListener(e -> controller.showHome());
        btnAdd.addActionListener(e -> controller.showAddPatientForm());
        btnView.addActionListener(e -> controller.showPatientList());
        btnBook.addActionListener(e -> controller.showBookAppointment());
        btnDel.addActionListener(e -> controller.showDeletePatientForm());
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
            currentSelectedButton.revalidate();
        }
        currentSelectedButton = selectedButton;
        currentSelectedButton.setBackground(Color.GRAY);
        currentSelectedButton.setPreferredSize(new Dimension(280, 70));
        currentSelectedButton.revalidate();
    }

    public void showHome() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        JLabel lblWelcome = new JLabel("Welcome to Doctor Dashboard", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(44, 62, 80));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showAddPatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Enter Patient Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        txtName = new JTextField(40);
        txtBirthDate = new JTextField(40);
        txtAddress = new JTextField(40);
        txtPhone = new JTextField(40);
        txtDisease = new JTextField(40);
        cbGender = new JComboBox<>(Gender.values());

        txtName.setPreferredSize(new Dimension(400, 60));
        txtBirthDate.setPreferredSize(new Dimension(400, 60));
        txtAddress.setPreferredSize(new Dimension(400, 60));
        txtPhone.setPreferredSize(new Dimension(400, 60));
        txtDisease.setPreferredSize(new Dimension(400, 60));
        cbGender.setPreferredSize(new Dimension(400, 60));

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        txtName.setFont(fieldFont);
        txtBirthDate.setFont(fieldFont);
        txtAddress.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtDisease.setFont(fieldFont);
        cbGender.setFont(fieldFont);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(labelFont);
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblName, gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblBirthDate = new JLabel("Birth Date (YYYY-MM-DD):");
        lblBirthDate.setFont(labelFont);
        lblBirthDate.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblBirthDate, gbc);
        gbc.gridx = 1;
        formPanel.add(txtBirthDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(labelFont);
        lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblAddress, gbc);
        gbc.gridx = 1;
        formPanel.add(txtAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        lblGender.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblGender, gbc);
        gbc.gridx = 1;
        formPanel.add(cbGender, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(labelFont);
        lblPhone.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblPhone, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblMedicalHistory = new JLabel("Disease:");
        lblMedicalHistory.setFont(labelFont);
        lblMedicalHistory.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(lblMedicalHistory, gbc);
        gbc.gridx = 1;
        formPanel.add(txtDisease, gbc);

        JButton btnSave = createButton("Save");
        btnSave.setPreferredSize(new Dimension(300, 80));
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnSave, gbc);

        btnSave.addActionListener(e -> controller.addPatient(txtName.getText(), txtBirthDate.getText(), 
                                                             txtAddress.getText(), txtPhone.getText(), 
                                                             (Gender) cbGender.getSelectedItem(), txtDisease.getText()));

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showPatientList(java.util.List<Patient> patients) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Patient ID", "Full Name", "Birth Date", "Address", "Gender", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                    p.getPatientID(),
                    p.getFullName(),
                    p.getDateOfBirth().toString(),
                    p.getAddress(),
                    p.getGender().toString(),
                    p.getPhoneNumber()
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showBookAppointment() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Book an Appointment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        txtPatientId = new JTextField(40);
        txtDate = new JTextField(40);
        txtPatientId.setPreferredSize(new Dimension(400, 60));
        txtDate.setPreferredSize(new Dimension(400, 60));

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        txtPatientId.setFont(fieldFont);
        txtDate.setFont(fieldFont);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblPatientId = new JLabel("Patient ID:");
        lblPatientId.setFont(labelFont);
        lblPatientId.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblPatientId, gbc);
        gbc.gridx = 1;
        contentPanel.add(txtPatientId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblDate = new JLabel("Appointment Date (YYYY-MM-DD):");
        lblDate.setFont(labelFont);
        lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblDate, gbc);
        gbc.gridx = 1;
        contentPanel.add(txtDate, gbc);

        JButton btnBook = createButton("Book");
        btnBook.setPreferredSize(new Dimension(200, 60));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnBook, gbc);

        btnBook.addActionListener(e -> controller.bookAppointment(txtPatientId.getText(), txtDate.getText()));

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showDeletePatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Delete Patient", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        txtPatientId = new JTextField(40);
        txtPatientId.setPreferredSize(new Dimension(400, 60));
        txtPatientId.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblPatientId = new JLabel("Patient ID:");
        lblPatientId.setFont(new Font("Arial", Font.PLAIN, 18));
        lblPatientId.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblPatientId, gbc);
        gbc.gridx = 1;
        contentPanel.add(txtPatientId, gbc);

        JButton btnDelete = createButton("Delete");
        btnDelete.setPreferredSize(new Dimension(200, 60));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnDelete, gbc);

        btnDelete.addActionListener(e -> controller.deletePatient(txtPatientId.getText()));

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public JButton getBtnHome() { return btnHome; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnView() { return btnView; }
    public JButton getBtnBook() { return btnBook; }
    public JButton getBtnDel() { return btnDel; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new DoctorView("DOC001").setVisible(true));
    }
}