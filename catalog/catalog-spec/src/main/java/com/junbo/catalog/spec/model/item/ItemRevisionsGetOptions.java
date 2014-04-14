/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Items get options.
 */
public class ItemRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("itemId")
    private List<ItemId> itemIds;
    @QueryParam("revisionId")
    private List<ItemRevisionId> revisionIds;
    @QueryParam("type")
    private String type;
    @QueryParam("status")
    private String status;

    public List<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    public List<ItemRevisionId> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(List<ItemRevisionId> revisionIds) {
        this.revisionIds = revisionIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
