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
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.jackson.serializer.CascadeResource;

import java.util.Date;
import java.util.UUID;

/**
 * payment instrument model.
 */
public class PaymentInstrument {
    private Long id;
    private UUID trackingUuid;
    @JsonProperty("user")
    @UserId
    private Long userId;
    private boolean isValidated;
    private Date lastValidatedTime;
    private String isDefault;
    @PaymentInstrumentTypeId
    private String type;
    private String accountName;
    private String accountNum;
    private Address address;
    private Phone phone;
    private String email;
    private String relationToHolder;
    private CreditCardRequest creditCardRequest;
    //response:
    private String status;

    public Long getId() {
        return id;
    }
    @PaymentInstrumentId
    @JsonProperty("self")
    public void setId(Long id) {
        this.id = id;
    }

    @PaymentInstrumentId
    @JsonProperty("self")
    public CascadeResource getCascadePaymentInstrumentId() {
        return new CascadeResource(id, new Object[]{userId, id});
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @JsonProperty
    public Long getUserId() {
        return userId;
    }
    //TODO: add @JsonIgnore
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CreditCardRequest getCreditCardRequest() {
        return creditCardRequest;
    }

    public void setCreditCardRequest(CreditCardRequest creditCardRequest) {
        this.creditCardRequest = creditCardRequest;
    }

    @JsonIgnore
    public boolean getIsValidated() {
        return isValidated;
    }
    @JsonProperty
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastValidatedTime() {
        return lastValidatedTime;
    }

    public void setLastValidatedTime(Date lastValidatedTime) {
        this.lastValidatedTime = lastValidatedTime;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
