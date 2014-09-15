/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.pool;

import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.webflow.action.Action;
import com.zaxxer.hikari.util.DriverDataSource;
import org.postgresql.Driver;
import org.postgresql.util.HostSpec;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * JunboDriverDataSource.
 */
public class JunboDriverDataSource implements DataSource {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JunboDriverDataSource.class);

    private DriverDataSource[] candidates;
    private PrintWriter logWriter;

    public JunboDriverDataSource(String jdbcUrl,
                                 Properties properties,
                                 String username,
                                 String password) {

        Properties props;
        try {
            props = Driver.parseURL(jdbcUrl, properties);
        } catch (SQLException e) {
            LOGGER.error("Error occurred during parsing jdbc url [" + jdbcUrl + "].");
            throw new RuntimeException(e);
        }

        HostSpec[] hosts = hostSpecs(props);
        candidates = new DriverDataSource[hosts.length];

        String hostsUrl = getHostsUrl(jdbcUrl);

        for (int i = 0; i < hosts.length; i++) {
            String hostUrl = jdbcUrl.replace(hostsUrl, hosts[i].getHost() + ":" + hosts[i].getPort());
            candidates[i] = new DriverDataSource(hostUrl, properties, username, password);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connect(new Func<DataSource, Connection>() {
            public Connection apply(DataSource dataSource) throws SQLException {
                return dataSource.getConnection();
            }
        });
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return connect(new Func<DataSource, Connection>() {
            public Connection apply(DataSource dataSource) throws SQLException {
                return dataSource.getConnection(username, password);
            }
        });
    }

    private Connection connect(Func<DataSource, Connection> func) throws SQLException {
        for (DriverDataSource dataSource : candidates) {
            try {
                Connection conn = func.apply(dataSource);

                if (conn != null) {
                    return conn;
                } else {
                    LOGGER.warn("Get invalid connection from data source [" + dataSource + "].");
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to get connection from data source [" + dataSource + "].");
            }
        }

        throw new SQLException("Failed to get available connection.");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        this.logWriter = logWriter;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    private static interface Func<I, O> {
        public O apply(I input) throws Exception;
    }

    private static HostSpec[] hostSpecs(Properties props) {
        String[] hosts = props.getProperty("PGHOST").split(",");
        String[] ports = props.getProperty("PGPORT").split(",");
        HostSpec[] hostSpecs = new HostSpec[hosts.length];
        for (int i = 0; i < hostSpecs.length; ++i) {
            hostSpecs[i] = new HostSpec(hosts[i], Integer.parseInt(ports[i]));
        }
        return hostSpecs;
    }

    private static String getHostsUrl(String jdbcUrl) {
        // jdbc url has already been parsed before, no need to do validation anymore
        jdbcUrl = jdbcUrl.substring("jdbc:postgresql://".length());
        int slash = jdbcUrl.indexOf('/');

        return jdbcUrl.substring(0, slash);
    }
}
