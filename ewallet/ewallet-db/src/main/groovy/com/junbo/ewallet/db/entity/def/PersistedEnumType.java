/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.def;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * UserType to map certain enum.
 */
public class PersistedEnumType implements UserType, DynamicParameterizedType {
    private Method returnEnum;
    private Method getPersistedValue;
    private Class<Enum<?>> enumClass;

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER};
    }

    @Override
    public Class returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
                              SessionImplementor session,
                              Object owner) throws HibernateException, SQLException {
        Integer value = rs.getInt(names[0]);
        if (value != null) {
            try {
                return returnEnum.invoke(value);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st,
                            Object value, int index,
                            SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setObject(index, null);
        }
        else {
            try {
                st.setInt(index, (Integer) getPersistedValue.invoke(value));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        if (parameters != null) {
            enumClass = ((ParameterType)
                    parameters.get(DynamicParameterizedType.PARAMETER_TYPE))
                    .getReturnedClass().asSubclass(Enum.class);
            try {
                returnEnum = enumClass.getMethod("returnEnum", new Class[]{Integer.class});
                getPersistedValue = enumClass.getMethod("getPersistedValue");
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
