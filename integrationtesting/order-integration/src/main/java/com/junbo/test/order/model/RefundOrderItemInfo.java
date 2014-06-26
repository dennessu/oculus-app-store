/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import java.math.BigDecimal;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class RefundOrderItemInfo {
    private String offerId;
    private int quantity;
    private BigDecimal refundAmount;
    private BigDecimal refundTax;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getRefundTax() {
        return refundTax;
    }

    public void setRefundTax(BigDecimal refundTax) {
        this.refundTax = refundTax;
    }
}
