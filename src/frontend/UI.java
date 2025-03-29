package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UI {
	private JFrame frame;
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JTable table;
	private DefaultTableModel model;
	private JTextField txtSearchID, txtName, txtBirthdate, txtGender, txtPhone, txtAddress, txtMedicalHistory;

	// Thông tin kết nối MySQL
	private static final String URL = "jdbc:mysql://localhost:3306/hospitaldb?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "2005";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				UI window = new UI();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Quản lý bệnh nhân");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		// ====================== NAVIGATION PANEL (Menu điều hướng)
		// ======================
		JPanel navPanel = new JPanel(new GridLayout(5, 1, 10, 10)); // 5 hàng, 1 cột
		navPanel.setPreferredSize(new Dimension(300, frame.getHeight())); // Chiếm 30% chiều rộng màn hình
		navPanel.setBackground(new Color(200, 200, 200));

		JButton btnHome = new JButton("🏠 TRANG CHỦ");
		btnHome.setFont(new Font("Arial", Font.BOLD, 18));
		btnHome.setBackground(new Color(100, 100, 255));
		btnHome.setForeground(Color.WHITE);
		btnHome.setPreferredSize(new Dimension(300, 80));

		JButton btnShowList = new JButton("📜 Hiện danh sách");
		JButton btnSearch = new JButton("🔍 Tìm kiếm");
		JButton btnAddPatient = new JButton("➕ Thêm bệnh nhân");

		navPanel.add(btnHome);
		navPanel.add(btnShowList);
		navPanel.add(btnSearch);
		navPanel.add(btnAddPatient);
		frame.getContentPane().add(navPanel, BorderLayout.WEST);

		// ====================== MAIN PANEL (Chứa các màn hình) ======================
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

		// ====================== TRANG CHỦ ======================
		JPanel homePanel = new JPanel();
		homePanel.add(new JLabel("<html><h1>Chào mừng đến với Hệ thống Quản lý Bệnh nhân</h1></html>"));
		mainPanel.add(homePanel, "Home");

		// ====================== BẢNG DANH SÁCH BỆNH NHÂN ======================
		JPanel listPanel = new JPanel(new BorderLayout());
		model = new DefaultTableModel();
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		listPanel.add(scrollPane, BorderLayout.CENTER);
		model.addColumn("ID");
		model.addColumn("Tên");
		model.addColumn("Ngày sinh");
		model.addColumn("Giới tính");
		model.addColumn("SĐT");
		model.addColumn("Địa chỉ");
		model.addColumn("Tiền sử bệnh");
		mainPanel.add(listPanel, "List");

		// ====================== TÌM KIẾM THEO ID ======================
		JPanel searchPanel = new JPanel(new FlowLayout());
		txtSearchID = new JTextField(10);
		JButton btnSearchAction = new JButton("Tìm kiếm");
		searchPanel.add(new JLabel("Nhập ID:"));
		searchPanel.add(txtSearchID);
		searchPanel.add(btnSearchAction);
		mainPanel.add(searchPanel, "Search");

		// ====================== FORM THÊM BỆNH NHÂN ======================
		JPanel addPanel = new JPanel(new GridLayout(7, 2, 10, 10));
		txtName = new JTextField(10);
		txtBirthdate = new JTextField(8);
		txtGender = new JTextField(5);
		txtPhone = new JTextField(10);
		txtAddress = new JTextField(15);
		txtMedicalHistory = new JTextField(15);
		JButton btnAddAction = new JButton("Thêm bệnh nhân");

		addPanel.add(new JLabel("Tên:"));
		addPanel.add(txtName);
		addPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"));
		addPanel.add(txtBirthdate);
		addPanel.add(new JLabel("Giới tính:"));
		addPanel.add(txtGender);
		addPanel.add(new JLabel("SĐT:"));
		addPanel.add(txtPhone);
		addPanel.add(new JLabel("Địa chỉ:"));
		addPanel.add(txtAddress);
		addPanel.add(new JLabel("Tiền sử bệnh:"));
		addPanel.add(txtMedicalHistory);
		addPanel.add(new JLabel(""));
		addPanel.add(btnAddAction);
		mainPanel.add(addPanel, "Add");

		// ====================== EVENT HANDLERS ======================
		btnHome.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
		btnShowList.addActionListener(e -> {
			cardLayout.show(mainPanel, "List");
			loadPatients();
		});
		btnSearch.addActionListener(e -> cardLayout.show(mainPanel, "Search"));
		btnAddPatient.addActionListener(e -> cardLayout.show(mainPanel, "Add"));

		btnSearchAction.addActionListener(e -> searchPatientByID());
		btnAddAction.addActionListener(e -> addNewPatient());

		// ====================== HIỂN THỊ TRANG CHỦ BAN ĐẦU ======================
		cardLayout.show(mainPanel, "Home");
	}

	/**
	 * Kết nối CSDL và tải danh sách bệnh nhân
	 */
	private void loadPatients() {
		model.setRowCount(0); // Xóa dữ liệu cũ
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "SELECT * FROM patients";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				model.addRow(new Object[] { rs.getInt("id"), rs.getString("name"), rs.getDate("birthdate"),
						rs.getString("gender"), rs.getString("phone"), rs.getString("address"),
						rs.getString("medical_history") });
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Lỗi kết nối CSDL: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Tìm kiếm bệnh nhân theo ID
	 */
	private void searchPatientByID() {
	    String idText = txtSearchID.getText().trim();
	    
	    if (idText.isEmpty()) {
	        JOptionPane.showMessageDialog(frame, "Vui lòng nhập ID!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    int patientID;
	    try {
	        patientID = Integer.parseInt(idText);
	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(frame, "ID phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
	        System.out.println("Kết nối CSDL thành công!");

	        String sql = "SELECT * FROM patients WHERE id = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, patientID);
	        ResultSet rs = stmt.executeQuery();

	        model.setRowCount(0);
	        if (rs.next()) {
	            System.out.println("Tìm thấy bệnh nhân: " + rs.getString("name"));
	            model.addRow(new Object[]{
	                rs.getInt("id"), 
	                rs.getString("name"), 
	                rs.getDate("birthdate"),
	                rs.getString("gender"), 
	                rs.getString("phone"), 
	                rs.getString("address"),
	                rs.getString("medical_history")
	            });
	        } else {
	            JOptionPane.showMessageDialog(frame, "Không tìm thấy!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	        }
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(frame, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	    }
	}


	private void addNewPatient() {
		String name = txtName.getText();
		String birthdate = txtBirthdate.getText();
		String gender = txtGender.getText();
		String phone = txtPhone.getText();
		String address = txtAddress.getText();
		String medicalHistory = txtMedicalHistory.getText();

		// Kiểm tra thông tin có bị bỏ trống không
		if (name.isEmpty() || birthdate.isEmpty() || gender.isEmpty() || phone.isEmpty() || address.isEmpty()
				|| medicalHistory.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Kết nối database và thực hiện INSERT
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String sql = "INSERT INTO patients (name, birthdate, gender, phone, address, medical_history) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, birthdate);
			stmt.setString(3, gender);
			stmt.setString(4, phone);
			stmt.setString(5, address);
			stmt.setString(6, medicalHistory);

			int rowsInserted = stmt.executeUpdate();
			if (rowsInserted > 0) {
				JOptionPane.showMessageDialog(frame, "Thêm bệnh nhân thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
				clearInputFields(); // Xóa dữ liệu trên form sau khi thêm thành công
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Lỗi khi thêm bệnh nhân: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearInputFields() {
		txtName.setText("");
		txtBirthdate.setText("");
		txtGender.setText("");
		txtPhone.setText("");
		txtAddress.setText("");
		txtMedicalHistory.setText("");
	}

}
