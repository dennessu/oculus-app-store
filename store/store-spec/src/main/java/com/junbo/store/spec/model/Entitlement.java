/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.store.spec.model.browse.document.Item;

import java.util.Date;

/**
 * Entitlement used for IAP.
 */
public class Entitlement {

    private EntitlementId self;

    @JsonIgnore
    private UserId user;

    @JsonIgnore
    private String developerPayload;

    private Date createdTime;

    private String entitlementType;

    private ItemId item;

    private String itemType;

    private Item itemDetails;

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
    }

    public ItemId getItem() {
        return item;
    }

    public void setItem(ItemId item) {
        this.item = item;
    }

    public EntitlementId getSelf() {
        return self;
    }

    public void setSelf(EntitlementId self) {
        this.self = self;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Item getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(Item itemDetails) {
        this.itemDetails = itemDetails;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getEntitlementType() {
        return entitlementType;
    }

    public void setEntitlementType(String entitlementType) {
        this.entitlementType = entitlementType;
    }

}
