package main.java.com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class MariaDBConnector {

    public static Connection getConnection() {

        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/dbconfig.properties")) {

            Properties prop = new Properties();
            //load a properties file from class path, inside static method
            prop.load(input);

            Class.forName(prop.getProperty("db.driver"));

            return DriverManager.getConnection(
                    prop.getProperty("db.url"),
                    prop.getProperty("db.user"),
                    prop.getProperty("db.pass"));

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void executeStatement(String sql, String successMessage) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                PreparedStatement state = conn.prepareStatement(sql);
                state.execute();
                System.out.println(successMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeRSStatement(String sql, String successMessage) {
        ResultSet resultSet = null;
        try (Connection conn = getConnection()) {
            if (conn != null) {
                PreparedStatement state = conn.prepareStatement(sql);
                resultSet = state.executeQuery();
                System.out.println(successMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

}
