/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.store.spec.model.Address;

import java.math.BigDecimal;

/**
 * The PaymentInstrument class.
 */
public class Instrument {

    private PaymentInstrumentId self;
    private Boolean isDefault;
    private String type;
    private String accountName;
    private String accountNum;
    private String creditCardType;
    private String expireDate;
    private BigDecimal storedValueBalance;
    private String storedValueCurrency;
    private Address billingAddress;
    private String phoneNumber;

    @JsonIgnore
    private PaymentInstrument paymentInstrument;

    public PaymentInstrumentId getSelf() {
        return self;
    }

    public void setSelf(PaymentInstrumentId self) {
        this.self = self;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getStoredValueCurrency() {
        return storedValueCurrency;
    }

    public void setStoredValueCurrency(String storedValueCurrency) {
        this.storedValueCurrency = storedValueCurrency;
    }

    public BigDecimal getStoredValueBalance() {
        return storedValueBalance;
    }

    public void setStoredValueBalance(BigDecimal storedValueBalance) {
        this.storedValueBalance = storedValueBalance;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PaymentInstrument getPaymentInstrument() {
        return paymentInstrument;
    }

    public void setPaymentInstrument(PaymentInstrument paymentInstrument) {
        this.paymentInstrument = paymentInstrument;
    }
}
