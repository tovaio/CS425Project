import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

public class ModifyUserPrompt extends JDialog {
	private int userID;
	
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JCheckBox isAdminField;
	private JFormattedTextField lotIDField;
	
	public ModifyUserPrompt(Frame parent, int _userID, boolean fromAdminPerspective) {
		super(parent, "Admin Menu", true);
		setUserID(_userID);
		setupUI(fromAdminPerspective);
	}
	
	private void setupUI(boolean fromAdminPerspective) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel userIDLabel = new JLabel("User ID: " + Integer.toString(getUserID()));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		panel.add(userIDLabel, constraints);
		
		JLabel usernameLabel = new JLabel("Username:");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(usernameLabel, constraints);
		
		usernameField = new JTextField(30);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		panel.add(usernameField, constraints);
		
		JLabel passwordLabel = new JLabel("Password:");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(passwordLabel, constraints);
		
		passwordField = new JPasswordField(20);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		panel.add(passwordField, constraints);
		
		if (fromAdminPerspective) {
			isAdminField = new JCheckBox("Admin?");
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.gridwidth = 3;
			panel.add(isAdminField, constraints);
			
			JLabel lotIDLabel = new JLabel("Staffed lot ID (0 if none):");
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 1;
			panel.add(lotIDLabel, constraints);
			
			NumberFormat format = NumberFormat.getInstance();
			NumberFormatter formatter = new NumberFormatter(format);
			formatter.setValueClass(Integer.class);
			formatter.setMinimum(0);
			formatter.setMaximum(Integer.MAX_VALUE);
			formatter.setAllowsInvalid(false);
			formatter.setCommitsOnValidEdit(true);
			
			lotIDField = new JFormattedTextField(formatter);
			constraints.gridx = 1;
			constraints.gridy = 4;
			constraints.gridwidth = 2;
			panel.add(lotIDField, constraints);
		}
		
		JLabel errorLabel = new JLabel(" ");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 3;
		panel.add(errorLabel, constraints);
		
		try {
			ResultSet userResult = User.getUser(getUserID());
			userResult.next();
			setUsername(userResult.getString("name"));
			setPassword(userResult.getString("password"));
			if (fromAdminPerspective) {
				setIsAdmin(userResult.getBoolean("is_admin"));
				setLotID(userResult.getInt("lot_id"));
			}
		} catch (Exception exception) {
			errorLabel.setText("Unexpected error; check System.err");
			exception.printStackTrace(System.err);
		}
		
		JButton updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (fromAdminPerspective) {
						User.adminSetUser(getUserID(), getUsername(), getPassword(), getIsAdmin(), getLotID());
					} else {
						User.setUser(getUserID(), getUsername(), getPassword());
					}
					dispose();
				} catch (ParkingException exception) {
					errorLabel.setText(exception.getMessage());
				} catch (Exception exception) {
					errorLabel.setText("Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				} 
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		panel.add(updateButton, constraints);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		constraints.gridx = 2;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		panel.add(cancelButton, constraints);
		
		getContentPane().add(panel);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	
	private int getUserID() {
		return userID;
	}
	
	private String getUsername() {
		return usernameField.getText().trim();
	}
	
	private String getPassword() {
		return new String(passwordField.getPassword());
	}
	
	private boolean getIsAdmin() {
		return isAdminField.isSelected();
	}
	
	private int getLotID() {
		return (int) lotIDField.getValue();
	}
	
	private void setUserID(int _userID) {
		userID = _userID;
	}
	
	private void setUsername(String username) {
		usernameField.setText(username);
	}

	private void setPassword(String password) {
		passwordField.setText(password);
	}
	
	private void setIsAdmin(boolean isAdmin) {
		isAdminField.setSelected(isAdmin);
	}
	
	private void setLotID(int lotID) {
		lotIDField.setValue(lotID);
	}
}
