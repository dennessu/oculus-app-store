/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for subledger item status.
 *
 * @author Yunlong
 *         Created on 6/5/2014.
 */

public enum SubledgerItemStatus {
    PENDING("PENDING"),
    PROCESSED("PROCESSED");

    private String name;

    private SubledgerItemStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
