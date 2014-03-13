/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.group.Group;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * Created by kg on 3/10/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private UserId id;

    private String username;

    private String displayName;

    private UserName name;

    private String preferredLanguage;

    private String locale; // for currency, date time format, numerical representation

    private String timezone;

    private Boolean active; // only active can only login.
        // Todo:    Need to confirm with Kgu, we need to support multiple status.

    private Date birthday; // The date and time is in ISO 8601 format. For example, 2014-03-11T02:10:24Z.

    private String gender;

    // Those below values should be expand according to the expand feature.
    @Null
    private List<UserEmail> emails;

    @Null
    @JsonProperty("phones")
    private List<PhoneNumber> phoneNumbers;

    @Null
    private List<UserExternalId> externalIds;

    @Null
    @JsonProperty("tosAcceptances")
    private List<UserTos> tos;

    @Null
    private List<UserOptIn> optins;

    // private List<UserSecurityQuestion> securityQuestions;

    // private List<UserPassword> passwords;

    // private List<UserPIN> pins;

    @Null
    private List<LoginAttempt> loginAttempts;

    @Null
    private List<Group> groups;

    @Null
    private List<UserDevice> devices;

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<UserEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<UserEmail> emails) {
        this.emails = emails;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<UserExternalId> getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(List<UserExternalId> externalIds) {
        this.externalIds = externalIds;
    }

    public List<UserTos> getTos() {
        return tos;
    }

    public void setTos(List<UserTos> tos) {
        this.tos = tos;
    }

    public List<UserOptIn> getOptins() {
        return optins;
    }

    public void setOptins(List<UserOptIn> optins) {
        this.optins = optins;
    }

    public List<LoginAttempt> getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(List<LoginAttempt> loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<UserDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<UserDevice> devices) {
        this.devices = devices;
    }
}
