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
public enum EventType {
    PURCHASE("PURCHASE"),
    CYCLE("CYCLE"),
    CANCEL("CANCEL");

    private String type;

    private EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getRandom(){
        return values()[(int) (Math.random() * values().length)].getType();
    }

    public static EventType getRandomType(){
        return values()[(int) (Math.random() * values().length)];
    }
}
