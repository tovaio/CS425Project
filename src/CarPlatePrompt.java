import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

import com.github.lgooddatepicker.components.*;

public class CarPlatePrompt extends JDialog {
	private int plateID = 0;
	
	public CarPlatePrompt(Frame parent) {
		super(parent, "File Car Plate Number", true);
		setupUI();
	}
	
	private void setupUI() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel plateLabel = new JLabel("Type car plate number:");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		panel.add(plateLabel, constraints);
		
		JTextField plateField = new JTextField(10);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		panel.add(plateField, constraints);
		
		JCheckBox tempField = new JCheckBox("Temporary? (1 day)");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		panel.add(tempField, constraints);
		
		CarPlatePrompt _this = this;
		
		JButton submitButton = new JButton("File car plate");
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String plateNumber = plateField.getText().trim();
					if (plateNumber.length() == 0) {
						return;
					}
					
					boolean isTemp = tempField.isSelected();
					ResultSet plateResults = User.addPlate(plateNumber, isTemp);
					
					if (plateResults.next()) {
						plateID = plateResults.getInt("plate_id");
					}
					
					dispose();
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		panel.add(submitButton, constraints);
		
		add(panel, BorderLayout.CENTER);
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	
	public int getPlateID() {
		return plateID;
	}
}
