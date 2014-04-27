/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by liangfu on 4/24/14.
 */
public class UserPersonalInfo extends ResourceMeta implements Identifiable<UserPersonalInfoId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of user personal info resource.")
    @JsonProperty("self")
    private UserPersonalInfoId id;

    @ApiModelProperty(position = 2, required = true, value = "The type of user personal info resource, it must be in " +
            "[ADDRESS, " +
            "EMAIL, " +
            "PHONE, " +
            "GIVEN_NAME, " +
            "FAMILY_NAME, " +
            "MIDDLE_NAME, " +
            "NICK_NAME, " +
            "DOB, " +
            "SMS, " +
            "QQ, " +
            "WHATSAPP, " +
            "PASSPORT - e.g., \"USA 123456789\", " +
            "GOVERNMENT_ID - SSN or equivalent in other countries, " +
            "DRIVERS_LICENSE - e.g., \"USA CA 12345\" ].")
    private String type;

    @ApiModelProperty(position = 3, required = true, value = "The userPersonal information, it must be json structure.")
    private String value;

    @ApiModelProperty(position = 4, required = false, value = "Last validated time, if null, it isn't validated.")
    private Date lastValidateTime;

    @ApiModelProperty(position = 5, required = false, value = "Whether the value is normalized or not.")
    private Boolean isNormalized;

    @ApiModelProperty(position = 6, required = false, value = "User resource link.")
    private String label;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        support.setPropertyAssigned("label");
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error happened while encoding the value", e);
        }
    }

    public String decodedValue() {
        try {
            return URLDecoder.decode(value, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error happened while decoding the value", e);
        }
    }
}

