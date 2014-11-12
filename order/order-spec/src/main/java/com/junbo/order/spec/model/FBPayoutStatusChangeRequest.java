/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.PayoutId;

import java.math.BigDecimal;

/**
 * The FBPayoutStatusChangeRequest class.
 */
public class FBPayoutStatusChangeRequest {

    /**
     * RECEIVE: Facebook Finance receive the payout (happen when it first generated)
     * PAID: Facebook send the money out
     * HOLD: the payout is held (I.e. Less than threshold, fail OFAC check, etc)
     * REJECT: payout rejected by bank (usually bank info incorrect)
     */
    public enum FBPayoutStatus {
        RECEIVE,
        PAID,
        HOLD,
        REJECT
    }

    private PayoutId payoutId;

    private String fbPayoutId;

    private String financialId;

    private String startDate;

    private String endDate;

    private String payoutCurrency;

    private BigDecimal payoutAmount;

    private String status;

    private String reason;

    public PayoutId getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(PayoutId payoutId) {
        this.payoutId = payoutId;
    }

    public String getFbPayoutId() {
        return fbPayoutId;
    }

    public void setFbPayoutId(String fbPayoutId) {
        this.fbPayoutId = fbPayoutId;
    }

    public String getFinancialId() {
        return financialId;
    }

    public void setFinancialId(String financialId) {
        this.financialId = financialId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPayoutCurrency() {
        return payoutCurrency;
    }

    public void setPayoutCurrency(String payoutCurrency) {
        this.payoutCurrency = payoutCurrency;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(BigDecimal payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
