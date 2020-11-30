import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame {
	private JLabel loginStatus;
	private User user;
	
	public MainWindow() {
		super("Parking Database Application");
		setupUI();
		
		// Prompt user for login
		LoginPrompt prompt = new LoginPrompt(this);
		prompt.setVisible(true);
		
		// Exit early if the user is nonexistent
		user = prompt.getUser();
		if (user == null) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			return;
		}
		
		loginStatus.setText("Logged in as user \"" + user.getUsername() + "\" with user ID " + user.getUserID());
	}
	
	private void setupUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLayout(new FlowLayout());
		
		loginStatus = new JLabel("Not logged in");
		getContentPane().add(loginStatus);
	}
}
