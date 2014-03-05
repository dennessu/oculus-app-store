/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.payment;


import com.junbo.payment.spec.enums.PIType;

import javax.persistence.*;
import java.util.Date;


/**
 * merchant entity.
 */
@Entity
@Table(name = "merchant_account")
public class MerchantAccountEntity {

    @Id
    @Column(name = "merchant_account_id")
    private Integer merchantAccountId;

    @Column(name = "merchant_account_ref")
    private String merchantAccountRef;

    @Column(name = "payment_provider_id")
    private Integer paymentProviderId;

    @Column(name = "payment_instrument_type_id")
    private PIType piType;

    @Column(name = "currency_code")
    private String currency;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "created_by")
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

    public Integer getPaymentProviderId() {
        return paymentProviderId;
    }

    public void setPaymentProviderId(Integer paymentProviderId) {
        this.paymentProviderId = paymentProviderId;
    }

    public PIType getPiType() {
        return piType;
    }

    public void setPiType(PIType piType) {
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
