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
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by liangfu on 4/3/14.
 */
public class User extends PropertyAssignedAwareResourceMeta implements Identifiable<UserId> {
    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of user resource.")
    @JsonProperty("self")
    private UserId id;

    @ApiModelProperty(position = 2, required = true,
            value = "The username of the user, it can be null if it is anonymous user or login through authenticators.")
    private String username;

    @ApiModelProperty(position = 3, required = false, value = "The preferred locale of the user.")
    private LocaleId preferredLocale;

    @ApiModelProperty(position = 4, required = false,
            value = "The preferred timezone of the user, must be the format as UTC+08:00.")
    private String preferredTimezone;

    @ApiModelProperty(position = 5, required = true, value = "Is Anonymous. " +
            "If username is null and authenticator isn't null, the value will be true;" +
            "else, the value is false. Anonymous user can't have authenticator.")
    private Boolean isAnonymous;

    @ApiModelProperty(position = 5, required = false, value = "User status. " +
            "It must be in [ACTIVE, SUSPEND, BANNED DELETED].")
    private String status;

    @ApiModelProperty(position = 5, required = false, value = "The address book.")
    private List<UserPersonalInfoLink> addresses;

    @ApiModelProperty(position = 5, required = false, value = "The personal phones information.")
    private List<UserPersonalInfoLink> phones;

    @ApiModelProperty(position = 5, required = false, value = "The personal email information.")
    private List<UserPersonalInfoLink> emails;

    @ApiModelProperty(position = 6, required = false, value = "The personal name information.")
    private UserPersonalInfoLink name;

    @ApiModelProperty(position = 7, required = false, value = "The personal birthday information.")
    private UserPersonalInfoLink dob;

    @ApiModelProperty(position = 8, required = false, value = "The personal textMessages information.")
    private List<UserPersonalInfoLink> textMessages;

    @ApiModelProperty(position = 9, required = false, value = "The personal qq information.")
    private List<UserPersonalInfoLink> qqs;

    @ApiModelProperty(position = 10, required = false, value = "The personal whatsapp information.")
    private List<UserPersonalInfoLink> whatsApps;

    @ApiModelProperty(position = 11, required = false, value = "The personal passport information.")
    private UserPersonalInfoLink passport;

    @ApiModelProperty(position = 12, required = false, value = "The personal government id information.")
    private UserPersonalInfoLink governmentId;

    @ApiModelProperty(position = 13, required = false, value = "The personal drivers license information.")
    private UserPersonalInfoLink driversLicense;

    @ApiModelProperty(position = 14, required = false, value = "The personal gender information.")
    private UserPersonalInfoLink gender;

    @ApiModelProperty(position = 6, required = false, value = "[Nullable]User Group memberships.")
    @HateoasLink("/user-group-memberships?userId={id}")
    private Link groupMemeberships;

    @ApiModelProperty(position = 7, required = false, value = "[Nullable]Groups containing the user.")
    @HateoasLink("/groups?userId={id}")
    private Link groups;

    @ApiModelProperty(position = 8, required = false, value = "[Nullable]Devices attaching to the user.")
    @HateoasLink("/devices?userId={id}")
    private Link devices;

    @ApiModelProperty(position = 9, required = false,
            value = "[Nullable]Authenticators attaching to the user.")
    @HateoasLink("/authenticators?userId={id}")
    private Link authenticators;

    @ApiModelProperty(position = 10, required = false, value = "[Nullable]Tos user agree.")
    @HateoasLink("/users/{id}/tos-agreements")
    private Link tosAgreements;

    @ApiModelProperty(position = 11, required = false, value = "[Nullable]User Credential history.")
    @HateoasLink("/users/{id}/credentials")
    private Link credentials;

    @ApiModelProperty(position = 12, required = false, value = "[Nullable]User Credential Attempt history.")
    @HateoasLink("/credential-attempts?userId={id}")
    private Link credentialAttempts;

    @ApiModelProperty(position = 13, required = false, value = "[Nullable]User selecting optins")
    @HateoasLink("/opt-ins?userId={id}")
    private Link optins;

    @ApiModelProperty(position = 15, required = false, value = "[Nullable]User Security questions")
    @HateoasLink("/users/{id}/security-questions")
    private Link securityQuestions;

    @ApiModelProperty(position = 16, required = false, value = "[Nullable]User Security quesiton attempts.")
    @HateoasLink("/users/{id}/security-question-attempts")
    private Link securityQuestionAttempts;

    @ApiModelProperty(position = 17, required = false, value = "[Nullable]User Payment Instruments.")
    @HateoasLink("/payment-instruments?userId={id}")
    private Link paymentInstruments;

    @ApiModelProperty(position = 19, required = false, value = "[Nullable]User orders.")
    @HateoasLink("/orders?userId={id}")
    private Link orders;

    @ApiModelProperty(position = 20, required = false, value = "[Nullable]User cart.")
    @HateoasLink("/users/{id}/carts/primary")
    private Link cart;

    @ApiModelProperty(position = 21, required = false, value = "[Nullable]User entitlements.")
    @HateoasLink("/entitlements?userId={id}")
    private Link entitlements;

    @JsonIgnore
    private String canonicalUsername;

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        support.setPropertyAssigned("username");
    }

    public LocaleId getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(LocaleId preferredLocale) {
        this.preferredLocale = preferredLocale;
        support.setPropertyAssigned("preferredLocale");
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
        support.setPropertyAssigned("preferredTimezone");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        support.setPropertyAssigned("status");
    }

    public Link getGroupMemeberships() {
        return groupMemeberships;
    }

    public void setGroupMemeberships(Link groupMemeberships) {
        this.groupMemeberships = groupMemeberships;
        // support.setPropertyAssigned("groupMemeberships");
    }

    public Link getGroups() {
        return groups;
    }

    public void setGroups(Link groups) {
        this.groups = groups;
        // support.setPropertyAssigned("groups");
    }

    public Link getDevices() {
        return devices;
    }

    public void setDevices(Link devices) {
        this.devices = devices;
        // support.setPropertyAssigned("devices");
    }

    public Link getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(Link authenticators) {
        this.authenticators = authenticators;
        // support.setPropertyAssigned("authenticators");
    }

    public Link getTosAgreements() {
        return tosAgreements;
    }

    public void setTosAgreements(Link tosAgreements) {
        this.tosAgreements = tosAgreements;
        // support.setPropertyAssigned("tosAgreements");
    }

    public Link getCredentials() {
        return credentials;
    }

    public void setCredentials(Link credentials) {
        this.credentials = credentials;
        // support.setPropertyAssigned("credentials");
    }

    public Link getCredentialAttempts() {
        return credentialAttempts;
    }

    public void setCredentialAttempts(Link credentialAttempts) {
        this.credentialAttempts = credentialAttempts;
        // support.setPropertyAssigned("credentialAttempts");
    }

    public Link getOptins() {
        return optins;
    }

    public void setOptins(Link optins) {
        this.optins = optins;
        // support.setPropertyAssigned("optins");
    }

    public Link getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(Link securityQuestions) {
        this.securityQuestions = securityQuestions;
        // support.setPropertyAssigned("securityQuestions");
    }

    public Link getSecurityQuestionAttempts() {
        return securityQuestionAttempts;
    }

    public void setSecurityQuestionAttempts(Link securityQuestionAttempts) {
        this.securityQuestionAttempts = securityQuestionAttempts;
        // support.setPropertyAssigned("securityQuestionAttempts");
    }

    public Link getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(Link paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
        // support.setPropertyAssigned("paymentInstruments");
    }

    public Link getOrders() {
        return orders;
    }

    public void setOrders(Link orders) {
        this.orders = orders;
        // support.setPropertyAssigned("orders");
    }

    public Link getCart() {
        return cart;
    }

    public void setCart(Link cart) {
        this.cart = cart;
        // support.setPropertyAssigned("cart");
    }

    public Link getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Link entitlements) {
        this.entitlements = entitlements;
        // support.setPropertyAssigned("entitlements");
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
        support.setPropertyAssigned("canonicalUsername");
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        support.setPropertyAssigned("isAnonymous");
    }

    public List<UserPersonalInfoLink> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserPersonalInfoLink> addresses) {
        this.addresses = addresses;
        support.setPropertyAssigned("addresses");
    }

    public List<UserPersonalInfoLink> getPhones() {
        return phones;
    }

    public void setPhones(List<UserPersonalInfoLink> phones) {
        this.phones = phones;
        support.setPropertyAssigned("phones");
    }

    public List<UserPersonalInfoLink> getEmails() {
        return emails;
    }

    public void setEmails(List<UserPersonalInfoLink> emails) {
        this.emails = emails;
        support.setPropertyAssigned("emails");
    }

    public UserPersonalInfoLink getName() {
        return name;
    }

    public void setName(UserPersonalInfoLink name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public UserPersonalInfoLink getDob() {
        return dob;
    }

    public void setDob(UserPersonalInfoLink dob) {
        this.dob = dob;
        support.setPropertyAssigned("dob");
    }

    public List<UserPersonalInfoLink> getTextMessages() {
        return textMessages;
    }

    public void setTextMessages(List<UserPersonalInfoLink> textMessages) {
        this.textMessages = textMessages;
        support.setPropertyAssigned("textMessages");
    }

    public List<UserPersonalInfoLink> getQqs() {
        return qqs;
    }

    public void setQqs(List<UserPersonalInfoLink> qqs) {
        this.qqs = qqs;
        support.setPropertyAssigned("qqs");
    }

    public List<UserPersonalInfoLink> getWhatsApps() {
        return whatsApps;
    }

    public void setWhatsApps(List<UserPersonalInfoLink> whatsApps) {
        this.whatsApps = whatsApps;
        support.setPropertyAssigned("whatsApps");
    }

    public UserPersonalInfoLink getPassport() {
        return passport;
    }

    public void setPassport(UserPersonalInfoLink passport) {
        this.passport = passport;
        support.setPropertyAssigned("passport");
    }

    public UserPersonalInfoLink getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(UserPersonalInfoLink governmentId) {
        this.governmentId = governmentId;
        support.setPropertyAssigned("governmentId");
    }

    public UserPersonalInfoLink getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(UserPersonalInfoLink driversLicense) {
        this.driversLicense = driversLicense;
        support.setPropertyAssigned("driversLicense");
    }

    public UserPersonalInfoLink getGender() {
        return gender;
    }

    public void setGender(UserPersonalInfoLink gender) {
        this.gender = gender;
        support.setPropertyAssigned("gender");
    }
}
