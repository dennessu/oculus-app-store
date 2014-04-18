/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.spec.model;

/**
 * Created by fzhang on 14-3-11.
 */
public class BillingEvent extends BaseEventModel {

    private String balanceId;

    private Long orderId;

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

}
