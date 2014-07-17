/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * The EntitlementsGetRequest class.
 */
public class EntitlementsGetRequest {

    @QueryParam("packageName")
    private String packageName;

    @QueryParam("userId")
    private UserId userId;

    @QueryParam("itemType")
    String itemType;

    @QueryParam("entitlementType")
    String entitlementType;

    @QueryParam("isIAP")
    private Boolean isIAP;

    @QueryParam("isActive")
    private Boolean isActive;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getEntitlementType() {
        return entitlementType;
    }

    public void setEntitlementType(String entitlementType) {
        this.entitlementType = entitlementType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Boolean getIsIAP() {
        return isIAP;
    }

    public void setIsIAP(Boolean isIAP) {
        this.isIAP = isIAP;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
