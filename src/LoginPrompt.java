import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginPrompt extends JDialog {
	private JLabel usernameLabel;
	private JTextField usernameField;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JLabel errorLabel;
	private JButton loginButton;
	private JButton registerButton;
	private JButton cancelButton;
	
	private User user;
	
	public LoginPrompt(Frame parent) {
		super(parent, "Login", true);
		setupUI();
	}
	
	private void setupUI() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		usernameLabel = new JLabel("Username:");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		panel.add(usernameLabel, constraints);
		
		usernameField = new JTextField(30);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		panel.add(usernameField, constraints);
		
		passwordLabel = new JLabel("Password:");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(passwordLabel, constraints);
		
		passwordField = new JPasswordField(20);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		panel.add(passwordField, constraints);
		
		errorLabel = new JLabel(" ");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		panel.add(errorLabel, constraints);
		
		loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					user = new User(getUsername(), getPassword());
					dispose();
				} catch (LoginException exception) {
					errorLabel.setText(exception.getMessage());
				} catch (Exception exception) {
					errorLabel.setText("Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				} 
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(loginButton, constraints);
		
		registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					user = User.registerUser(getUsername(), getPassword());
					dispose();
				} catch (LoginException exception) {
					errorLabel.setText(exception.getMessage());
				} catch (Exception exception) {
					errorLabel.setText("Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				} 
			}
		});
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(registerButton, constraints);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(cancelButton, constraints);
		
		getContentPane().add(panel);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	
	private String getUsername() {
		return usernameField.getText().trim();
	}
	
	private String getPassword() {
		return new String(passwordField.getPassword());
	}
	
	public User getUser() {
		return user;
	}
}
