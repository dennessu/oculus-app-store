/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

/**
 * Enum for catalog item type.
 *
 * @author Jason
 */
public enum CatalogItemType {
    PHYSICAL("PHYSICAL"),
    APP("APP"),
    DOWNLOADED_ADDITION("DOWNLOADED_ADDITION"),
    STORED_VALUE("STORED_VALUE"),
    SUBSCRIPTION("SUBSCRIPTION"),
    PERMANENT_UNLOCK("PERMANENT_UNLOCK"),
    CONSUMABLE_UNLOCK("CONSUMABLE_UNLOCK"),;

    private String itemType;

    private CatalogItemType(String type) {
        this.itemType = type;
    }

    public String getItemType() {
        return itemType;
    }

    public static CatalogItemType getRandom(){
        return values()[(int) (Math.random() * values().length)];
    }

    public static CatalogItemType getByIndex(int index) {
        return values()[index];
    }

}

