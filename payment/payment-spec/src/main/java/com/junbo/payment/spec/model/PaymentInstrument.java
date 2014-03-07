/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.PaymentInstrumentId;
import com.junbo.common.jackson.annotation.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * payment instrument model.
 */
public class PaymentInstrument {
    @Null
    @JsonProperty("self")
    @PaymentInstrumentId
    private Long id;
    private UUID trackingUuid;
    @UserId
    private Long userId;
    @NotNull
    @UserId
    private List<Long> admins;
    private boolean isValidated;
    private Date lastValidatedTime;
    private String isDefault;
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

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }
    @JsonIgnore
    public Long getUserId() {
        return userId;
    }
    @JsonIgnore
    public void setUserId(Long userId) {
        this.userId = userId;
        this.setAdmins(Arrays.asList(userId));
    }

    public List<Long> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Long> admins) {
        this.admins = admins;
        this.userId = admins.get(0);
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
