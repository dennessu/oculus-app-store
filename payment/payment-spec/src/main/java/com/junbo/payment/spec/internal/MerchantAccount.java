/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import java.util.Date;

/**
 * Merchant Account.
 */
public class MerchantAccount {
    private Integer merchantAccountId;
    private String merchantAccountRef;
    private Integer tenantId;
    private Integer paymentProviderId;
    private Long piType;
    private String currency;
    private String countryCode;
    private Date createdTime;
    private String createdBy;

    public Integer getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setMerchantAccountId(Integer merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

    public String getMerchantAccountRef() {
        return merchantAccountRef;
    }

    public void setMerchantAccountRef(String merchantAccountRef) {
        this.merchantAccountRef = merchantAccountRef;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getPaymentProviderId() {
        return paymentProviderId;
    }

    public void setPaymentProviderId(Integer paymentProviderId) {
        this.paymentProviderId = paymentProviderId;
    }

    public Long getPiType() {
        return piType;
    }

    public void setPiType(Long piType) {
        this.piType = piType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
