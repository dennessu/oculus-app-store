/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.hibernate.multitenancy;

import org.hibernate.HibernateException;
import org.hibernate.service.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by minhao on 3/3/14.
 */
@SuppressWarnings("serial")
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSource;
    }

    /**
     * Obtains a connection for Hibernate use according to the underlying strategy of this provider.
     *
     * @param tenantIdentifier The identifier of the tenant for which to get a connection
     *
     * @return The obtained JDBC connection
     *
     * @throws SQLException Indicates a problem opening a connection
     * @throws org.hibernate.HibernateException Indicates a problem otherwise obtaining a connection.
     */
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        // getAnyConnection() allows access to the database metadata of the underlying database(s) in situations where we do not have a
        // tenant id (like startup processing, for example).
        final Connection connection = getAnyConnection();
        try {
            connection.createStatement().execute( "set search_path=" + tenantIdentifier );
        }
        catch ( SQLException e ) {
            throw new HibernateException(
                    "Could not alter JDBC connection to specified schema [" +
                            tenantIdentifier + "]",
                    e
            );
        }
        return connection;
    }
}
