import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame {
	private User user;
	private AdminWindow adminWindow;
	
	public MainWindow() {
		super("Parking Database Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		adminWindow = new AdminWindow();
		
		promptLogin();
	}
	
	private void promptLogin() {
		clearSubMenus();
		clearUI();
		
		// Prompt user for login
		LoginPrompt prompt = new LoginPrompt(this);
		prompt.setVisible(true);
		
		// Exit early if the user is nonexistent
		user = prompt.getUser();
		if (user == null) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			return;
		}

		setupSubMenus();
		setupUI();
	}
	
	private void setupSubMenus() {
		adminWindow = new AdminWindow();
	}
	
	private void clearSubMenus() {
		if (adminWindow != null) {
			adminWindow.dispose();
			adminWindow = null;
		}
	}
	
	private void setupUI() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
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
		reserveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: implement reservation menu
				System.out.println("Reserve button clicked!");
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(reserveButton, constraints);
		
		JButton membershipButton = new JButton("Buy a membership");
		membershipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: implement membership menu
				System.out.println("Membership button clicked!");
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(membershipButton, constraints);
		
		JButton adminButton = new JButton("Admin menu");
		adminButton.setEnabled(user.getIsAdmin());
		adminButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				adminWindow.setVisible(true);
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(adminButton, constraints);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		setSize(600, 400);
		setResizable(false);
		
		getContentPane().revalidate();
		getContentPane().repaint();
	}
	
	private void clearUI() {
		getContentPane().removeAll();
		getContentPane().revalidate();
		getContentPane().repaint();
	}
}
