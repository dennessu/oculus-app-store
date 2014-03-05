/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

import java.io.Serializable;

/**
 * generic identifier class.
 *
 */
public abstract class Id extends Object implements Serializable {

    private Long value;

    // default ctor needed for Class.newInstance in deserializer
    public Id() {
    }

    public Id(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
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
}
