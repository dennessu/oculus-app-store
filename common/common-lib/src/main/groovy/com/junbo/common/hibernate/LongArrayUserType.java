/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * Type class to convert between String and JSON.
 */
public class LongArrayUserType implements UserType {
    protected static final int  SQLTYPE = java.sql.Types.ARRAY;

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner)
            throws HibernateException, SQLException {
        if (rs.getString(names[0]) == null) {
            return null;
        }
        Array array = rs.getArray(names[0]);
        return Arrays.asList((Long[]) array.getArray());
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }
        Array array = st.getConnection().createArrayOf("bigint", ((List<Long>)value).toArray());
        st.setArray(index, array);
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(final Object o) throws HibernateException {
        //return o == null ? null : ((int[]) o).clone();
        return o;
    }

    @Override
    public Serializable disassemble(final Object o) throws HibernateException {
        return (Serializable) o;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(final Object o) throws HibernateException {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class returnedClass() {
        return List.class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { SQLTYPE };
    }
}
