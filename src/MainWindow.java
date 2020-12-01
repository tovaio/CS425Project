import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame {
	private User user;
	
	public MainWindow() {
		super("Parking Database Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		promptLogin();
	}
	
	private void promptLogin() {
		// Prompt user for login
		LoginPrompt prompt = new LoginPrompt(this);
		prompt.setVisible(true);
		
		// Exit early if the user is nonexistent
		user = prompt.getUser();
		if (user == null) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			return;
		}
		
		setupUI();
	}
	
	private void setupUI() {
		getContentPane().removeAll();
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		MainWindow _this = this;
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem logOutItem = new JMenuItem("Log Out");
		logOutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				promptLogin();
			}
		});
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispatchEvent(new WindowEvent(_this, WindowEvent.WINDOW_CLOSING));
			}
		});
		fileMenu.add(logOutItem);
		fileMenu.add(exitItem);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		panel.add(welcomeLabel, constraints);
		
		JButton reserveButton = new JButton("Reserve a parking spot");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(reserveButton, constraints);
		
		JButton membershipButton = new JButton("Buy a membership");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(membershipButton, constraints);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setResizable(false);
		
		getContentPane().repaint();
	}
}
