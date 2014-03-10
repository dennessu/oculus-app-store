/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity.def;

import com.junbo.common.util.EnumRegistry;
import com.junbo.common.util.Identifiable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * UserType to map Enum which implements Identifiable.
 */
public class IdentifiableType implements UserType, DynamicParameterizedType {
    public static final String TYPE = "com.junbo.entitlement.db.entity.def.IdentifiableType";

    private Class<Enum<?>> enumClass;

    //May use a map to return sqlTypes(not necessary now).
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
        Object value = rs.getObject(names[0]);
        if (value != null) {
            return EnumRegistry.resolve(value, enumClass);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st,
                            Object value, int index,
                            SessionImplementor session) throws HibernateException, SQLException {
        st.setObject(index, EnumRegistry.getId(enumClass.cast(value)));
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
            if (!Identifiable.class.isAssignableFrom(enumClass)) {
                throw new IllegalArgumentException("The enum should implement [Identifiable] interface.");
            }
        }
    }
}
