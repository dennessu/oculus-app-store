/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by fzhang on 4/2/2014.
 */
public class SubledgerParam {

    @QueryParam("payOutStatus")
    private String payOutStatus;

    @QueryParam("sellerId")
    private UserId sellerId;

    @QueryParam("fromDate")
    private Date fromDate;

    @QueryParam("toDate")
    private Date toDate;

    private OfferId offerId;

    private String country;

    private String currency;

    private Boolean toDateInclusive;

    public UserId getSellerId() {
        return sellerId;
    }

    public void setSellerId(UserId sellerId) {
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

    public OfferId getOfferId() {
        return offerId;
    }

    public void setOfferId(OfferId offerId) {
        this.offerId = offerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getToDateInclusive() {
        return toDateInclusive;
    }

    public void setToDateInclusive(Boolean toDateInclusive) {
        this.toDateInclusive = toDateInclusive;
    }
}
