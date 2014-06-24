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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        if (annualTaxReportIds != null ? !annualTaxReportIds.equals(that.annualTaxReportIds) : that.annualTaxReportIds != null)
            return false;
        if (billingAddress != null ? !billingAddress.equals(that.billingAddress) : that.billingAddress != null)
            return false;
        if (canonicalName != null ? !canonicalName.equals(that.canonicalName) : that.canonicalName != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (isValidated != null ? !isValidated.equals(that.isValidated) : that.isValidated != null) return false;
        if (migratedCompanyId != null ? !migratedCompanyId.equals(that.migratedCompanyId) : that.migratedCompanyId != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (ownerId != null ? !ownerId.equals(that.ownerId) : that.ownerId != null) return false;
        if (payoutInstrument != null ? !payoutInstrument.equals(that.payoutInstrument) : that.payoutInstrument != null)
            return false;
        if (payoutTaxProfile != null ? !payoutTaxProfile.equals(that.payoutTaxProfile) : that.payoutTaxProfile != null)
            return false;
        if (shippingAddress != null ? !shippingAddress.equals(that.shippingAddress) : that.shippingAddress != null)
            return false;
        if (shippingName != null ? !shippingName.equals(that.shippingName) : that.shippingName != null) return false;
        if (shippingPhone != null ? !shippingPhone.equals(that.shippingPhone) : that.shippingPhone != null)
            return false;
        if (taxId != null ? !taxId.equals(that.taxId) : that.taxId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (billingAddress != null ? billingAddress.hashCode() : 0);
        result = 31 * result + (shippingAddress != null ? shippingAddress.hashCode() : 0);
        result = 31 * result + (shippingName != null ? shippingName.hashCode() : 0);
        result = 31 * result + (shippingPhone != null ? shippingPhone.hashCode() : 0);
        result = 31 * result + (isValidated != null ? isValidated.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (taxId != null ? taxId.hashCode() : 0);
        result = 31 * result + (payoutInstrument != null ? payoutInstrument.hashCode() : 0);
        result = 31 * result + (payoutTaxProfile != null ? payoutTaxProfile.hashCode() : 0);
        result = 31 * result + (annualTaxReportIds != null ? annualTaxReportIds.hashCode() : 0);
        result = 31 * result + (canonicalName != null ? canonicalName.hashCode() : 0);
        result = 31 * result + (migratedCompanyId != null ? migratedCompanyId.hashCode() : 0);
        return result;
    }
}
