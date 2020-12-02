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
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel welcomeLabel = new JLabel("Welcome to the admin menu!");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		panel.add(welcomeLabel, constraints);
		
		AdminWindow _this = this;
		
		JButton modifyUserButton = new JButton("Modify user info");
		modifyUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				SelectUserPrompt selectUserPrompt = new SelectUserPrompt(_this);
				selectUserPrompt.setVisible(true);
				
				if (selectUserPrompt.getUserID() > 0) {
					ModifyUserPrompt modifyUserPrompt = new ModifyUserPrompt(_this, selectUserPrompt.getUserID(), true);
					modifyUserPrompt.setVisible(true);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(modifyUserButton, constraints);
		
		JButton reportButton = new JButton("Generate reports");
		reportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: implement report menu
				System.out.println("Report button clicked!");
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(reportButton, constraints);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		setSize(600, 400);
		setResizable(false);
		setVisible(false);
	}
}
