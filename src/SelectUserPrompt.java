import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class SelectUserPrompt extends JDialog {
	private int userID = 0;
	
	public SelectUserPrompt(Frame parent) {
		super(parent, "Browse Users", true);
		setupUI();
	}
	
	private void setupUI() {
		JLabel topLabel = new JLabel("Choose a user:");
		getContentPane().add(topLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		try {
			ResultSet userList = User.getUserList();
			
			while (userList.next()) {
				int _userID = userList.getInt("user_id");
				JButton userButton = new JButton(userList.getString("name"));
				userButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						setUserID(_userID);
						dispose();
					}
				});
				panel.add(userButton);
			}
		} catch (SQLException exception) {
			topLabel.setText("Unexpected error; check System.err");
			exception.printStackTrace(System.err);
		}
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(200, 400));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	
	private void setUserID(int _userID) {
		userID = _userID;
	}
	
	public int getUserID() {
		return userID;
	}
}
