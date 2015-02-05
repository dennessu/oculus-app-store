/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.jobs.transaction.model;

import com.junbo.common.id.OrderId;
import com.junbo.common.util.IdFormatter;

/**
 * Created by acer on 2015/1/29.
 */
public class DiscrepancyRecord {

    private OrderId orderId;

    private DiscrepancyReason discrepancyReason;

    private FacebookTransaction facebookTransaction;

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public DiscrepancyReason getDiscrepancyReason() {
        return discrepancyReason;
    }

    public void setDiscrepancyReason(DiscrepancyReason discrepancyReason) {
        this.discrepancyReason = discrepancyReason;
    }

    public FacebookTransaction getFacebookTransaction() {
        return facebookTransaction;
    }

    public void setFacebookTransaction(FacebookTransaction facebookTransaction) {
        this.facebookTransaction = facebookTransaction;
    }

    public String getOrderIdEncoded() {
        if (orderId == null) {
            return null;
        }
        return IdFormatter.encodeId(orderId);
    }
}
