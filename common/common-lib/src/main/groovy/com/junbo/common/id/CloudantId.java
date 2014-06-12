/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;

import java.io.Serializable;
import java.util.Properties;

/**
 * generic identifier class.
 *
 */
public abstract class CloudantId extends Object implements Serializable, PropertyAssignedAware {

    private final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

    private String value;

    protected Properties resourcePathPlaceHolder;

    // default ctor needed for Class.newInstance in deserializer
    public CloudantId() {
        // value = new Long(-1);
    }

    public CloudantId(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public Properties getResourcePathPlaceHolder() {
        if (resourcePathPlaceHolder == null) {
            resourcePathPlaceHolder = new Properties();
        }

        return resourcePathPlaceHolder;
    }

    @Override
    public String toString() {
        if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    @Override
    public int hashCode() {
        if (value != null) {
            return value.hashCode();
        }
        return 0;
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

        if (value != null) {
            return value.equals(((CloudantId)other).value);
        }
        return ((CloudantId)other).value == null;
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}
