/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.store.spec.model.Address;

import java.math.BigDecimal;

/**
 * The PaymentInstrument class.
 */
public class PaymentInstrument {

    private String paymentInstrumentId;

    private String type;

    private String accountName;

    private String accountNum;

    private String expireDate;

    private String encryptedCvmCode;

    private String storedValueCurrency;

    private BigDecimal storedValueBalance;

    private Address billingAddress;

    private String email;

    public String getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(String paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
