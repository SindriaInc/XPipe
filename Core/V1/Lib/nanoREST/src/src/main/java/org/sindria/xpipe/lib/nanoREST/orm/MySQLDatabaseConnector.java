package org.sindria.xpipe.lib.nanoREST.orm;

import org.sindria.xpipe.lib.nanoREST.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class MySQLDatabaseConnector {

    //private static final String URL = "jdbc:mysql://172.16.10.253:3306/app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000&autoReconnect=true";
    //private static final String USER = "your_username";
    //private static final String PASSWORD = "your_password";

    private static final String URL = AppConfig.config.getNanorest().getDatasource().getUrl();
    private static final String USER = AppConfig.config.getNanorest().getDatasource().getUsername();
    private static final String PASSWORD = AppConfig.config.getNanorest().getDatasource().getPassword();

    protected Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    public MySQLDatabaseConnector() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public abstract List<List<String>> executeQuery(String query);

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

// Example
//    public static void main(String[] args) {
//        MySQLDatabaseConnector db = new MySQLDatabaseConnector() {
//            @Override
//            public List<List<String>> executeQuery(String query) {
//                List<List<String>> results = new ArrayList<>();
//                try (Statement statement = connection.createStatement()) {
//                    boolean isResultSet = statement.execute(query);
//                    if (isResultSet) {
//                        ResultSet rs = statement.getResultSet();
//                        ResultSetMetaData metaData = rs.getMetaData();
//                        int columnCount = metaData.getColumnCount();
//
//                        while (rs.next()) {
//                            List<String> row = new ArrayList<>();
//                            for (int i = 1; i <= columnCount; i++) {
//                                row.add(rs.getString(i));
//                            }
//                            results.add(row);
//                        }
//                    }
//                } catch (SQLException e) {
//                    System.err.println("Query execution error: " + e.getMessage());
//                }
//                return results;
//            }
//        };
//
//        List<List<String>> queryResults = db.executeQuery("SELECT * FROM your_table"); // Example query
//        for (List<String> row : queryResults) {
//            System.out.println(row);
//        }
//        db.closeConnection();
//    }

}