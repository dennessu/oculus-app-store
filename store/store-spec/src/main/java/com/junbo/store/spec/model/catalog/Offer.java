/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The offer class from catalog.
 */
public class Offer {

    private String id;
    private String title;
    private String description;
    private String price;
    private String originalPrice;
    private String currencyCode;
    private Boolean isConsumable;
    private String type;
    @JsonIgnore
    private Boolean hasStoreValueItem;
    @JsonIgnore
    private Boolean hasPhysicalItem;
    @JsonIgnore
    private Boolean isFree;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Boolean getIsConsumable() {
        return isConsumable;
    }

    public void setIsConsumable(Boolean isConsumable) {
        this.isConsumable = isConsumable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getHasStoreValueItem() {
        return hasStoreValueItem;
    }

    public void setHasStoreValueItem(Boolean hasStoreValueItem) {
        this.hasStoreValueItem = hasStoreValueItem;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Boolean getHasPhysicalItem() {
        return hasPhysicalItem;
    }

    public void setHasPhysicalItem(Boolean hasPhysicalItem) {
        this.hasPhysicalItem = hasPhysicalItem;
    }
}
