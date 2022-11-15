package dk.dtu.util.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import dk.dtu.util.configuration.Configuration;

public class UserRepository {

	private static Configuration conf;

	static {
		try {
			conf = Configuration.getInstance();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	String url = conf.getDBUrl();
	String dbUsername = conf.getDBUsername();
	String password = conf.getDBPassword();

	public int addUser(String username, String passwordhash, String role) {
		int result = 0;
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			PreparedStatement preparedStatement = conn
					.prepareStatement("insert into users_with_roles (username, role, password_hash) values (?, ?, ?)");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, role);
			preparedStatement.setString(3, passwordhash);
			result = preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int deleteUserByName(String username) {
		int result = 0;
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			PreparedStatement preparedStatement = conn
					.prepareStatement("delete from users_with_roles where username = ?");
			preparedStatement.setString(1, username);
			result = preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String getUserPasswordHashByName(String username) {
		String result = null;
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			PreparedStatement preparedStatement = conn
					.prepareStatement("select password_hash from users_with_roles where username = ?");
			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				result = resultSet.getString("password_hash");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String getUserRoleByName(String username) {
		String result = null;
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			PreparedStatement preparedStatement = conn
					.prepareStatement("select role from users_with_roles where username = ?");
			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				result = resultSet.getString("role");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int updateUserRoleByName(String role, String username) {
		int result = 0;
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			PreparedStatement preparedStatement = conn
					.prepareStatement("update users_with_roles set role = ? where username = ?");
			preparedStatement.setString(1, role);
			preparedStatement.setString(2, username);
			result = preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void clearDatabase() {
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			Statement statement = conn.createStatement();
			String sql = "TRUNCATE TABLE users_with_roles";
			statement.executeUpdate(sql);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDataNumber() {
		int result = 0;
		try {
			Connection conn = DriverManager.getConnection(url, dbUsername, password);
			PreparedStatement preparedStatement = conn.prepareStatement("select count(*) from users_with_roles");
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				result = resultSet.getInt(1);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
