/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;

import java.util.Properties;

/**
 * generic identifier class.
 *
 */
public abstract class Id implements UniversalId, PropertyAssignedAware {

    private final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

    private Long value;

    protected Properties resourcePathPlaceHolder;

    // default ctor needed for Class.newInstance in deserializer
    public Id() {
        // value = new Long(-1);
    }

    public Id(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public void setCloudantId(String id) {
        this.value = Long.parseLong(id);
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
        return String.valueOf(value);
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
        return value.equals(((Id)other).value);
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}
