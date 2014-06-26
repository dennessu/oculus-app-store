/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.PageableGetOptions;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Items get options.
 */
public class ItemRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("itemId")
    private Set<String> itemIds;
    @QueryParam("revisionId")
    private Set<String> revisionIds;
    @QueryParam("status")
    private String status;
    @QueryParam("timeInMillis")
    private Long timestamp;
    @QueryParam("locale")
    private String locale;

    public Set<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<String> itemIds) {
        this.itemIds = itemIds;
    }

    public Set<String> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(Set<String> revisionIds) {
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
