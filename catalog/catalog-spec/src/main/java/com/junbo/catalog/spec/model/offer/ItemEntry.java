/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.ItemId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Item entry in offer.
 */
public class ItemEntry {
    @ItemId
    @JsonProperty("item")
    @ApiModelProperty(position = 1, required = true, value = "item")
    private Long itemId;
    @ApiModelProperty(position = 1, required = true, value = "item quantity")
    private Integer quantity;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
