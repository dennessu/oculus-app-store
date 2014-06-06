/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for fulfilment action.
 *
 * @author Yunlongzhao
 *         Created on 6/5/2014.
 */

public enum FulfillmentAction {
    FULFILL("FULFILL"),
    REVERSE("REVERSE"),
    RETURN("RETURN"),
    PREORDER("PREORDER"),
    SHIP("SHIP"),
    DELIVER("DELIVER"),
    REPLACE("REPLACE"),
    PENDING_FULFILL("PENDING_FULFILL");

    private String name;

    private FulfillmentAction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
