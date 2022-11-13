package dk.dtu.util.repository;

import dk.dtu.util.configuration.Configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

}
