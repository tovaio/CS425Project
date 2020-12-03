import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;

import javax.swing.*;

import com.github.lgooddatepicker.components.*;

public class MainWindow extends JFrame {
	private User user;
	private AdminWindow adminWindow;
	private Timestamp loginTime;
	
	public MainWindow() {
		super("Parking Database Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		adminWindow = new AdminWindow();
		
		promptLogin();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				addSession();
			}
		});
	}
	
	private void addSession() {
		if (user != null && loginTime != null) {
			MainWindow _this = this;
			Timestamp logoutTime = new Timestamp(System.currentTimeMillis());
			try {
				User.addSession(user.getUserID(), loginTime, logoutTime);
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
				exception.printStackTrace(System.err);
			}
		}
	}
	
	private void promptLogin() {
		addSession();
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
		
		loginTime = new Timestamp(System.currentTimeMillis());

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
		
		JButton configureButton = new JButton("Configure user");
		configureButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ModifyUserPrompt modifyUserPrompt = new ModifyUserPrompt(_this, user.getUserID(), false);
				modifyUserPrompt.setVisible(true);
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		panel.add(configureButton, constraints);
		
		JButton reserveButton = new JButton("Make an online reservation");
		reserveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					SelectLotPrompt selectLotPrompt = new SelectLotPrompt(_this);
					selectLotPrompt.setVisible(true);
					
					int lotID = selectLotPrompt.getLotID();
					int reservationFee = selectLotPrompt.getReservationFee();
					if (lotID == 0) {
						return;
					}
					
					TimeSlotPrompt timeSlotPrompt = new TimeSlotPrompt(_this, true, true);
					timeSlotPrompt.setVisible(true);
					
					Date date = timeSlotPrompt.getDate();
					Time startTime = timeSlotPrompt.getStartTime();
					Time endTime = timeSlotPrompt.getEndTime();
					
					if (date == null || startTime == null || endTime == null) {
						return;
					}
					
					ResultSet availableSpots = User.getLotAvailability(lotID, startTime, endTime, date);
					
					SelectSpotPrompt selectSpotPrompt = new SelectSpotPrompt(_this, availableSpots);
					selectSpotPrompt.setVisible(true);
					
					int spotID = selectSpotPrompt.getSpotID();
					if (spotID == 0) {
						return;
					}
					
					int confirmation = JOptionPane.showConfirmDialog(_this, "You will be charged $" + reservationFee + " to reserve this spot. Confirm?");
					
					if (confirmation == JOptionPane.YES_OPTION) {
						User.singleReserve(user.getUserID(), spotID, startTime, endTime, date, false);
						
						JOptionPane.showMessageDialog(_this, "Online reservation completed!");
					}
				} catch (ParkingException exception) {
					JOptionPane.showMessageDialog(_this, exception.getMessage());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(reserveButton, constraints);
		
		JButton staffButton = new JButton("Staff only");
		staffButton.setEnabled(user.getIsLotStaff());
		if (user.getIsLotStaff()) {
			staffButton.setText("Staff: Process drive-in reservation in Lot " + user.getStaffedLotID());
		}
		staffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					int lotID = user.getStaffedLotID();
					if (lotID == 0) {
						return;
					}
					
					ResultSet lotResults = User.getLot(lotID);
					if (!lotResults.next()) {
						return;
					}
					
					int reservationFee = lotResults.getInt("reservation_fee");
					
					SelectUserPrompt selectUserPrompt = new SelectUserPrompt(_this);
					selectUserPrompt.setTitle("Select customer user");
					selectUserPrompt.setVisible(true);
					
					int userID = selectUserPrompt.getUserID();
					String username = selectUserPrompt.getUsername();
					if (userID == 0) {
						return;
					}
					
					TimeSlotPrompt timeSlotPrompt = new TimeSlotPrompt(_this, true, true);
					timeSlotPrompt.setVisible(true);
					
					Date date = timeSlotPrompt.getDate();
					Time startTime = timeSlotPrompt.getStartTime();
					Time endTime = timeSlotPrompt.getEndTime();
					
					if (date == null || startTime == null || endTime == null) {
						return;
					}
					
					ResultSet availableSpots = User.getLotAvailability(lotID, startTime, endTime, date);
					
					SelectSpotPrompt selectSpotPrompt = new SelectSpotPrompt(_this, availableSpots);
					selectSpotPrompt.setVisible(true);
					
					int spotID = selectSpotPrompt.getSpotID();
					if (spotID == 0) {
						return;
					}
					
					int confirmation = JOptionPane.showConfirmDialog(_this, "Did you obtain $" + reservationFee + " from " + username + "?");
					
					if (confirmation == JOptionPane.YES_OPTION) {
						User.singleReserve(userID, spotID, startTime, endTime, date, true);
						
						JOptionPane.showMessageDialog(_this, "Guest reservation completed!");
					}
				} catch (ParkingException exception) {
					JOptionPane.showMessageDialog(_this, exception.getMessage());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(staffButton, constraints);
		
		JButton membershipButton = new JButton("Buy month-long parking lot membership");
		membershipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					SelectLotPrompt selectLotPrompt = new SelectLotPrompt(_this);
					selectLotPrompt.setVisible(true);
					
					int lotID = selectLotPrompt.getLotID();
					int membershipFee = selectLotPrompt.getMembershipFee();
					if (lotID == 0) {
						return;
					}
					
					TimeSlotPrompt timeSlotPrompt = new TimeSlotPrompt(_this, true, false);
					timeSlotPrompt.setVisible(true);
					
					int month = timeSlotPrompt.getMonth();
					int year = timeSlotPrompt.getYear();
					Time startTime = timeSlotPrompt.getStartTime();
					Time endTime = timeSlotPrompt.getEndTime();
					
					if (month == 0 || year == 0 || startTime == null || endTime == null) {
						return;
					}
					
					ResultSet availableSpots = User.getLotAvailabilityMonth(lotID, startTime, endTime, month, year);
					
					SelectSpotPrompt selectSpotPrompt = new SelectSpotPrompt(_this, availableSpots);
					selectSpotPrompt.setVisible(true);
					
					int spotID = selectSpotPrompt.getSpotID();
					if (spotID == 0) {
						return;
					}
					
					CarPlatePrompt carPlatePrompt = new CarPlatePrompt(_this);
					carPlatePrompt.setVisible(true);
					
					int plateID = carPlatePrompt.getPlateID();
					if (plateID == 0) {
						JOptionPane.showMessageDialog(_this, "Filing car plate number failed.");
						return;
					}
					
					int confirmation = JOptionPane.showConfirmDialog(_this, "You will be charged $" + membershipFee + " to reserve this spot for the entire month of " + month + "/" + year + ". Confirm?");
					
					if (confirmation == JOptionPane.YES_OPTION) {
						User.memberReserve(user.getUserID(), spotID, startTime, endTime, month, year, plateID);
						
						JOptionPane.showMessageDialog(_this, "Membership registration and reservation completed!");
					}
				} catch (ParkingException exception) {
					JOptionPane.showMessageDialog(_this, exception.getMessage());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(_this, "Unexpected error; check System.err");
					exception.printStackTrace(System.err);
				}
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 4;
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
		constraints.gridy = 5;
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
