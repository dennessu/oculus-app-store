/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by fzhang on 2015/2/3.
 */
public class SubledgerAmount {

    private static final int SCALE = 5;

    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal totalPayoutAmount = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private Long totalQuantity = 0L;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalPayoutAmount() {
        return totalPayoutAmount;
    }

    public void setTotalPayoutAmount(BigDecimal totalPayoutAmount) {
        this.totalPayoutAmount = totalPayoutAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public SubledgerAmount add(SubledgerAmount subledgerAmount) {
        SubledgerAmount result = new SubledgerAmount();
        result.setTaxAmount(taxAmount.add(subledgerAmount.getTaxAmount()));
        result.setTotalPayoutAmount(totalPayoutAmount.add(subledgerAmount.getTotalPayoutAmount()));
        result.setTotalAmount(totalAmount.add(subledgerAmount.getTotalAmount()));
        result.setTotalQuantity(totalQuantity + subledgerAmount.getTotalQuantity());
        return result;
    }

    public SubledgerAmount substract(SubledgerAmount subledgerAmount) {
        SubledgerAmount result = new SubledgerAmount();
        result.setTaxAmount(taxAmount.subtract(subledgerAmount.getTaxAmount()));
        result.setTotalPayoutAmount(totalPayoutAmount.subtract(subledgerAmount.getTotalPayoutAmount()));
        result.setTotalAmount(totalAmount.subtract(subledgerAmount.getTotalAmount()));
        result.setTotalQuantity(totalQuantity - subledgerAmount.getTotalQuantity());
        return result;
    }

    public SubledgerAmount max(SubledgerAmount subledgerAmount) {
        SubledgerAmount result = new SubledgerAmount();
        result.setTaxAmount(taxAmount.max(subledgerAmount.getTaxAmount()));
        result.setTotalPayoutAmount(totalPayoutAmount.max(subledgerAmount.getTotalPayoutAmount()));
        result.setTotalAmount(totalAmount.max(subledgerAmount.getTotalAmount()));
        result.setTotalQuantity(Math.max(totalQuantity, subledgerAmount.getTotalQuantity()));
        return result;
    }

    public SubledgerAmount min(SubledgerAmount subledgerAmount) {
        SubledgerAmount result = new SubledgerAmount();
        result.setTaxAmount(taxAmount.min(subledgerAmount.getTaxAmount()));
        result.setTotalPayoutAmount(totalPayoutAmount.min(subledgerAmount.getTotalPayoutAmount()));
        result.setTotalAmount(totalAmount.min(subledgerAmount.getTotalAmount()));
        result.setTotalQuantity(Math.min(totalQuantity, subledgerAmount.getTotalQuantity()));
        return result;
    }

    public boolean anyPositive() {
        return (normalize(totalAmount).compareTo(BigDecimal.ZERO) > 0) ||
                (normalize(taxAmount).compareTo(BigDecimal.ZERO) > 0) ||
                (normalize(totalPayoutAmount).compareTo(BigDecimal.ZERO) > 0) ||
                        (totalQuantity > 0);
    }

    private static BigDecimal normalize(BigDecimal val) {
        return val.setScale(SCALE, RoundingMode.FLOOR);
    }
}
