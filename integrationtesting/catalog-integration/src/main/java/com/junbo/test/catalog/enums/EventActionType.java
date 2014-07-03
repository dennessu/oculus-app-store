/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

/**
 * Enum for offer event action type.
 *
 * @author Jason
 */
public enum EventActionType {
    GRANT_ENTITLEMENT("GRANT_ENTITLEMENT"),
    DELIVER_PHYSICAL_GOODS("DELIVER_PHYSICAL_GOODS"),
    CHARGE("CHARGE"),
    CREDIT_WALLET("CREDIT_WALLET");

    private String type;

    private EventActionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getRandom(){
        return values()[(int) (Math.random() * values().length)].getType();
    }

    public static EventActionType getRandomType(){
        return values()[(int) (Math.random() * values().length)];
    }
}
