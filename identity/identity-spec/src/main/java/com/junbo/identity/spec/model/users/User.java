/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;

import java.util.Date;

/**
 * Created by kg on 3/10/14.
 */
public class User extends ResourceMeta implements Identifiable<UserId> {

    @JsonProperty("self")
    private UserId id;

    private String username;

    // not readable, not writable
    @JsonIgnore
    private String canonicalUsername;

    private String displayName;

    /*
     * Possible values:
     * 1. firstName_lastName_then_username
     * 2. lastName_firstName_then_username
     * 3. firstName_middleName_lastName_then_username
     * 4. lastName_middleName_firstName_then_username
     * 5. username
     */
    private Integer displayNameType;

    private UserName name;

    private String nickName;

    private String preferredLanguage;

    // for currency, date time format, numerical representation
    private String locale;

    // in Olson timezone database format.
    private String timezone;

    // only active can only login.
    private Boolean active;

    private Date birthday;

    private String gender;

    // anonymousUser, user
    private String type;

    // private List<UserEmail> emails;

    // private List<UserPhoneNumber> phoneNumbers;

    // private List<UserAuthenticator> authenticators;

    // private List<UserTos> tos;

    // private List<UserOptin> optins;

    // private List<UserSecurityQuestion> securityQuestions;

    // private List<UserPassword> passwords;

    // private List<UserPin> pins;

    // private List<UserCredentialVerifyAttempt> loginAttempts;

    // private List<UserGroup> groups; not indexable.

    // private List<UserDevice> devices;

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
        support.setPropertyAssigned("id");

        // just work around. TODO: fix later.
        support.setPropertyAssigned("self");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        support.setPropertyAssigned("username");
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
        support.setPropertyAssigned("canonicalUsername");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        support.setPropertyAssigned("displayName");
    }

    public Integer getDisplayNameType() {
        return displayNameType;
    }

    public void setDisplayNameType(Integer displayNameType) {
        this.displayNameType = displayNameType;
        support.setPropertyAssigned("displayNameType");
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        support.setPropertyAssigned("nickName");
    }

    public UserName getName() {
        return name;
    }

    public void setName(UserName name) {
        this.name = name;
        support.setPropertyAssigned("name");
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
        support.setPropertyAssigned("birthday");
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        support.setPropertyAssigned("gender");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }
}
