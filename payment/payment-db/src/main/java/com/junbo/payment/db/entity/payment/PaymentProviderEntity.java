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
 * payment provider entity.
 */
@Entity
@Table(name = "payment_provider")
public class PaymentProviderEntity {

    @Id
    @Column(name = "payment_provider_id")
    private Integer id;

    @Column(name = "payment_provider_name")
    private String providerName;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
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
