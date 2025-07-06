package org.sindria.xpipe.core.lib.nanorest.repository;

import org.sindria.xpipe.core.lib.nanorest.logger.Logger;
import org.sindria.xpipe.core.lib.nanorest.orm.MySQLDatabaseConnector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRepository {

    /**
     * logger
     */
    protected final Logger logger;

    /**
     * connector
     */
    protected final MySQLDatabaseConnector connector;


    public BaseRepository() {
        this.logger = Logger.getInstance();

        this.connector = new MySQLDatabaseConnector() {
            @Override
            public List<List<String>> executeQuery(String query) {
                List<List<String>> results = new ArrayList<>();
                try (Statement statement = connection.createStatement()) {
                    boolean isResultSet = statement.execute(query);
                    if (isResultSet) {
                        ResultSet rs = statement.getResultSet();
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        while (rs.next()) {
                            List<String> row = new ArrayList<>();
                            for (int i = 1; i <= columnCount; i++) {
                                row.add(rs.getString(i));
                            }
                            results.add(row);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Query execution error: " + e.getMessage());
                }
                return results;
            }
        };
    }



    public List<List<String>> query(String query) {
        try {
            return this.connector.executeQuery(query);
        } finally {
            this.connector.closeConnection();
        }
    }

}
