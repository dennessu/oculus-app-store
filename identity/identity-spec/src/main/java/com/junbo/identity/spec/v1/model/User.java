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

/**
 * Created by liangfu on 4/3/14.
 */
public class User extends ResourceMeta implements Identifiable<UserId> {
    @JsonProperty("self")
    private UserId id;

    // username can be null
    private String username;

    // type should only support anonymousUser/user
    private String type;

    private String preferredLanguage;

    private String locale;

    private String timezone;

    private Boolean active;

    private String nickName;

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
