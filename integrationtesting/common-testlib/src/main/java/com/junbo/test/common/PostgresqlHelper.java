/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author dw
 */
public class PostgresqlHelper {

    private PostgresqlHelper() {

    }

    public static final String DefaultPostgresqlHost = ConfigHelper.getSetting("defaultPostgresqlHost");
    public static final String DefaultPostgresqlUser = ConfigHelper.getSetting("defaultPostgresqlUser");
    public static final String DefaultPostgresqlPwd = ConfigHelper.getSetting("defaultPostgresqlPwd");

    public static String QuerySingleRowSingleColumn(String query, String db) throws Exception {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            connection = DriverManager.getConnection(
                    DefaultPostgresqlHost + "/" + db, DefaultPostgresqlUser, DefaultPostgresqlPwd);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }
    }
}
