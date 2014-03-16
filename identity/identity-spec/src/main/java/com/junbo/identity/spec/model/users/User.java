/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/10/14.
 */
public class User extends ResourceMeta implements Identifiable<UserId> {

    private UserId id;

    private String username;

    private String displayName;

    private String nickName;

    private UserName name;

    private String preferredLanguage;

    // for currency, date time format, numerical representation
    private String locale;

    // in Olson timezone database format.
    private String timezone;

    // only active can only login.
    private Boolean active;

    private String birthday;

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

    // private List<UserPIN> pins;

    // private List<LoginAttempt> loginAttempts;

    // private List<UserGroup> groups; not indexable.

    // private List<UserDevice> devices;

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserName getName() {
        return name;
    }

    public void setName(UserName name) {
        this.name = name;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
