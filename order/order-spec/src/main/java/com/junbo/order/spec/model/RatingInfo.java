/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 2/14/14.
 */
public class RatingInfo {
    private BigDecimal totalAmount;
    private BigDecimal totalShippingFee;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFeeDiscount;
    private BigDecimal totalPreorderAmount;
    private BigDecimal totalPreorderTax;
    private Date honorUntilTime;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(BigDecimal totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public Boolean getIsTaxInclusive() {
        return isTaxInclusive;
    }

    public void setIsTaxInclusive(Boolean isTaxInclusive) {
        this.isTaxInclusive = isTaxInclusive;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalShippingFeeDiscount() {
        return totalShippingFeeDiscount;
    }

    public void setTotalShippingFeeDiscount(BigDecimal totalShippingFeeDiscount) {
        this.totalShippingFeeDiscount = totalShippingFeeDiscount;
    }

    public BigDecimal getTotalPreorderAmount() {
        return totalPreorderAmount;
    }

    public void setTotalPreorderAmount(BigDecimal totalPreorderAmount) {
        this.totalPreorderAmount = totalPreorderAmount;
    }

    public BigDecimal getTotalPreorderTax() {
        return totalPreorderTax;
    }

    public void setTotalPreorderTax(BigDecimal totalPreorderTax) {
        this.totalPreorderTax = totalPreorderTax;
    }

    public Date getHonorUntilTime() {
        return honorUntilTime;
    }

    public void setHonorUntilTime(Date honorUntilTime) {
        this.honorUntilTime = honorUntilTime;
    }
}
