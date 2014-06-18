/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.payment;

import com.junbo.payment.db.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * payment entity.
 */
@Entity
@Table(name = "payment")
public class PaymentTransactionEntity extends GenericEntity {

    @Id
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_type_id")
    private Short typeId;

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
    private Short statusId;

    @Column(name = "external_token")
    private String externalToken;

    @Column(name = "billing_ref_id")
    private String billingRefId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return paymentInstrumentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Short getTypeId() {
        return typeId;
    }

    public void setTypeId(Short typeId) {
        this.typeId = typeId;
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

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }

    public String getBillingRefId() {
        return billingRefId;
    }

    public void setBillingRefId(String billingRefId) {
        this.billingRefId = billingRefId;
    }
}
