/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.PaymentInstrumentId;
import com.junbo.common.jackson.annotation.PaymentInstrumentTypeId;
import com.junbo.payment.common.InnerFilter;

import java.util.Date;
import java.util.UUID;

/**
 * payment instrument model.
 */
public class PaymentInstrument {
    @PaymentInstrumentId
    @JsonProperty("self")
    private PIId id;
    @JsonIgnore
    private UUID trackingUuid;
    private boolean isValidated;
    private Date lastValidatedTime;
    @PaymentInstrumentTypeId
    private String type;
    private String accountName;
    private String accountNum;
    private Integer rev;
    private Address address;
    private String phoneNum;
    private String email;
    private String relationToHolder;
    @InnerFilter
    private CreditCardRequest creditCardRequest;
    private WalletRequest walletRequest;
    //response:
    private Boolean isActive;

    public PIId getId() {
        return id;
    }

    public void setId(PIId id) {
        this.id = id;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public CreditCardRequest getCreditCardRequest() {
        return creditCardRequest;
    }

    public void setCreditCardRequest(CreditCardRequest creditCardRequest) {
        this.creditCardRequest = creditCardRequest;
    }

    public boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(boolean validated) {
        this.isValidated = validated;
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

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRelationToHolder() {
        return relationToHolder;
    }

    public void setRelationToHolder(String relationToHolder) {
        this.relationToHolder = relationToHolder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getLastValidatedTime() {
        return lastValidatedTime;
    }

    public void setLastValidatedTime(Date lastValidatedTime) {
        this.lastValidatedTime = lastValidatedTime;
    }

    public WalletRequest getWalletRequest() {
        return walletRequest;
    }

    public void setWalletRequest(WalletRequest walletRequest) {
        this.walletRequest = walletRequest;
    }
}
