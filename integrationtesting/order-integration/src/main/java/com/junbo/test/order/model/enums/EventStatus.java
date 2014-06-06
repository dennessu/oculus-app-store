/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for event status.
 *
 * @author Yunlong
 *         Created on 6/5/2014.
 */

public enum EventStatus{
    OPEN("OPEN"),
    PROCESSING("PROCESSING"),
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    ERROR("ERROR");

    private String name;

    private EventStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
