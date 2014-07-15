/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.spec.model;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;

import java.util.Date;

/**
 * Entitlement.
 */
public class Entitlement {
    private EntitlementId id;
    private ItemId itemId;
    private Date grantTime;
    private Date expirationTime;
    private String type;

    public EntitlementId getId() {
        return id;
    }

    public void setId(EntitlementId id) {
        this.id = id;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
