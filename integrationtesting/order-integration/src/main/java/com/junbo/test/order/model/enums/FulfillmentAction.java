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
    REVERSE_FULFILL("REVERSE_FULFILL"),
    PREORDER("PREORDER");

    private String name;

    private FulfillmentAction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
