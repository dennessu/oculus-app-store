/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for preorder action.
 *
 * @author Yunlong
 *         Created on 6/5/2014.
 */

public enum PreorderAction{
    PREORDER("PREORDER"),
    CHARGE("CHARGE"),
    FULFILL("FULFILL"),
    PRE_RELEASE_NOTIFY("PRE_RELEASE_NOTIFY"),
    RELEASE_NOTIFY("RELEASE_NOTIFY");

    private String name;

    private PreorderAction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
