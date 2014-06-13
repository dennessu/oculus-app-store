/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.Price;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.ItemId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Action containing information for events.
 */
public class Action {
    @ApiModelProperty(position = 1, required = true, value = "action type",
            allowableValues = "GRANT_ENTITLEMENT, DELIVER_PHYSICAL_GOODS, CREDIT_WALLET, CHARGE")
    private String type;
    @ApiModelProperty(position = 2, required = true, value = "The condition to perform the action")
    private ActionCondition condition;
    @ApiModelProperty(position = 3, required = true, value = "properties")
    @ItemId
    private String itemId;
    @CurrencyId
    @ApiModelProperty(position = 4, required = true, value = "Stored value credit currency")
    private String storedValueCurrency;
    @ApiModelProperty(position = 5, required = true, value = "Stored value credit amount")
    private BigDecimal storedValueAmount;
    @ApiModelProperty(position = 6, required = true, value = "Use count for consumable")
    private Integer useCount;
    @ApiModelProperty(position = 7, required = true, value = "Subscription price when specific event occurs")
    private Price price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ActionCondition getCondition() {
        return condition;
    }

    public void setCondition(ActionCondition condition) {
        this.condition = condition;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getStoredValueCurrency() {
        return storedValueCurrency;
    }

    public void setStoredValueCurrency(String storedValueCurrency) {
        this.storedValueCurrency = storedValueCurrency;
    }

    public BigDecimal getStoredValueAmount() {
        return storedValueAmount;
    }

    public void setStoredValueAmount(BigDecimal storedValueAmount) {
        this.storedValueAmount = storedValueAmount;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
