/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class FulfilmentHistory {
    private FulfilmentAction fulfilmentEvent;
    private List<String> entitlementIds;
    private BigDecimal walletAmount;
    private String rev;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;

    public FulfilmentAction getFulfilmentEvent() {
        return fulfilmentEvent;
    }

    public void setFulfilmentEvent(FulfilmentAction fulfilmentEvent) {
        this.fulfilmentEvent = fulfilmentEvent;
    }

    public List<String> getEntitlementIds() {
        return entitlementIds;
    }

    public void setEntitlementIds(List<String> entitlementIds) {
        this.entitlementIds = entitlementIds;
    }

    public BigDecimal getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(BigDecimal walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    //TODO Shipping details
    //TODO coupons
}
