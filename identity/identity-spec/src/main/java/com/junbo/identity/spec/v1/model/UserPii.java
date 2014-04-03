/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPiiId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

import java.util.Date;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserPii extends ResourceMeta implements Identifiable<UserPiiId> {

    @JsonProperty("self")
    private UserPiiId id;

    @JsonProperty("user")
    private UserId userId;

    private UserName name;

    private Map<String, UserEmail> emails;

    private Map<String, UserPhoneNumber> phoneNumbers;

    private Date birthday;

    private String gender;

    private String displayName;

    @Override
    public UserPiiId getId() {
        return id;
    }

    public void setId(UserPiiId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserName getName() {
        return name;
    }

    public void setName(UserName name) {
        this.name = name;
    }

    public Map<String, UserEmail> getEmails() {
        return emails;
    }

    public void setEmails(Map<String, UserEmail> emails) {
        this.emails = emails;
    }

    public Map<String, UserPhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<String, UserPhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
