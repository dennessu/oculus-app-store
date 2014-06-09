/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.OrganizationId;

import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by fzhang on 4/2/2014.
 */
public class SubledgerParam {

    @QueryParam("payOutStatus")
    private String payOutStatus;

    @QueryParam("sellerId")
    private OrganizationId sellerId;

    @QueryParam("fromDate")
    private Date fromDate;

    @QueryParam("toDate")
    private Date toDate;

    public OrganizationId getSellerId() {
        return sellerId;
    }

    public void setSellerId(OrganizationId sellerId) {
        this.sellerId = sellerId;
    }

    public String getPayOutStatus() {
        return payOutStatus;
    }

    public void setPayOutStatus(String payOutStatus) {
        this.payOutStatus = payOutStatus;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
