import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class SelectSpotPrompt extends JDialog {
	private int spotID = 0;
	
	public SelectSpotPrompt(Frame parent, ResultSet spotList) {
		super(parent, "Browse Parking Spots", true);
		setupUI(spotList);
	}
	
	private void setupUI(ResultSet spotList) {
		JLabel topLabel = new JLabel("Choose a parking spot:");
		getContentPane().add(topLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		try {
			while (spotList.next()) {
				int _spotID = spotList.getInt("spot_id");
				JButton spotButton = new JButton("Parking Spot " + spotList.getInt("spot_number"));
				spotButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						setSpotID(_spotID);
						dispose();
					}
				});
				panel.add(spotButton);
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
	
	private void setSpotID(int _lotID) {
		spotID = _lotID;
	}
	
	public int getSpotID() {
		return spotID;
	}
}
