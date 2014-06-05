/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for item type.
 *
 * @author Yunlongzhao
 *         Created on 6/5/2014.
 */
public enum ItemType {
    DIGITAL("DIGITAL"),
    PHYSICAL("PHYSICAL"),
    STORED_VALUE("STORED_VALUE");

    private String name;

    private ItemType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
