/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class User extends ResourceMeta implements Identifiable<UserId> {
    @ApiModelProperty(position = 1, required = true, value = "The id of user resource.")
    @JsonProperty("self")
    private UserId id;

    @ApiModelProperty(position = 2, required = true, value = "The username of the user.")
    // username can be null
    private String username;

    @ApiModelProperty(position = 3, required = true, value = "The user type, it must be anonymousUser or user.")
    private String type;

    @ApiModelProperty(position = 4, required = false, value = "The preferred language of the user.")
    private String preferredLanguage;

    @ApiModelProperty(position = 5, required = false, value = "The locale of the user.")
    private String locale;

    @ApiModelProperty(position = 6, required = false, value = "The timezone of the user.")
    private String timezone;

    @ApiModelProperty(position = 7, required = false, value = "Whether the user is active.")
    private Boolean active;

    @ApiModelProperty(position = 8, required = false, value = "The nick name of the user.")
    private String nickName;

    @ApiModelProperty(position = 9, required = false, value = "The currency of the user.")
    private String currency;

    @JsonIgnore
    private String canonicalUsername;

    @Override
    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        support.setPropertyAssigned("username");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        support.setPropertyAssigned("preferredLanguage");
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
        support.setPropertyAssigned("locale");
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        support.setPropertyAssigned("timezone");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        support.setPropertyAssigned("active");
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        support.setPropertyAssigned("nickName");
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        support.setPropertyAssigned("currency");
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
        support.setPropertyAssigned("canonicalUsername");
    }
}
