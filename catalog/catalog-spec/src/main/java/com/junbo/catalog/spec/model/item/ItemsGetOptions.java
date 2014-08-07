/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.OrganizationId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Items get options.
 */
public class ItemsGetOptions extends PageableGetOptions {
    @QueryParam("itemId")
    private Set<String> itemIds;
    @QueryParam("type")
    private String type;
    @QueryParam("genreId")
    private String genre;
    @QueryParam("hostItemId")
    private String hostItemId;
    @QueryParam("developerId")
    private OrganizationId ownerId;
    @QueryParam("packageName")
    private String packageName;
    @QueryParam("q")
    private String query;

    public Set<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<String> itemIds) {
        this.itemIds = itemIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getHostItemId() {
        return hostItemId;
    }

    public void setHostItemId(String hostItemId) {
        this.hostItemId = hostItemId;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
