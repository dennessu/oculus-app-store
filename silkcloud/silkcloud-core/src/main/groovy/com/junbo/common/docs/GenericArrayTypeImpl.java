/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.docs;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

class GenericArrayTypeImpl implements GenericArrayType {

    private Type componentType;

    public GenericArrayTypeImpl(Type componentType) {
        this.componentType = componentType;
    }

    @Override
    public Type getGenericComponentType() {
        return componentType;
    }

    @Override
    public String toString() {
        return (componentType instanceof Class
                ? ((Class)componentType).getName()
                : componentType.toString()) + "[]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof GenericArrayType)) return false;

        GenericArrayType that = (GenericArrayType) o;

        if (componentType != null
                ? !componentType.equals(that.getGenericComponentType())
                : that.getGenericComponentType() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return componentType != null ? componentType.hashCode() : 0;
    }
}
