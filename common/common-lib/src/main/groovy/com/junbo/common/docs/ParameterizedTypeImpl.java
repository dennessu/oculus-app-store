/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.docs;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

class ParameterizedTypeImpl implements ParameterizedType {

    private Type rawType;
    private Type[] actualTypeArguments;

    public ParameterizedTypeImpl(Type rawType, Type[] actualTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public int hashCode() {
        int retVal = Arrays.hashCode(actualTypeArguments);
        if (rawType != null) {
            retVal ^= rawType.hashCode();
        }
        return retVal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ParameterizedType)) {
            return false;
        }

        ParameterizedType other = (ParameterizedType) o;

        if (!rawType.equals(other.getRawType())) {
            return false;
        }

        Type[] otherActuals = other.getActualTypeArguments();
        if (otherActuals.length != actualTypeArguments.length) {
            return false;
        }

        for (int lcv = 0; lcv < otherActuals.length; lcv++) {
            if (!actualTypeArguments[lcv].equals(otherActuals[lcv])) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(((Class)rawType).getName() + "<");
        for (int i = 0; i < actualTypeArguments.length; ++i) {
            Type p = actualTypeArguments[i];
            builder.append(p.toString());
            if (i < actualTypeArguments.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(">");
        return builder.toString();
    }
}
