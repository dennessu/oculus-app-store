/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.Entities.enums.PaymentType;

/**
 * Created by Yunlong on 3/25/14.
 */
public abstract class PaymentInstrumentBase {
    private String pid;
    private String userId;
    private boolean isValidated;
    private boolean isDefault;
    private PaymentType type;
    private String accountName;
    private String accountNum;
    private Address address;
    private long phone;
    private long email;
    private String status;
    private String relationToHolder;
    private String phoneNumber;
    private Long billingAddressId;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPid() {
        return pid;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Long getBillingAddressId() {
        return billingAddressId;
    }

    public PaymentType getType() {
        return type;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public Address getAddress() {
        return address;
    }

    public long getPhone() {
        return phone;
    }

    public long getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getRelationToHolder() {
        return relationToHolder;
    }

    public void setRelationToHolder(String relationToHolder) {
        this.relationToHolder = relationToHolder;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setEmail(long email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBillingAddressId(Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }
}
