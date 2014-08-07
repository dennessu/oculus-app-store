/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Item class.
 */
public class Item {

    private String itemId;
    private String type;
    private String packageName;
    private String externalId;
    private Boolean consumable;

    @JsonIgnore
    private Object catalogItem;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }

    public Object getCatalogItem() {
        return catalogItem;
    }

    public void setCatalogItem(Object catalogItem) {
        this.catalogItem = catalogItem;
    }
}
