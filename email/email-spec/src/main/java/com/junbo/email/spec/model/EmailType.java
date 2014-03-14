/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.junbo.common.util.Identifiable;

/**
 * Email Type.
 */
public enum EmailType implements Identifiable<Short> {
    COMMERCE((short)1);

    EmailType(Short id) {
        this.id = id;
    }

    private final short id;

    public Short getId() {
        return id;
    }
}
