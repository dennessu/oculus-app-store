/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import java.sql.*;

/**
 * Created by Yunlong on 3/27/14.
 */
public class DBHelper {
    private String postgresConnectionString;
    private String postgresUserName;
    private String postgresPassword;

    private LogHelper logger = new LogHelper(DBHelper.class);

    public DBHelper() {
        try {
            this.postgresConnectionString = ConfigPropertiesHelper.instance().getProperty("db.postgresql.connectionstring");
            this.postgresUserName = ConfigPropertiesHelper.instance().getProperty("db.postgresql.username");
            this.postgresPassword = ConfigPropertiesHelper.instance().getProperty("db.postgresql.password");
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Enum for database name.
     *
     * @author Yunlongzhao
     */
    public enum DBName {
        BILLING("billing"),
        CART("cart"),
        CATALOG("catalog"),
        EMAIL("email"),
        ENTITLEMENT("entitlement"),
        FULFILMENT("fulfilment"),
        IDENTITY("identity"),
        ORDER("order"),
        PAYMENT("payment"),
        SHARDING("sharding"),
        EWALLET("ewallet");

        private String dbName;

        private DBName(String dbName) {
            this.dbName = dbName;
        }

        public String getDBName() {
            return this.dbName;
        }
    }


    private Connection getConnection(DBName dbName) throws Exception {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            String url = this.postgresConnectionString + "/" + dbName.getDBName();
            Connection conn = DriverManager.getConnection(url, this.postgresUserName, this.postgresPassword);
            return conn;
        } catch (ClassNotFoundException classNotFoundException) {
            throw new RuntimeException("class not found for org.postgresql.driver");
        }
    }

    public String executeScalar(String sqlStatement, DBName dbName) throws Exception {
        try {
            logger.logInfo("============Execute SQL Scalar: Start==============================");
            logger.logInfo(sqlStatement);
            Connection conn = getConnection(dbName);
            Statement statement = conn.createStatement();
            //String sql="set clint_encoding to ''utf8'' ";
            //sql+=sqlStatement;
            ResultSet resultSet = statement.executeQuery(sqlStatement);
            /*
            if (resultSet.getFetchSize() <= 0) {
                return null;
            }
            */
            String value = new String();
            if (resultSet.next()) {
                value = resultSet.getString(1);
            }
            logger.logInfo(String.format("Get sql query result value : %s", value));
            logger.logInfo("============Execute SQL Scalar: END ================================");
            closeConnection(conn);
            statement.close();
            return value;
        } catch (SQLException sqlEx) {
            throw new RuntimeException(
                    String.format("Failed to execute sql statement. Connection string: %s. Query: %s. Message: %s",
                            this.postgresConnectionString, sqlStatement, sqlEx.getMessage()));
        }
    }

    public int executeUpdate(String sqlStatement, DBName dbName) throws Exception {
        try {
            logger.logInfo("============Execute SQL Update: Start==============================");
            logger.logInfo(sqlStatement);
            Connection conn = getConnection(dbName);
            Statement statement = conn.createStatement();
            int affectedRows = statement.executeUpdate(sqlStatement);
            closeConnection(conn);
            statement.close();
            logger.logInfo(String.format(
                    "============Execute SQL Update Complete. Affected Rows: %s============", affectedRows));
            logger.logInfo("============Execute SQL Scalar: END ================================");
            return affectedRows;
        } catch (SQLException sqlEx) {
            throw new RuntimeException(
                    String.format("Failed to execute sql statement. Connection string: %s. Query: %s. Message: %s",
                            this.postgresConnectionString, sqlStatement, sqlEx.getMessage()));
        }
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException(
                    String.format("Failed to close sql connection. Connection string: %s. Message: %s",
                            this.postgresConnectionString, sqlEx.getMessage()));
        }
    }

}
