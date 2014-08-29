/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by LinYi on 2014/8/28.
 */
public class ItemSnapshot extends ResourceMetaForDualWrite<Long> {
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long offerSnapshotId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOfferSnapshotId() {
        return offerSnapshotId;
    }

    public void setOfferSnapshotId(Long offerSnapshotId) {
        this.offerSnapshotId = offerSnapshotId;
    }

    @ApiModelProperty(required = true, position = 10, value = "Link to the item resource.")
    private ItemId item;

    @ApiModelProperty(required = true, position = 20, value = "Link to the itemRevision resource.")
    private ItemRevisionId itemRevision;

    public ItemId getItem() {
        return item;
    }

    public void setItem(ItemId item) {
        this.item = item;
    }

    public ItemRevisionId getItemRevision() {
        return itemRevision;
    }

    public void setItemRevision(ItemRevisionId itemRevision) {
        this.itemRevision = itemRevision;
    }
}
