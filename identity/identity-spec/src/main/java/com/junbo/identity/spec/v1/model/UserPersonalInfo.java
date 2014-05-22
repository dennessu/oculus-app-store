/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/24/14.
 */
public class UserPersonalInfo extends PropertyAssignedAwareResourceMeta implements Identifiable<UserPersonalInfoId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of user personal info resource.")
    @JsonProperty("self")
    private UserPersonalInfoId id;

    @ApiModelProperty(position = 2, required = true, value = "The type of user personal info resource, it must be in " +
            "[WIPED," +
            "ADDRESS, " +
            "EMAIL, " +
            "PHONE, " +
            "NAME, " +
            "DOB, " +
            "SMS, " +
            "QQ, " +
            "WHATSAPP, " +
            "PASSPORT - e.g., \"USA 123456789\", " +
            "GOVERNMENT_ID - SSN or equivalent in other countries, " +
            "DRIVERS_LICENSE - e.g., \"USA CA 12345\" ]" +
            "GENDER.")
    private String type;

    @ApiModelProperty(position = 3, required = true, value = "The userPersonal information, it must be json structure." +
            "ADDRESS Type[street1(string),street2(string),street3(string),city(string),phoneNumber(string),subCountry(string)," +
            "country(link),postalCode(string),firstName(string),lastName(string)];" +
            "Other Types[info(string)]")
    private JsonNode value;

    @ApiModelProperty(position = 4, required = false, value = "Last validated time, if null, it isn't validated.")
    private Date lastValidateTime;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable]Whether the personal info is validated.")
    private Boolean isValidated;

    @ApiModelProperty(position = 6, required = false, value = "Whether the value is normalized or not.")
    private Boolean isNormalized;

    @ApiModelProperty(position = 7, required = true, value = "User resource.")
    @JsonProperty("user")
    private UserId userId;

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
}

