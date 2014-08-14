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
import com.junbo.store.spec.model.iap.IAPEntitlement;

/**
 * Entitlement used for IAP.
 */
public class Entitlement {

    private EntitlementId self;

    @JsonIgnore
    private UserId user;

    private String entitlementType;

    private ItemId item;

    private String itemType;

    private IAPEntitlement iapEntitlement;

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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getEntitlementType() {
        return entitlementType;
    }

    public void setEntitlementType(String entitlementType) {
        this.entitlementType = entitlementType;
    }

    public IAPEntitlement getIapEntitlement() {
        return iapEntitlement;
    }

    public void setIapEntitlement(IAPEntitlement iapEntitlement) {
        this.iapEntitlement = iapEntitlement;
    }
}
