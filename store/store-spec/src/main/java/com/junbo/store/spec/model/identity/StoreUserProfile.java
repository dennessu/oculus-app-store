/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.junbo.common.id.UserId;

import java.util.List;

/**
 * The UserProfile class.
 */
public class StoreUserProfile {

    private UserId userId;
    private String username;
    private List<PersonalInfo> emails;
    private List<PersonalInfo> phones;
    private List<PersonalInfo> addresses;
    private PersonalInfo name;
    private Boolean tfaEnabled;
    @JsonUnwrapped
    private com.junbo.identity.spec.v1.model.UserProfile idUserProfile;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<PersonalInfo> getEmails() {
        return emails;
    }

    public void setEmails(List<PersonalInfo> emails) {
        this.emails = emails;
    }

    public List<PersonalInfo> getPhones() {
        return phones;
    }

    public void setPhones(List<PersonalInfo> phones) {
        this.phones = phones;
    }

    public List<PersonalInfo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PersonalInfo> addresses) {
        this.addresses = addresses;
    }

    public PersonalInfo getName() {
        return name;
    }

    public void setName(PersonalInfo name) {
        this.name = name;
    }

    public Boolean getTfaEnabled() {
        return tfaEnabled;
    }

    public void setTfaEnabled(Boolean tfaEnabled) {
        this.tfaEnabled = tfaEnabled;
    }

    public com.junbo.identity.spec.v1.model.UserProfile getIdUserProfile() {
        return idUserProfile;
    }

    public void setIdUserProfile(com.junbo.identity.spec.v1.model.UserProfile idUserProfile) {
        this.idUserProfile = idUserProfile;
    }
}
