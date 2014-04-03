/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by fzhang on 4/2/2014.
 */
public class SubledgerParam {

    @QueryParam("sellerId")
    private UserId sellerId;

    @QueryParam("status")
    private String status;

    @QueryParam("fromDate")
    private Date fromDate;

    @QueryParam("toDate")
    private Date toDate;

    public UserId getSellerId() {
        return sellerId;
    }

    public void setSellerId(UserId sellerId) {
        this.sellerId = sellerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
