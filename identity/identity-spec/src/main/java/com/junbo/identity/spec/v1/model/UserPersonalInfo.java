/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/24/14.
 */
public class UserPersonalInfo extends PropertyAssignedAwareResourceMeta<UserPersonalInfoId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] Link to this PersonalInfo resource.")
    @JsonProperty("self")
    private UserPersonalInfoId id;

    @ApiModelProperty(position = 2, required = true, value = "Enumeration giving the type of information in the 'value' property. " +
            "The enumerations are ADDRESS, EMAIL, PHONE, NAME, DOB, SMS, QQ, WHATSAPP, PASSPORT, GOVERNMENT_ID, DRIVERS_LICENSE, GENDER. " +
            "The type is encrypted when storing the info into the DB.")
    private String type;

    @ApiModelProperty(position = 3, required = true, value = "The userPersonal information, it must be json structure." +
            "ADDRESS Type[street1(string),street2(string),street3(string),city(string),phoneNumber(string),subCountry(string)," +
            "country(link)]; Other Types[info(string)]")
    private JsonNode value;

    @ApiModelProperty(position = 4, required = false, value = "[Nullable] Null if the 'value' has not been validated, " +
            "otherwise it shall be a timestamp (in ISO 8601 format) that indicates the last time 'value' was validated. " +
            "Clients use PUT to control validation: changing it to null causes the server to forget any previous validation, " +
            "and changing it to a timestamp causes the server to re-validate (the server adjusts the timestamp, or sets it to null if validation failed). " +
            "The lastValidateTime property is encrypted when the PersonalInfo resource is stored into the DB.")
    private Date lastValidateTime;

    @ApiModelProperty(position = 5, required = false, value = "[Client Immutable] True if/only if lastValidateTime is non-null and is recent enough. " +
            "Used primarily for convenience in query-params, e.g., /personal-info?isValidated=true.")
    private Boolean isValidated;

    @ApiModelProperty(position = 6, required = true, value = "True if/only if the value is normalized, " +
            "e.g., addresses and phone numbers can be put into some normalized form via a normalizer service.")
    private Boolean isNormalized;

    @ApiModelProperty(position = 7, required = false, value = "Link to the user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 8, required = false, value = "Link to the organization resource.")
    @JsonProperty("organization")
    private OrganizationId organizationId;

    public UserPersonalInfoId getId() {
        return id;
    }

    public void setId(UserPersonalInfoId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public Date getLastValidateTime() {
        return lastValidateTime;
    }

    public void setLastValidateTime(Date lastValidateTime) {
        this.lastValidateTime = lastValidateTime;
        support.setPropertyAssigned("lastValidateTime");
    }

    public Boolean getIsNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(Boolean isNormalized) {
        this.isNormalized = isNormalized;
        support.setPropertyAssigned("isNormalized");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
        support.setPropertyAssigned("isValidated");
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
        support.setPropertyAssigned("organizationId");
        support.setPropertyAssigned("organization");
    }
}

