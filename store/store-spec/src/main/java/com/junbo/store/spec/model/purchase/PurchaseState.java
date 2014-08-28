/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.common.id.OrderId;

import java.util.Date;

/**
 * The PurchaseState class.
 */
public class PurchaseState {

    private OrderId order;

    private String country;

    private String locale;

    private String offer;

    private Date timestamp;

    private Boolean tosAccept;

    public OrderId getOrder() {
        return order;
    }

    public void setOrder(OrderId order) {
        this.order = order;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public Boolean getTosAccept() {
        return tosAccept;
    }

    public void setTosAccept(Boolean tosAccept) {
        this.tosAccept = tosAccept;
    }
}
