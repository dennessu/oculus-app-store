/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemId;
import com.junbo.common.jackson.annotation.AttributeId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Items get options.
 */
public class ItemsGetOptions extends PageableGetOptions {
    @QueryParam("id")
    private List<ItemId> itemIds;
    @QueryParam("curated")
    private Boolean curated;
    @QueryParam("type")
    private String type;
    @AttributeId
    @QueryParam("genre")
    private Long genre;

    public List<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getGenre() {
        return genre;
    }

    public void setGenre(Long genre) {
        this.genre = genre;
    }
}
