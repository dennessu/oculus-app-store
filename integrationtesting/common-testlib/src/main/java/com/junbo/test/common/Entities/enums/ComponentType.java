/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.enums;

/**
 * Created by weiyu_000 on 7/9/14.
 */

/**
 * Enum for Component name.
 *
 * @author Yunlongzhao
 */
public enum ComponentType {
    IDENTITY("identity"),
    CATALOG("catalog"),
    COMMERCE("commerce");

    private String name;

    private ComponentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}