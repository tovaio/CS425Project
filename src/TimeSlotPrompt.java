import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.sql.*;
import java.text.NumberFormat;

import com.github.lgooddatepicker.components.*;

public class TimeSlotPrompt extends JDialog {
	private Date date;
	private int month = 0;
	private int year = 0;
	private Time startTime;
	private Time endTime;
	
	public TimeSlotPrompt(Frame parent, boolean canSelectDate, boolean normalDatePicker) {
		super(parent, "Select Time Slot", true);
		setupUI(canSelectDate, normalDatePicker);
	}
	
	private void setupUI(boolean canSelectDate, boolean normalDatePicker) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		DatePickerSettings dateSettings = new DatePickerSettings();
		dateSettings.setAllowEmptyDates(false);
		DatePicker datePicker = new DatePicker(dateSettings);
		
		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(1);
		formatter.setMaximum(3000);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		JFormattedTextField monthField = new JFormattedTextField(formatter);
		JFormattedTextField yearField = new JFormattedTextField(formatter);
		
		if (canSelectDate) {
			if (normalDatePicker) {
				JLabel dateLabel = new JLabel("Select date:");
				constraints.gridx = 0;
				constraints.gridy = 0;
				constraints.gridwidth = 1;
				panel.add(dateLabel, constraints);
				
				constraints.gridx = 1;
				constraints.gridy = 0;
				constraints.gridwidth = 1;
				panel.add(datePicker, constraints);
			} else {
				JLabel monthLabel = new JLabel("Select month (1-12):");
				constraints.gridx = 0;
				constraints.gridy = 0;
				constraints.gridwidth = 1;
				panel.add(monthLabel, constraints);
				
				constraints.gridx = 1;
				constraints.gridy = 0;
				constraints.gridwidth = 1;
				panel.add(monthField, constraints);
				
				JLabel yearLabel = new JLabel("Select year:");
				constraints.gridx = 0;
				constraints.gridy = 1;
				constraints.gridwidth = 1;
				panel.add(yearLabel, constraints);
				
				constraints.gridx = 1;
				constraints.gridy = 1;
				constraints.gridwidth = 1;
				panel.add(yearField, constraints);
			}
		}
		
		JLabel startTimeLabel = new JLabel("Select start time:");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(startTimeLabel, constraints);
		
		TimePickerSettings timeSettings = new TimePickerSettings();
		timeSettings.setAllowEmptyTimes(false);
		
		TimePicker startTimePicker = new TimePicker(timeSettings);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		panel.add(startTimePicker, constraints);
		
		JLabel endTimeLabel = new JLabel("Select end time:");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(endTimeLabel, constraints);
		
		TimePicker endTimePicker = new TimePicker(timeSettings);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		panel.add(endTimePicker, constraints);
		
		JButton submitButton = new JButton("Select Time Slot");
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (canSelectDate) {
					if (normalDatePicker) {
						date = Date.valueOf(datePicker.getDate());
					} else {
						month = (int) monthField.getValue();
						year = (int) yearField.getValue();
					}
				}
				startTime = Time.valueOf(startTimePicker.getTime());
				endTime = Time.valueOf(endTimePicker.getTime());
				dispose();
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		panel.add(submitButton, constraints);
		
		add(panel, BorderLayout.CENTER);
		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}
	
	public Date getDate() {
		return date;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getYear() {
		return year;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
}
