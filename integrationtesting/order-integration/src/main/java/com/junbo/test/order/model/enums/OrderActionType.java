/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for order action type.
 *
 * @author Yunlongzhao
 *         Created on 6/5/2014.
 */

public enum OrderActionType {
    RATE("RATE"),
    CHARGE("CHARGE"),
    AUTHORIZE("AUTHORIZE"),
    FULFILL("FULFILL"),
    REFUND("REFUND"),
    CANCEL("CANCEL"),
    UPDATE("UPDATE"),
    PREORDER("PREORDER"),
    PARTIAL_REFUND("PARTIAL_REFUND"),
    CAPTURE("CAPTURE"),
    PARTIAL_CHARGE("PARTIAL_CHARGE");

    private String name;

    private OrderActionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
