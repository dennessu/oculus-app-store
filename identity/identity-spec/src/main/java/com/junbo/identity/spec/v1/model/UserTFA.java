/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.id.UserTFAId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/22/14.
 */
public class UserTFA extends PropertyAssignedAwareResourceMeta<UserTFAId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of UserTFA resource.")
    @JsonProperty("self")
    private UserTFAId id;

    @ApiModelProperty(position = 2, required = false, value = "[Client Immutable]The id of user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "User Phone number used to verify.")
    private UserPersonalInfoId personalInfo;

    @ApiModelProperty(position = 4, required = false, value = "The language to sent to the user.")
    private LocaleId sentLocale;

    @JsonIgnore
    private String verifyCode;

    @ApiModelProperty(position = 6, required = false, value = "The template sent to the user.")
    private String template;

    @ApiModelProperty(position = 7, required = true, value = "The verify type, it must be in [CALL, SMS].")
    private String verifyType;

    @ApiModelProperty(position = 8, required = false, value = "[Client Immutable]The verify expires time.")
    private Date expiresBy;

    @ApiModelProperty(position = 9, required = false, value = "[Client Immutable]Whether user Tele resource is active.")
    private Boolean active;

    public void setId(UserTFAId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserTFAId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public UserPersonalInfoId getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(UserPersonalInfoId personalInfo) {
        this.personalInfo = personalInfo;
        support.setPropertyAssigned("personalInfo");
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
        support.setPropertyAssigned("verifyCode");
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
        support.setPropertyAssigned("template");
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
        support.setPropertyAssigned("verifyType");
    }

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
        support.setPropertyAssigned("expiresBy");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        support.setPropertyAssigned("active");
    }

    public LocaleId getSentLocale() {
        return sentLocale;
    }

    public void setSentLocale(LocaleId sentLocale) {
        this.sentLocale = sentLocale;
        support.setPropertyAssigned("sentLocale");
    }
}