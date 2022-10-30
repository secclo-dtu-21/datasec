package dk.dtu.util.repository;

import java.sql.*;

public class AuthRepository {
    String url = "jdbc:postgresql://mouse.db.elephantsql.com:5432/ravvvhek";
    String dbUsername = "ravvvhek";
    String password = "BEd3TPp5KhemuMmKE2WMnCwjTc9-abir";

    public int addUser(String username, String passwordhash) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, password);
            PreparedStatement preparedStatement = conn.prepareStatement("insert into users (username, password_hash) values (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordhash);
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
            PreparedStatement preparedStatement = conn.prepareStatement("select password_hash from users where username = ?");
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

    public void clearDatabase() {
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, password);
            Statement statement = conn.createStatement();
            String sql = "TRUNCATE TABLE users";
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
            PreparedStatement preparedStatement = conn.prepareStatement("select count(*) from users");
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
