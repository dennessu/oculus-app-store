/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.fusion;

import java.math.BigDecimal;

/**
 * Item.
 */
public class Item {
    private Long itemId;
    private String sku;

    private BigDecimal storedValueAmount;
    private String storedValueCurrency;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getStoredValueAmount() {
        return storedValueAmount;
    }

    public void setStoredValueAmount(BigDecimal storedValueAmount) {
        this.storedValueAmount = storedValueAmount;
    }

    public String getStoredValueCurrency() {
        return storedValueCurrency;
    }

    public void setStoredValueCurrency(String storedValueCurrency) {
        this.storedValueCurrency = storedValueCurrency;
    }
}
