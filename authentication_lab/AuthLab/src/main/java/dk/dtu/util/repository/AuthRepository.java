package dk.dtu.util.repository;

import dk.dtu.util.configuration.Configuration;

import java.io.IOException;
import java.sql.*;

public class AuthRepository {

    private static Configuration conf;

    static {
        try {
            conf = Configuration.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String url = conf.getDBeUrl();
    String dbUsername = conf.getDBUsername();
    String password = conf.getDBPassword();

    public int addUser(String username, String passwordhash) {

        System.out.println(url);
        System.out.println(dbUsername);
        System.out.println(password);

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
