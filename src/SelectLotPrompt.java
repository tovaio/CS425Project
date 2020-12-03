import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class SelectLotPrompt extends JDialog {
	private int lotID = 0;
	private int reservationFee;
	private int membershipFee;
	
	public SelectLotPrompt(Frame parent) {
		super(parent, "Browse Parking Lots", true);
		setupUI();
	}
	
	private void setupUI() {
		JLabel topLabel = new JLabel("Choose a parking lot:");
		getContentPane().add(topLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		try {
			ResultSet lotList = User.getLotList();
			
			while (lotList.next()) {
				int _lotID = lotList.getInt("lot_id");
				int _reservationFee = lotList.getInt("reservation_fee");
				int _membershipFee = lotList.getInt("membership_fee");
				JButton lotButton = new JButton(
					"Parking Lot ID " + _lotID + ": $" + lotList.getInt("reservation_fee") + "/reservation, $"
					+ lotList.getInt("membership_fee") + "/membership"
				);
				lotButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						setLotID(_lotID);
						reservationFee = _reservationFee;
						membershipFee = _membershipFee;
						dispose();
					}
				});
				panel.add(lotButton);
			}
		} catch (SQLException exception) {
			topLabel.setText("Unexpected error; check System.err");
			exception.printStackTrace(System.err);
		}
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(500, 400));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	
	private void setLotID(int _lotID) {
		lotID = _lotID;
	}
	
	public int getLotID() {
		return lotID;
	}
	
	public int getReservationFee() {
		return reservationFee;
	}
	
	public int getMembershipFee() {
		return membershipFee;
	}
}
