import java.sql.*;

public class User {
	private static String databaseURL = "jdbc:postgresql://localhost:5432/parking?user=parking_admin&password=admin";
	private static Connection connection;
	
	private static PreparedStatement searchStatement;
	private static PreparedStatement registerStatement;

	private int userID;
	private String username;
	private boolean isAdmin;
	private boolean isLotStaff;
	private int staffedLotID;
	
	public User(String _username, String _password, boolean registerNewUser) throws LoginException, SQLException {
		User.validateUsernameAndPassword(_username, _password);
		User.connect();
		
		searchStatement.setString(1, _username);
		ResultSet searchResult = searchStatement.executeQuery();
		
		if (registerNewUser) {
			if (!searchResult.next()) {
				registerStatement.setString(1, _username);
				registerStatement.setString(2, _password);
				ResultSet registerResult = registerStatement.executeQuery();
				
				if (registerResult.next()) {
					setCredentials(registerResult);
				} else {
					throw new LoginException("Unable to register user \"" + _username + "\".");
				}
			} else {
				throw new LoginException("User \"" + _username + "\" already exists.");
			}
		} else {
			if (searchResult.next() && searchResult.getString("password").equals(_password)) {
				setCredentials(searchResult);
			} else {
				throw new LoginException("Incorrect login for user \"" + _username + "\".");
			}
		}
	}
	
	private static void validateUsernameAndPassword(String _username, String _password) throws LoginException {
		if (_username.length() == 0) {
			throw new LoginException("Username field must not be empty.");
		}
		if (_password.length() == 0) {
			throw new LoginException("Password field must not be empty.");
		}
	}
	
	private static void connect() throws SQLException {
		// Create a connection to the database if one does not exist yet
		if (connection == null) {
			connection = DriverManager.getConnection(databaseURL);
			
			// Prepare SQL statements
			searchStatement = connection.prepareStatement("select * from \"user\" where upper(\"name\") = upper(?);");
			registerStatement = connection.prepareStatement("insert into \"user\" values (default, ?, ?, false, null) returning *;");
		}
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
