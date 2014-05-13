/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.EntitlementDefinitionId;
import com.junbo.common.jackson.annotation.ItemId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Action containing information for events.
 */
public class Action {
    @ApiModelProperty(position = 1, required = true, value = "action type",
            allowableValues = "GRANT_ENTITLEMENT, DELIVER_PHYSICAL_GOODS, CREDIT_WALLET")
    private String type;
    @EntitlementDefinitionId
    @JsonProperty("entitlementDefinition")
    @ApiModelProperty(position = 2, required = true, value = "entitlement definition")
    private Long entitlementDefId;
    @ApiModelProperty(position = 3, required = true, value = "properties")
    @ItemId
    private Long itemId;
    @CurrencyId
    @ApiModelProperty(position = 4, required = true, value = "Stored value credit currency")
    private String storedValueCurrency;
    @ApiModelProperty(position = 5, required = true, value = "Stored value credit amount")
    private BigDecimal storedValueAmount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEntitlementDefId() {
        return entitlementDefId;
    }

    public void setEntitlementDefId(Long entitlementDefId) {
        this.entitlementDefId = entitlementDefId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
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
}
