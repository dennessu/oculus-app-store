/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

/**
 * Enum for entitlement type.
 *
 * @author Jason
 */
public enum EntitlementType {
    ALLOW_IN_APP("ALLOW_IN_APP"),
    DEVELOPER("DEVELOPER"),
    DOWNLOAD("DOWNLOAD"),
    RUN("RUN");

    private String type;

    private EntitlementType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getRandom(){
        return values()[(int) (Math.random() * values().length)].getType();
    }

    public static EntitlementType getRandomType(){
        return values()[(int) (Math.random() * values().length)];
    }

}
