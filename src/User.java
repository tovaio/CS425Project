import java.sql.*;

public class User {
	private static String databaseURL = "jdbc:postgresql://localhost:5432/parking?user=parking_admin&password=admin";
	private static Connection connection;
	
	private static PreparedStatement loginStatement;
	private static PreparedStatement searchStatement;
	private static PreparedStatement registerStatement;

	private int userID;
	private String username;
	
	private User(int _userID, String _username) {
		setUserID(_userID);
		setUsername(_username);
	}
	
	public User(String _username, String _password) throws Exception {
		User.validateUsernameAndPassword(_username, _password);
		User.connect();
		
		loginStatement.setString(1, _username);
		loginStatement.setString(2, _password);
		ResultSet loginResult = loginStatement.executeQuery();
		
		if (loginResult.next()) {
			setUserID(loginResult.getInt("user_id"));
			setUsername(loginResult.getString("name"));
		} else {
			throw new LoginException("Incorrect login for user \"" + _username + "\".");
		}
	}
	
	public static User registerUser(String _username, String _password) throws Exception {
		User.validateUsernameAndPassword(_username, _password);
		User.connect();
		
		searchStatement.setString(1, _username);
		ResultSet searchResult = searchStatement.executeQuery();
		
		if (searchResult.next()) {
			throw new LoginException("User \"" + _username + "\" already exists.");
		} else {
			registerStatement.setString(1, _username);
			registerStatement.setString(2, _password);
			ResultSet registerResult = registerStatement.executeQuery();
			
			if (registerResult.next()) {
				return new User(registerResult.getInt("user_id"), registerResult.getString("name"));
			} else {
				throw new LoginException("Unable to register user \"" + _username + "\".");
			}
		}
	}
	
	private static void validateUsernameAndPassword(String _username, String _password) throws Exception {
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
			loginStatement = connection.prepareStatement("select * from \"user\" where upper(\"name\") = upper(?) and password = ?;");
			searchStatement = connection.prepareStatement("select * from \"user\" where upper(\"name\") = upper(?);");
			registerStatement = connection.prepareStatement("insert into \"user\" values (default, ?, ?, false, null) returning user_id, name;");
		}
	}
	
	private void setUserID(int _userID) {
		userID = _userID;
	}
	
	private void setUsername(String _username) {
		username = _username;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public String getUsername() {
		return username;
	}
}
