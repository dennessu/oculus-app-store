/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

/**
 * Enum for entitlement type.
 * Created by jason on 7/18/2014.
 * @author Jason
 */
public enum PriceType {
    FREE("FREE"),
    TIERED("TIERED"),
    CUSTOM("CUSTOM");

    private String type;

    private PriceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getRandom(){
        return values()[(int) (Math.random() * values().length)].getType();
    }

    public static PriceType getRandomType(){
        return values()[(int) (Math.random() * values().length)];
    }
}
