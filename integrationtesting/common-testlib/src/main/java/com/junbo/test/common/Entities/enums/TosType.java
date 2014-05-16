/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.enums;

/**
 * Enum for Tos type.
 *
 * @author Jason
 * Created on 5/14/2014.
 */
public enum TosType {
    EULA("EULA"),
    TOS("TOS"),
    PP("PP");

    private String type;

    private TosType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getRandom(){
        return values()[(int) (Math.random() * values().length)].getType();
    }

    public static TosType getRandomType(){
        return values()[(int) (Math.random() * values().length)];
    }

}
