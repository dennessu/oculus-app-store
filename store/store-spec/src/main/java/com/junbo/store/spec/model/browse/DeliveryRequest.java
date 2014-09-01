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
    private Integer desiredVersionCode;

    @QueryParam("currentVersionCode")
    private Integer currentVersionCode;

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public Integer getDesiredVersionCode() {
        return desiredVersionCode;
    }

    public void setDesiredVersionCode(Integer desiredVersionCode) {
        this.desiredVersionCode = desiredVersionCode;
    }

    public Integer getCurrentVersionCode() {
        return currentVersionCode;
    }

    public void setCurrentVersionCode(Integer currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }
}
