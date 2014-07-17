/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.payment.spec.model.PaymentInstrument;

import java.math.BigDecimal;

/**
 * The PaymentInstrument class.
 */
public class Instrument {

    private PaymentInstrumentId instrumentId;
    private UserId userId;
    private String type;
    private String accountName;
    private String accountNum;
    private String expireDate;
    private String encryptedCvmCode;
    private String creditCardType;
    private String storedValueCurrency;
    private BigDecimal storedValueBalance;
    private UserPersonalInfo billingAddress;
    private UserPersonalInfo phoneNumber;
    private UserPersonalInfo email;
    @JsonIgnore
    private PaymentInstrument paymentInstrument;

    public PaymentInstrumentId getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(PaymentInstrumentId instrumentId) {
        this.instrumentId = instrumentId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getEncryptedCvmCode() {
        return encryptedCvmCode;
    }

    public void setEncryptedCvmCode(String encryptedCvmCode) {
        this.encryptedCvmCode = encryptedCvmCode;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
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

    public UserPersonalInfo getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(UserPersonalInfo billingAddress) {
        this.billingAddress = billingAddress;
    }

    public UserPersonalInfo getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(UserPersonalInfo phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserPersonalInfo getEmail() {
        return email;
    }

    public void setEmail(UserPersonalInfo email) {
        this.email = email;
    }

    public PaymentInstrument getPaymentInstrument() {
        return paymentInstrument;
    }

    public void setPaymentInstrument(PaymentInstrument paymentInstrument) {
        this.paymentInstrument = paymentInstrument;
    }
}
