/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * JunboHikariDataSource.
 */
public class JunboHikariDataSource extends HikariDataSource {
    public JunboHikariDataSource() {
        super();
    }

    public JunboHikariDataSource(HikariConfig configuration) {
        super(hackConfiguration(configuration));
    }

    private static HikariConfig hackConfiguration(HikariConfig configuration) {
        configuration.setDataSource(new JunboDriverDataSource(
                configuration.getJdbcUrl(),
                configuration.getDataSourceProperties(),
                configuration.getUsername(),
                configuration.getPassword()));

        // hide jdbc url
        configuration.setJdbcUrl(null);

        return configuration;
    }
}
