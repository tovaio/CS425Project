import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AdminWindow extends JFrame {
	public AdminWindow() {
		super("Admin Menu");
		setupUI();
	}
	
	private void setupUI() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLabel welcomeLabel = new JLabel("Welcome to the admin menu!");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		panel.add(welcomeLabel, constraints);
		
		JButton modifyUserButton = new JButton("Modify user info");
		modifyUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: implement modify user menu
				System.out.println("Modify user button clicked!");
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(modifyUserButton, constraints);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		setSize(600, 400);
		setResizable(false);
		setVisible(false);
	}
}
