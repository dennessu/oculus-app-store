/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

import com.junbo.common.error.AppCommonErrors;

import java.util.Properties;

/**
 * generic identifier class.
 *
 */
public abstract class CloudantId implements UniversalId {

    private String value;

    protected Properties resourcePathPlaceHolder;

    // default ctor needed for Class.newInstance in deserializer
    public CloudantId() {
    }

    public CloudantId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long asLong() {
        return Long.parseLong(value);
    }

    public void setLongValue(Long value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = value.toString();
        }
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

    public static void validate(String field, String id) {
        if (id.startsWith("_")) {
            throw AppCommonErrors.INSTANCE.invalidId(field, id).exception();
        }
    }

    public static void validate(String id) {
        if (id.startsWith("_")) {
            throw AppCommonErrors.INSTANCE.invalidId("id", id).exception();
        }
    }
}
