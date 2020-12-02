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
