/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.common.jackson.annotation.ItemId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Restriction to buy an offer.
 */
public class Restriction {
    @ApiModelProperty(position = 1, required = true, value = "Limit per customer")
    private Integer limitPerCustomer;
    @ApiModelProperty(position = 2, required = true, value = "Limit per order")
    private Integer limitPerOrder;

    @ItemId
    @ApiModelProperty(position = 3, required = true, value = "Precondition items")
    private List<String> preconditionItems;
    @ItemId
    @ApiModelProperty(position = 4, required = true, value = "Exclusion items")
    private List<String> exclusionItems;

    public Integer getLimitPerCustomer() {
        return limitPerCustomer;
    }

    public void setLimitPerCustomer(Integer limitPerCustomer) {
        this.limitPerCustomer = limitPerCustomer;
    }

    public Integer getLimitPerOrder() {
        return limitPerOrder;
    }

    public void setLimitPerOrder(Integer limitPerOrder) {
        this.limitPerOrder = limitPerOrder;
    }

    public List<String> getPreconditionItems() {
        return preconditionItems;
    }

    public void setPreconditionItems(List<String> preconditionItems) {
        this.preconditionItems = preconditionItems;
    }

    public List<String> getExclusionItems() {
        return exclusionItems;
    }

    public void setExclusionItems(List<String> exclusionItems) {
        this.exclusionItems = exclusionItems;
    }
}
