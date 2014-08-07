/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import javax.ws.rs.QueryParam;

/**
 * The EntitlementsGetRequest class.
 */
public class EntitlementsGetRequest {

    @QueryParam("packageName")
    private String packageName;

    @QueryParam("packageVersion")
    private String packageVersion;

    @QueryParam("packageSignatureHash")
    private String packageSignatureHash;

    @QueryParam("itemType")
    String itemType;

    @QueryParam("entitlementType")
    String entitlementType;

    @QueryParam("isActive")
    private Boolean isActive;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
