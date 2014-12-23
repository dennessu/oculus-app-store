package com.junbo.sharding.dualwrite.test.model;
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;

import java.util.List;

public class User extends ResourceMeta<UserId> {
    private UserId id;
    private String username;
    private LocaleId preferredLocale;
    private String preferredTimezone;
    private Boolean isAnonymous;
    private String status;
    private List<UserPersonalInfoLink> addresses;
    private List<UserPersonalInfoLink> phones;
    private List<UserPersonalInfoLink> emails;
    private UserPersonalInfoLink name;
    private UserPersonalInfoLink dob;
    private List<UserPersonalInfoLink> textMessages;
    private List<UserPersonalInfoLink> qqs;
    private List<UserPersonalInfoLink> whatsApps;
    private UserPersonalInfoLink passport;
    private UserPersonalInfoLink governmentId;
    private UserPersonalInfoLink driversLicense;
    private UserPersonalInfoLink gender;
    private String canonicalUsername;

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

    public LocaleId getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(LocaleId preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserPersonalInfoLink> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserPersonalInfoLink> addresses) {
        this.addresses = addresses;
    }

    public List<UserPersonalInfoLink> getPhones() {
        return phones;
    }

    public void setPhones(List<UserPersonalInfoLink> phones) {
        this.phones = phones;
    }

    public List<UserPersonalInfoLink> getEmails() {
        return emails;
    }

    public void setEmails(List<UserPersonalInfoLink> emails) {
        this.emails = emails;
    }

    public UserPersonalInfoLink getName() {
        return name;
    }

    public void setName(UserPersonalInfoLink name) {
        this.name = name;
    }

    public UserPersonalInfoLink getDob() {
        return dob;
    }

    public void setDob(UserPersonalInfoLink dob) {
        this.dob = dob;
    }

    public List<UserPersonalInfoLink> getTextMessages() {
        return textMessages;
    }

    public void setTextMessages(List<UserPersonalInfoLink> textMessages) {
        this.textMessages = textMessages;
    }

    public List<UserPersonalInfoLink> getQqs() {
        return qqs;
    }

    public void setQqs(List<UserPersonalInfoLink> qqs) {
        this.qqs = qqs;
    }

    public List<UserPersonalInfoLink> getWhatsApps() {
        return whatsApps;
    }

    public void setWhatsApps(List<UserPersonalInfoLink> whatsApps) {
        this.whatsApps = whatsApps;
    }

    public UserPersonalInfoLink getPassport() {
        return passport;
    }

    public void setPassport(UserPersonalInfoLink passport) {
        this.passport = passport;
    }

    public UserPersonalInfoLink getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(UserPersonalInfoLink governmentId) {
        this.governmentId = governmentId;
    }

    public UserPersonalInfoLink getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(UserPersonalInfoLink driversLicense) {
        this.driversLicense = driversLicense;
    }

    public UserPersonalInfoLink getGender() {
        return gender;
    }

    public void setGender(UserPersonalInfoLink gender) {
        this.gender = gender;
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
    }
}
