import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;
import com.github.lgooddatepicker.components.*;

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
		
		JButton availableSlotButton = new JButton("Report 1: Available Slots");
		availableSlotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					TimeSlotPrompt timeSlotPrompt = new TimeSlotPrompt(_this, true, true);
					timeSlotPrompt.setVisible(true);
					
					Date date = timeSlotPrompt.getDate();
					Time startTime = timeSlotPrompt.getStartTime();
					Time endTime = timeSlotPrompt.getEndTime();
					
					if (date == null || startTime == null || endTime == null) {
						return;
					}
					
					ResultSet availableSpots = User.getAvailableSpots(startTime, endTime, date);
					
					JFrame resultFrame = new JFrame("Report 1 Results");
					
					JPanel resultPanel = new JPanel();
					resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
					
					while (availableSpots.next()) {
						JLabel spotLabel = new JLabel("Lot ID: " + availableSpots.getInt("lot_id") + ", Spot Number: " + availableSpots.getInt("spot_number"));
						resultPanel.add(spotLabel);
					}
					
					JScrollPane scrollPane = new JScrollPane(resultPanel);
					scrollPane.setPreferredSize(new Dimension(200, 400));
					resultFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
					
					resultFrame.pack();
					resultFrame.setResizable(false);
					resultFrame.setLocationRelativeTo(_this);
					resultFrame.setVisible(true);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(availableSlotButton, constraints);
		
		JButton monthlyRevenueButton = new JButton("Report 2: Monthly Revenue");
		monthlyRevenueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					ResultSet monthlyRevenue = User.getMonthlyRevenue();
					
					JFrame resultFrame = new JFrame("Report 2 Results");
					
					JPanel resultPanel = new JPanel();
					resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
					
					while (monthlyRevenue.next()) {
						JLabel spotLabel = new JLabel(
							"Lot ID: " + monthlyRevenue.getInt("lot_id") + ", "
							+ monthlyRevenue.getInt("month") + "/" + monthlyRevenue.getInt("year")
							+ " - $" + monthlyRevenue.getInt("total_revenue")
						);
						resultPanel.add(spotLabel);
					}
					
					JScrollPane scrollPane = new JScrollPane(resultPanel);
					scrollPane.setPreferredSize(new Dimension(200, 400));
					resultFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
					
					resultFrame.pack();
					resultFrame.setResizable(false);
					resultFrame.setLocationRelativeTo(_this);
					resultFrame.setVisible(true);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(monthlyRevenueButton, constraints);
		
		JButton parkingUsageButton = new JButton("Report 3: Parking Usage");
		parkingUsageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					ResultSet parkingUsage = User.getParkingUsage();
					
					JFrame resultFrame = new JFrame("Report 3 Results");
					
					JPanel resultPanel = new JPanel();
					resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
					
					while (parkingUsage.next()) {
						JLabel spotLabel = new JLabel(
							"Lot ID: " + parkingUsage.getInt("lot_id") + ", Category: "
							+ parkingUsage.getString("category") + " - " + parkingUsage.getInt("count")
							+ " reservations"
						);
						resultPanel.add(spotLabel);
					}
					
					JScrollPane scrollPane = new JScrollPane(resultPanel);
					scrollPane.setPreferredSize(new Dimension(300, 400));
					resultFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
					
					resultFrame.pack();
					resultFrame.setResizable(false);
					resultFrame.setLocationRelativeTo(_this);
					resultFrame.setVisible(true);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		panel.add(parkingUsageButton, constraints);
		
		JButton userSessionButton = new JButton("Report 4: User Sessions");
		userSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					ResultSet userSessions = User.getSessions();
					
					JFrame resultFrame = new JFrame("Report 4 Results");
					
					JPanel resultPanel = new JPanel();
					resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
					
					while (userSessions.next()) {
						JLabel spotLabel = new JLabel(
							"User ID: " + userSessions.getInt("user_id") + ", Name: "
							+ userSessions.getString("name") + " - " + userSessions.getTimestamp("login_time")
							+ " to " + userSessions.getTimestamp("logout_time")
						);
						resultPanel.add(spotLabel);
					}
					
					JScrollPane scrollPane = new JScrollPane(resultPanel);
					scrollPane.setPreferredSize(new Dimension(600, 400));
					resultFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
					
					resultFrame.pack();
					resultFrame.setResizable(false);
					resultFrame.setLocationRelativeTo(_this);
					resultFrame.setVisible(true);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		panel.add(userSessionButton, constraints);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		setSize(600, 400);
		setResizable(false);
		setVisible(false);
	}
}
