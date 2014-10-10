/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import com.junbo.common.id.PIType;

/**
 * Provider Criteria.
 */
public class ProviderCriteria {
    private PIType piType;
    private String country;
    private String currency;
    private Long userId;
    private String cseType;

    public ProviderCriteria(PIType piType){
        this.piType = piType;
    }

    public PIType getPiType() {
        return piType;
    }

    public void setPiType(PIType piType) {
        this.piType = piType;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCseType() {
        return cseType;
    }

    public void setCseType(String cseType) {
        this.cseType = cseType;
    }

}
