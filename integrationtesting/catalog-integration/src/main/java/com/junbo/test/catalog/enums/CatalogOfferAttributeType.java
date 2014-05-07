/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

/**
 * Enum for catalog offer attribute type.
 *
 * @author Jason
 */
public enum CatalogOfferAttributeType {
    CATEGORY("CATEGORY");

    private String type;

    private CatalogOfferAttributeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getRandom(){
        return values()[(int) (Math.random() * values().length)].getType();
    }

}
