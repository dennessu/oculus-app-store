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

    private BigDecimal ewalletAmount;
    private String ewalletCurrency;

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

    public BigDecimal getEwalletAmount() {
        return ewalletAmount;
    }

    public void setEwalletAmount(BigDecimal ewalletAmount) {
        this.ewalletAmount = ewalletAmount;
    }

    public String getEwalletCurrency() {
        return ewalletCurrency;
    }

    public void setEwalletCurrency(String ewalletCurrency) {
        this.ewalletCurrency = ewalletCurrency;
    }
}
