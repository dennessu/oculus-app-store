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
    DEVELOPER("DEVELOPER"),
    DEVELOPER_SUBSCRIPTION("DEVELOPER_SUBSCRIPTION"),
    DOWNLOAD("DOWNLOAD"),
    DOWNLOAD_SUBSCRIPTION("DOWNLOAD_SUBSCRIPTION"),
    ONLINE_ACCESS("ONLINE_ACCESS"),
    ONLINE_ACCESS_SUBSCRIPTION("ONLINE_ACCESS_SUBSCRIPTION"),
    SUBSCRIPTION("SUBSCRIPTION");

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
