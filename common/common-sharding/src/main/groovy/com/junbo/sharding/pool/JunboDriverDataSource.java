/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.pool;

import com.zaxxer.hikari.util.DriverDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * JunboDriverDataSource.
 */
public class JunboDriverDataSource implements DataSource {
    private DriverDataSource driverDataSource;

    public JunboDriverDataSource(String jdbcUrl,
                                 Properties properties,
                                 String username,
                                 String password,
                                 long connectionTimeout) {

        System.out.println(connectionTimeout);

        driverDataSource = new DriverDataSource(jdbcUrl, properties, username, password);
    }

    public DriverDataSource getDriverDataSource() {
        return driverDataSource;
    }

    public void setDriverDataSource(DriverDataSource driverDataSource) {
        this.driverDataSource = driverDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return driverDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return driverDataSource.getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return driverDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        driverDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        driverDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return driverDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driverDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return driverDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return driverDataSource.isWrapperFor(iface);
    }
}
