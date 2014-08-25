/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.id.ItemId;

import javax.ws.rs.QueryParam;

/**
 * The AddReviewRequest class.
 */
public class DeliveryRequest {

    @QueryParam("itemId")
    private ItemId itemId;

    @QueryParam("desiredVersionCode")
    private String desiredVersionCode;

    @QueryParam("currentVersionCode")
    private String currentVersionCode;

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public String getDesiredVersionCode() {
        return desiredVersionCode;
    }

    public void setDesiredVersionCode(String desiredVersionCode) {
        this.desiredVersionCode = desiredVersionCode;
    }

    public String getCurrentVersionCode() {
        return currentVersionCode;
    }

    public void setCurrentVersionCode(String currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }
}
