/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.*;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 5/22/14.
 */
public class Organization extends PropertyAssignedAwareResourceMeta<OrganizationId> {
    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of organization resource.")
    @JsonProperty("self")
    private OrganizationId id;

    @ApiModelProperty(position = 2, required = true, value = "The owner of this organization.")
    @JsonProperty("owner")
    private UserId ownerId;

    @ApiModelProperty(position = 3, required = true, value = "The name of the organization.")
    private String name;

    @ApiModelProperty(position = 4, required = false, value = "The billing address of the organization.")
    private UserPersonalInfoId billingAddress;

    @ApiModelProperty(position = 5, required = false, value = "The shipping address of the organization.")
    private UserPersonalInfoId shippingAddress;

    @ApiModelProperty(position = 6, required = false, value = "The shipping name of the organization.")
    private UserPersonalInfoId shippingName;

    @ApiModelProperty(position = 7, required = false, value = "The shipping phone of the organization.")
    private UserPersonalInfoId shippingPhone;

    @ApiModelProperty(position = 8, required = true, value = "Whether the organization is validated.")
    private Boolean isValidated;

    @ApiModelProperty(position = 9, required = false, value = "The enum value of the organization, must in [INDIVIDUAL, CORPORATE]")
    private String type;

    @ApiModelProperty(position = 11, required = false, value = "The type (EIN/TIN/SSN) and value of taxId.")
    private UserPersonalInfoId taxId;

    @ApiModelProperty(position = 12, required = false, value = "The payoutInstrument link.")
    private UserPersonalInfoId payoutInstrument;

    @ApiModelProperty(position = 13, required = false, value = "The payout tax profile.")
    private PayoutTaxProfileId payoutTaxProfile;

    @ApiModelProperty(position = 14, required = false, value = "")
    private List<AnnualTaxReportId> annualTaxReportIds = new ArrayList<>();

    @JsonIgnore
    private String canonicalName;

    // This is just for migration only, please don't use them
    @JsonIgnore
    private Long migratedCompanyId;

    public OrganizationId getId() {
        return id;
    }

    public void setId(OrganizationId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserId ownerId) {
        this.ownerId = ownerId;
        support.setPropertyAssigned("owner");
        support.setPropertyAssigned("ownerId");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public UserPersonalInfoId getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(UserPersonalInfoId billingAddress) {
        this.billingAddress = billingAddress;
        support.setPropertyAssigned("billingAddress");
    }

    public UserPersonalInfoId getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(UserPersonalInfoId shippingAddress) {
        this.shippingAddress = shippingAddress;
        support.setPropertyAssigned("shippingAddress");
    }

    public UserPersonalInfoId getShippingName() {
        return shippingName;
    }

    public void setShippingName(UserPersonalInfoId shippingName) {
        this.shippingName = shippingName;
        support.setPropertyAssigned("shippingName");
    }

    public UserPersonalInfoId getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(UserPersonalInfoId shippingPhone) {
        this.shippingPhone = shippingPhone;
        support.setPropertyAssigned("shippingPhone");
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
        support.setPropertyAssigned("isValidated");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public UserPersonalInfoId getTaxId() {
        return taxId;
    }

    public void setTaxId(UserPersonalInfoId taxId) {
        this.taxId = taxId;
        support.setPropertyAssigned("taxId");
    }

    public UserPersonalInfoId getPayoutInstrument() {
        return payoutInstrument;
    }

    public void setPayoutInstrument(UserPersonalInfoId payoutInstrument) {
        this.payoutInstrument = payoutInstrument;
        support.setPropertyAssigned("payoutInstrument");
    }

    public PayoutTaxProfileId getPayoutTaxProfile() {
        return payoutTaxProfile;
    }

    public void setPayoutTaxProfile(PayoutTaxProfileId payoutTaxProfile) {
        this.payoutTaxProfile = payoutTaxProfile;
        support.setPropertyAssigned("payoutTaxProfile");
    }

    public List<AnnualTaxReportId> getAnnualTaxReportIds() {
        return annualTaxReportIds;
    }

    public void setAnnualTaxReportIds(List<AnnualTaxReportId> annualTaxReportIds) {
        this.annualTaxReportIds = annualTaxReportIds;
        support.setPropertyAssigned("annualTaxReportIds");
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public Long getMigratedCompanyId() {
        return migratedCompanyId;
    }

    public void setMigratedCompanyId(Long migratedCompanyId) {
        this.migratedCompanyId = migratedCompanyId;
    }
}
