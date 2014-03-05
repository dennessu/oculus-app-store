/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserProfileId;
import com.junbo.identity.spec.model.common.CommonStamp;

import java.util.Date;
/**
 * Java cod for UserProfile.
 */
public class UserProfile extends CommonStamp {
    @JsonProperty("self")
    private UserProfileId id;

    @JsonProperty("user")
    private UserId userId;

    private String type;
    private String region;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date dateOfBirth;
    private String locale;

    public UserProfileId getId() {
        return id;
    }

    public void setId(UserProfileId id) {
        this.id = id;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
