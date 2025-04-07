package UI;

import javax.swing.*;
import database.UserDAO;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Calendar;

import org.jdatepicker.impl.*;

public class PatientInfoUI extends JFrame {
	private JTextField nameText;
	private JComboBox<String> genderCombo;
	private JTextField addressText;
	private JDatePickerImpl datePicker;
	private int userId;

	public PatientInfoUI(int userId) {
		this.userId = userId;

		setTitle("Patient Information");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);

		// Load background image
		String imagePath = "src/resource/img/file_background.png";
		if (!new File(imagePath).exists()) {
			System.out.println("Image not found: " + imagePath);
		}

		ImageIcon originalIcon = new ImageIcon(imagePath);
		Image scaledImage = originalIcon.getImage().getScaledInstance(Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height, Image.SCALE_SMOOTH);
		ImageIcon bgImage = new ImageIcon(scaledImage);

		JLabel background = new JLabel(bgImage);
		background.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height);

		int panelWidth = 900;
		int panelHeight = 700;

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(panelWidth, panelHeight);
		panel.setBackground(new Color(0, 0, 0, 150));
		panel.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2, panelWidth, panelHeight);

		JLabel titleLabel = new JLabel("PATIENT INFORMATION", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBounds(0, 20, panelWidth, 50);

		// Name
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(250, 100, 200, 40);
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(new Font("Arial", Font.BOLD, 20));

		nameText = new JTextField();
		nameText.setBounds(250, 140, 400, 50);
		nameText.setFont(new Font("Arial", Font.PLAIN, 20));

		// Birthdate
		JLabel birthdateLabel = new JLabel("Birthdate:");
		birthdateLabel.setBounds(250, 200, 200, 40);
		birthdateLabel.setForeground(Color.WHITE);
		birthdateLabel.setFont(new Font("Arial", Font.BOLD, 20));

		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");

		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		datePicker.setBounds(250, 240, 400, 50); // vẫn giữ dòng này

		// Thêm đoạn này để chỉnh phần hiển thị ngày
		JFormattedTextField textField = datePicker.getJFormattedTextField();
		textField.setFont(new Font("Arial", Font.PLAIN, 20));
		textField.setPreferredSize(new Dimension(400, 50)); // Đặt lại kích thước nội dung
		textField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding đẹp hơn

		datePicker.getJFormattedTextField().setFont(new Font("Arial", Font.PLAIN, 20));

		// Gender
		JLabel genderLabel = new JLabel("Gender:");
		genderLabel.setBounds(250, 300, 200, 40);
		genderLabel.setForeground(Color.WHITE);
		genderLabel.setFont(new Font("Arial", Font.BOLD, 20));

		String[] genders = { "MALE", "FEMALE", "OTHER" };
		genderCombo = new JComboBox<>(genders);
		genderCombo.setBounds(250, 340, 400, 50);
		genderCombo.setFont(new Font("Arial", Font.PLAIN, 20));

		// Address
		JLabel addressLabel = new JLabel("Address:");
		addressLabel.setBounds(250, 400, 200, 40);
		addressLabel.setForeground(Color.WHITE);
		addressLabel.setFont(new Font("Arial", Font.BOLD, 20));

		addressText = new JTextField();
		addressText.setBounds(250, 440, 400, 50);
		addressText.setFont(new Font("Arial", Font.PLAIN, 20));

		// Submit Button
		JButton submitButton = new JButton("SUBMIT");
		int buttonWidth = 200;
		int buttonHeight = 60;
		submitButton.setBounds((panelWidth - buttonWidth) / 2, 520, buttonWidth, buttonHeight);
		submitButton.setFont(new Font("Arial", Font.BOLD, 22));
		submitButton.setBackground(Color.WHITE);
		submitButton.setForeground(Color.BLACK);

		submitButton.addActionListener(e -> {
			String name = nameText.getText();
			String gender = (String) genderCombo.getSelectedItem();
			String address = addressText.getText();

			Date selectedDate = (Date) datePicker.getModel().getValue();
			if (name.isEmpty() || selectedDate == null || address.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Check future date
			if (selectedDate.after(new Date())) {
				JOptionPane.showMessageDialog(null, "Birthdate cannot be in the future!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			String birthdate = outputFormat.format(selectedDate);

			String result = UserDAO.addPatient(userId, name, birthdate, gender, address);
			if ("Success".equals(result)) {
				JOptionPane.showMessageDialog(null, "Registration completed successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				dispose();
				new LoginUI().setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "Failed to save patient information! " + result, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		panel.add(titleLabel);
		panel.add(nameLabel);
		panel.add(nameText);
		panel.add(birthdateLabel);
		panel.add(datePicker);
		panel.add(genderLabel);
		panel.add(genderCombo);
		panel.add(addressLabel);
		panel.add(addressText);
		panel.add(submitButton);

		setContentPane(background);
		add(panel);
		setVisible(true);
	}
}
