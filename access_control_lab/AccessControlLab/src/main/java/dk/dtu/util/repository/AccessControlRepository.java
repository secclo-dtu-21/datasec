package dk.dtu.util.repository;

import dk.dtu.util.configuration.Configuration;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AccessControlRepository {

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

    public int updateAccessControlList(String username, String permission) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, password);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("update access_control_list " +
                            "set print = ?, queue = ?, top_queue = ?, start = ?, stop = ?, restart = ?, status = ?, read_config = ?, set_config = ?" +
                            "where username = ?");
            preparedStatement.setInt(1, permission.charAt(0) - 48);
            preparedStatement.setInt(2, permission.charAt(1) - 48);
            preparedStatement.setInt(3, permission.charAt(2) - 48);
            preparedStatement.setInt(4, permission.charAt(3) - 48);
            preparedStatement.setInt(5, permission.charAt(4) - 48);
            preparedStatement.setInt(6, permission.charAt(5) - 48);
            preparedStatement.setInt(7, permission.charAt(6) - 48);
            preparedStatement.setInt(8, permission.charAt(7) - 48);
            preparedStatement.setInt(9, permission.charAt(8) - 48);
            preparedStatement.setString(10, username);
            result = preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addAccessControlList(String username, String permission) {
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, password);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("insert into access_control_list (username, print, queue, top_queue, start, stop, restart, status, read_config, set_config) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, permission.charAt(0) - 48);
            preparedStatement.setInt(3, permission.charAt(1) - 48);
            preparedStatement.setInt(4, permission.charAt(2) - 48);
            preparedStatement.setInt(5, permission.charAt(3) - 48);
            preparedStatement.setInt(6, permission.charAt(4) - 48);
            preparedStatement.setInt(7, permission.charAt(5) - 48);
            preparedStatement.setInt(8, permission.charAt(6) - 48);
            preparedStatement.setInt(9, permission.charAt(7) - 48);
            preparedStatement.setInt(10, permission.charAt(8) - 48);
            preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Boolean> getAccessControlListByName(String username) {
        Map<String, Boolean> operations = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, password);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("select print, queue, top_queue, start, stop, restart, status, read_config, set_config from access_control_list where username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                operations.put("print", resultSet.getInt("print") != 0);
                operations.put("queue", resultSet.getInt("queue") != 0);
                operations.put("topQueue", resultSet.getInt("top_queue") != 0);
                operations.put("start", resultSet.getInt("start") != 0);
                operations.put("stop", resultSet.getInt("stop") != 0);
                operations.put("restart", resultSet.getInt("restart") != 0);
                operations.put("status", resultSet.getInt("status") != 0);
                operations.put("readConfig", resultSet.getInt("read_config") != 0);
                operations.put("setConfig", resultSet.getInt("set_config") != 0);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return operations;
    }

    public int deleteAccessControlListByName(String username) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, password);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("delete from access_control_list where username = ?");
            preparedStatement.setString(1, username);
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
            String sql = "TRUNCATE TABLE access_control_list";
            statement.executeUpdate(sql);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
