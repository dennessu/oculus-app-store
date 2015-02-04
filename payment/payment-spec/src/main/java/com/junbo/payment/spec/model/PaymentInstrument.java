/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.*;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.junbo.payment.common.FilterIn;
import com.junbo.payment.common.InnerFilter;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * payment instrument model.
 */
public class PaymentInstrument extends ResourceMetaForDualWrite<Long> {
    @ApiModelProperty(position = 1, required = true, value = "The id of payment instrument resource.")
    @PaymentInstrumentId
    @JsonProperty("self")
    private Long id;
    @ApiModelProperty(position = 2, required = true, value = "The ids of owner resource.")
    @UserId
    @JsonProperty("user")
    private Long userId;
    @NotNull
    @UserId
    @JsonIgnore
    private List<Long> admins;
    @JsonIgnore
    private UUID trackingUuid;
    @ApiModelProperty(position = 3, required = true, value = "[Client Immutable] Whether the PI is validated or not.")
    private boolean isValidated;
    @ApiModelProperty(position = 4, required = true, value = "Last validated time of the PI.")
    private Date lastValidatedTime;
    @ApiModelProperty(position = 5, required = true, value = "The payment instrument type resource.")
    @PaymentInstrumentTypeId
    private Long type;
    @ApiModelProperty(position = 6, required = true, value = "The type specific details of the PI.")
    @InnerFilter
    private TypeSpecificDetails typeSpecificDetails;
    @ApiModelProperty(position = 7, required = true, value = "The account name of the payment instrument.")
    @XSSFreeString
    private String accountName;
    @ApiModelProperty(position = 8, required = true, value = "The account of the payment instrument.")
    private String accountNumber;
    @ApiModelProperty(position = 9, required = true, value = "The label of the PI.")
    @XSSFreeString
    private String label;
    @JsonIgnore
    private Address address;
    @ApiModelProperty(position = 10, required = true, value = "The billing address resource of the PI.")
    @PersonalInfoId
    @JsonProperty("billingAddress")
    private Long billingAddressId;
    @ApiModelProperty(position = 11, required = true, value = "The phone number resource of the PI.")
    @PersonalInfoId
    private Long phoneNumber;
    @ApiModelProperty(position = 12, required = true, value = "The email resource of the PI.")
    @PersonalInfoId
    private Long email;
    @ApiModelProperty(position = 13, required = true, value = "option for choose provider")
    private ProviderOption providerOption;
    @JsonIgnore
    private Boolean isDeleted;

    @FilterIn
    @JsonIgnore
    private String paymentProvider;

    @JsonIgnore
    private String relationToHolder;
    //response:
    @JsonIgnore
    private Boolean isActive;
    @FilterIn
    @JsonIgnore
    private String externalToken;
    @JsonIgnore
    private UserInfo userInfo;

    private RiskFeature riskFeature;

    private String ipAddress;

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
        this.setAdmins(Arrays.asList(userId));
    }
    @JsonIgnore
    public List<Long> getAdmins() {
        return admins;
    }
    @JsonIgnore
    public void setAdmins(List<Long> admins) {
        this.admins = admins;
        this.userId = admins.get(0);
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(boolean validated) {
        this.isValidated = validated;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public TypeSpecificDetails getTypeSpecificDetails() {
        return typeSpecificDetails;
    }

    public void setTypeSpecificDetails(TypeSpecificDetails typeSpecificDetails) {
        this.typeSpecificDetails = typeSpecificDetails;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getEmail() {
        return email;
    }

    public void setEmail(Long email) {
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

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public ProviderOption getProviderOption() {
        return providerOption;
    }

    public void setProviderOption(ProviderOption providerOption) {
        this.providerOption = providerOption;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public RiskFeature getRiskFeature() {
        return riskFeature;
    }

    public void setRiskFeature(RiskFeature riskFeature) {
        this.riskFeature = riskFeature;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
