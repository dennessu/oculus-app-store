/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.id.ItemId;

import javax.ws.rs.QueryParam;

/**
 * The GetDetailsRequest class.
 */
public class DetailsRequest {

    @QueryParam("itemId")
    private ItemId itemId;

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }
}
