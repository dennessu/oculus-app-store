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
import java.util.Collection;
import java.util.List;

/**
 * Items get options.
 */
public class ItemRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("itemId")
    private Collection<ItemId> itemIds;
    @QueryParam("revisionId")
    private Collection<ItemRevisionId> revisionIds;
    @QueryParam("status")
    private String status;
    @QueryParam("timeInMillis")
    private Long timestamp;

    public Collection<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Collection<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    public Collection<ItemRevisionId> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(List<ItemRevisionId> revisionIds) {
        this.revisionIds = revisionIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
