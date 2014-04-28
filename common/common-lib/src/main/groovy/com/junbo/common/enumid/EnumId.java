/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.enumid;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by haomin on 14-4-24.
 */
public abstract class EnumId extends Object implements Serializable {

    private String value;

    protected Properties resourcePathPlaceHolder;

    // default ctor needed for Class.newInstance in deserializer
    public EnumId() {
        value = "";
    }

    public EnumId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Properties getResourcePathPlaceHolder() {
        if (resourcePathPlaceHolder == null) {
            resourcePathPlaceHolder = new Properties();
        }

        return resourcePathPlaceHolder;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value == null? super.hashCode(): value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        return value.equals(((EnumId)other).value);
    }
}
