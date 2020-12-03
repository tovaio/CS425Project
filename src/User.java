import java.sql.*;

public class User {
	private static String databaseURL = "jdbc:postgresql://localhost:5432/parking?user=parking_admin&password=admin";
	private static Connection connection;
	
	private static PreparedStatement searchStatement;
	private static PreparedStatement registerStatement;
	private static PreparedStatement userListStatement;
	private static PreparedStatement getUserStatement;
	private static PreparedStatement setUserStatement;
	private static PreparedStatement adminSetUserStatement;
	private static PreparedStatement getLotStatement;
	private static PreparedStatement addSessionStatement;
	private static PreparedStatement addPlateStatement;
	
	private static PreparedStatement lotListStatement;
	private static PreparedStatement lotAvailabilityStatement;
	private static PreparedStatement lotAvailabilityMonthStatement;
	private static PreparedStatement reserveStatement;
	private static PreparedStatement singleReserveStatement;
	private static PreparedStatement memberReserveStatement;
	
	private static PreparedStatement availableSpotStatement;
	private static PreparedStatement monthlyRevenueStatement;
	private static PreparedStatement parkingUsageStatement;
	private static PreparedStatement getSessionStatement;

	private int userID;
	private String username;
	private boolean isAdmin;
	private boolean isLotStaff;
	private int staffedLotID;
	
	public User(String _username, String _password, boolean registerNewUser) throws ParkingException, SQLException {
		User.validateUsernameAndPassword(_username, _password);
		User.connect();
		
		ResultSet searchResult = User.searchUser(_username);
		
		if (registerNewUser) {
			if (!searchResult.next()) {
				ResultSet registerResult = User.registerUser(_username, _password);
				
				if (registerResult.next()) {
					setCredentials(registerResult);
				} else {
					throw new ParkingException("Unable to register user \"" + _username + "\".");
				}
			} else {
				throw new ParkingException("User \"" + _username + "\" already exists.");
			}
		} else {
			if (searchResult.next() && searchResult.getString("password").equals(_password)) {
				setCredentials(searchResult);
			} else {
				throw new ParkingException("Incorrect login for user \"" + _username + "\".");
			}
		}
	}
	
	private static void validateUsernameAndPassword(String _username, String _password) throws ParkingException {
		if (_username.length() == 0) {
			throw new ParkingException("Username field must not be empty.");
		}
		if (_password.length() == 0) {
			throw new ParkingException("Password field must not be empty.");
		}
	}
	
	private static void connect() throws SQLException {
		// Create a connection to the database if one does not exist yet
		if (connection == null) {
			connection = DriverManager.getConnection(databaseURL);
			
			// Prepare SQL statements
			searchStatement = connection.prepareStatement("select * from \"user\" where upper(\"name\") = upper(?);");
			registerStatement = connection.prepareStatement("insert into \"user\" values (default, ?, ?, false, null) returning *;");
			userListStatement = connection.prepareStatement("select user_id, name from \"user\" order by user_id;");
			getUserStatement = connection.prepareStatement("select * from \"user\" where user_id = ?;");
			setUserStatement = connection.prepareStatement("update \"user\" set \"name\" = ?, \"password\" = ? where user_id = ?;");
			adminSetUserStatement = connection.prepareStatement("update \"user\" set \"name\" = ?, \"password\" = ?, is_admin = ?, lot_id = ? where user_id = ?;");
			getLotStatement = connection.prepareStatement("select * from parking_lot where lot_id = ?;");
			addSessionStatement = connection.prepareStatement("insert into \"session\" values (?, ?, ?);");
			addPlateStatement = connection.prepareStatement("insert into car_plate values (default, ?, ?, ?) returning *;");
			
			// Reservations
			lotListStatement = connection.prepareStatement("select * from parking_lot;");
			lotAvailabilityStatement = connection.prepareStatement("select * from available_spots(?, ?, ?) where lot_id = ?;");
			lotAvailabilityMonthStatement = connection.prepareStatement("select * from available_spots_month(?, ?, ?, ?) where lot_id = ?;");
			reserveStatement = connection.prepareStatement("insert into reservation values (default, ?, ?, cast(? as res_category), ?) returning res_id;");
			singleReserveStatement = connection.prepareStatement("insert into single_reservation values (?, ?, ?);");
			memberReserveStatement = connection.prepareStatement("insert into \"member\" values (?, ?, ?, ?, ?);");
			
			// Reports
			availableSpotStatement = connection.prepareStatement("select * from available_spots(?, ?, ?);");
			monthlyRevenueStatement = connection.prepareStatement("select * from monthly_revenue order by lot_id, \"year\", \"month\";");
			parkingUsageStatement = connection.prepareStatement("select * from parking_usage order by lot_id, category;");
			getSessionStatement = connection.prepareStatement("select * from user_sessions order by login_time;");
		}
	}
	
	public static ResultSet searchUser(String username) throws SQLException {
		searchStatement.setString(1, username);
		return searchStatement.executeQuery();
	}
	
	public static ResultSet registerUser(String username, String password) throws SQLException {
		registerStatement.setString(1, username);
		registerStatement.setString(2, password);
		return registerStatement.executeQuery();
	}
	
	public static ResultSet getUserList() throws SQLException {
		return userListStatement.executeQuery();
	}
	
	public static ResultSet getUser(int userID) throws SQLException {
		getUserStatement.setInt(1, userID);
		return getUserStatement.executeQuery();
	}
	
	public static void setUser(int userID, String username, String password) throws ParkingException, SQLException {
		ResultSet searchResult = User.searchUser(username);
		
		if (searchResult.next() && searchResult.getInt("user_id") != userID) {
			throw new ParkingException("Username \"" + username + "\" is already taken.");
		}
		
		setUserStatement.setString(1, username);
		setUserStatement.setString(2, password);
		setUserStatement.setInt(3, userID);
		setUserStatement.executeUpdate();
	}
	
	public static void adminSetUser(int userID, String username, String password, boolean isAdmin, int lotID) throws ParkingException, SQLException {
		ResultSet searchResult = User.searchUser(username);
		
		if (searchResult.next() && searchResult.getInt("user_id") != userID) {
			throw new ParkingException("Username \"" + username + "\" is already taken.");
		}
		
		if (lotID > 0) {
			ResultSet lotResult = User.getLot(lotID);
			
			if (!lotResult.next()) {
				throw new ParkingException("Lot with ID " + lotID + " does not exist.");
			}
		}
		
		adminSetUserStatement.setString(1, username);
		adminSetUserStatement.setString(2, password);
		adminSetUserStatement.setBoolean(3, isAdmin);
		if (lotID > 0) {
			adminSetUserStatement.setInt(4, lotID);
		} else {
			adminSetUserStatement.setNull(4, java.sql.Types.INTEGER);
		}
		adminSetUserStatement.setInt(5, userID);
		adminSetUserStatement.executeUpdate();
	}
	
	public static ResultSet getLot(int lotID) throws SQLException {
		getLotStatement.setInt(1, lotID);
		return getLotStatement.executeQuery();
	}
	
	public static void addSession(int userID, Timestamp loginTime, Timestamp logoutTime) throws SQLException {
		addSessionStatement.setInt(1, userID);
		addSessionStatement.setTimestamp(2, loginTime);
		addSessionStatement.setTimestamp(3, logoutTime);
		addSessionStatement.executeUpdate();
	}
	
	public static ResultSet addPlate(String plateNumber, boolean isTemp) throws SQLException {
		addPlateStatement.setString(1, plateNumber);
		addPlateStatement.setBoolean(2, isTemp);
		if (isTemp) {
			Date tempDate = new Date(System.currentTimeMillis());
			addPlateStatement.setDate(3, tempDate);
		} else {
			addPlateStatement.setNull(3, java.sql.Types.DATE);
		}
		return addPlateStatement.executeQuery();
	}
	
	public static ResultSet getLotList() throws SQLException {
		return lotListStatement.executeQuery();
	}
	
	public static ResultSet getLotAvailability(int lotID, Time startTime, Time endTime, Date date) throws SQLException {
		lotAvailabilityStatement.setTime(1, startTime);
		lotAvailabilityStatement.setTime(2, endTime);
		lotAvailabilityStatement.setDate(3, date);
		lotAvailabilityStatement.setInt(4, lotID);
		return lotAvailabilityStatement.executeQuery();
	}
	
	public static ResultSet getLotAvailabilityMonth(int lotID, Time startTime, Time endTime, int month, int year) throws SQLException {
		lotAvailabilityMonthStatement.setTime(1, startTime);
		lotAvailabilityMonthStatement.setTime(2, endTime);
		lotAvailabilityMonthStatement.setInt(3, month);
		lotAvailabilityMonthStatement.setInt(4, year);
		lotAvailabilityMonthStatement.setInt(5, lotID);
		return lotAvailabilityMonthStatement.executeQuery();
	}
	
	private static ResultSet reserve(Time startTime, Time endTime, String category, int spotID) throws ParkingException, SQLException {
		if (startTime.getTime() - endTime.getTime() >= 0) {
			throw new ParkingException("Start time must be before end time.");
		}
		
		reserveStatement.setTime(1, startTime);
		reserveStatement.setTime(2, endTime);
		reserveStatement.setString(3, category);
		reserveStatement.setInt(4, spotID);
		return reserveStatement.executeQuery();
	}
	
	public static void singleReserve(int userID, int spotID, Time startTime, Time endTime, Date date, boolean is_guest) throws ParkingException, SQLException {
		String category = (is_guest) ? "drive-in" : "online";
		ResultSet reserveResult = User.reserve(startTime, endTime, category, spotID);
		
		if (!reserveResult.next()) {
			throw new ParkingException("Reservation failed.");
		}
		
		singleReserveStatement.setInt(1, reserveResult.getInt("res_id"));
		singleReserveStatement.setDate(2, date);
		singleReserveStatement.setInt(3, userID);
		singleReserveStatement.executeUpdate();
	}
	
	public static void memberReserve(int userID, int spotID, Time startTime, Time endTime, int month, int year, int plateID) throws ParkingException, SQLException {
		if (month < 1 || month > 12) {
			throw new ParkingException("Invalid month.");
		}
		
		ResultSet reserveResult = User.reserve(startTime, endTime, "member", spotID);
		
		if (!reserveResult.next()) {
			throw new ParkingException("Reservation failed.");
		}
		
		memberReserveStatement.setInt(1, userID);
		memberReserveStatement.setInt(2, month);
		memberReserveStatement.setInt(3, year);
		memberReserveStatement.setInt(4, plateID);
		memberReserveStatement.setInt(5, reserveResult.getInt("res_id"));
		memberReserveStatement.executeUpdate();
	}
	
	public static ResultSet getAvailableSpots(Time startTime, Time endTime, Date date) throws SQLException {
		availableSpotStatement.setTime(1, startTime);
		availableSpotStatement.setTime(2, endTime);
		availableSpotStatement.setDate(3, date);
		return availableSpotStatement.executeQuery();
	}
	
	public static ResultSet getMonthlyRevenue() throws SQLException {
		return monthlyRevenueStatement.executeQuery();
	}
	
	public static ResultSet getParkingUsage() throws SQLException {
		return parkingUsageStatement.executeQuery();
	}
	
	public static ResultSet getSessions() throws SQLException {
		return getSessionStatement.executeQuery();
	}
	
	private void setCredentials(ResultSet userRow) throws SQLException {
		setUserID(userRow.getInt("user_id"));
		setUsername(userRow.getString("name"));
		setIsAdmin(userRow.getBoolean("is_admin"));
		setStaffedLotID(userRow.getInt("lot_id"));
	}
	
	private void setUserID(int _userID) {
		userID = _userID;
	}
	
	private void setUsername(String _username) {
		username = _username;
	}
	
	private void setIsAdmin(boolean _isAdmin) {
		isAdmin = _isAdmin;
	}
	
	private void setStaffedLotID(int _staffedLotID) {
		staffedLotID = _staffedLotID;
		isLotStaff = (_staffedLotID != 0);
	}
	
	public int getUserID() {
		return userID;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean getIsAdmin() {
		return isAdmin;
	}
	
	public boolean getIsLotStaff() {
		return isLotStaff;
	}
	
	public int getStaffedLotID() {
		return staffedLotID;
	}
}
