/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.payment;

import com.junbo.payment.db.entity.GenericEntity;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.PaymentType;

import java.math.BigDecimal;

import javax.persistence.*;


/**
 * payment entity.
 */
@Entity
@Table(name = "payment")
public class PaymentEntity extends GenericEntity {

    @Id
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_type_id")
    private PaymentType type;

    @Column(name = "currency_code")
    private String currency;

    @Column(name = "net_amount")
    private BigDecimal netAmount;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "business_descriptor")
    private String businessDescriptor;

    @Column(name = "payment_instrument_id")
    private Long paymentInstrumentId;

    @Column(name = "payment_provider_id")
    private Integer paymentProviderId;

    @Column(name = "merchant_account_id")
    private Integer merchantAccountId;

    @Column(name = "payment_status_id")
    private PaymentStatus status;

    @Column(name = "external_token")
    private String externalToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getBusinessDescriptor() {
        return businessDescriptor;
    }

    public void setBusinessDescriptor(String businessDescriptor) {
        this.businessDescriptor = businessDescriptor;
    }

    public Long getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(Long paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public Integer getPaymentProviderId() {
        return paymentProviderId;
    }

    public void setPaymentProviderId(Integer paymentProviderId) {
        this.paymentProviderId = paymentProviderId;
    }

    public Integer getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setMerchantAccountId(Integer merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }
}
