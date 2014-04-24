/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Link;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 4/3/14.
 */
public class User extends ResourceMeta implements Identifiable<UserId> {
    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of user resource.")
    @JsonProperty("self")
    private UserId id;

    @ApiModelProperty(position = 2, required = true,
            value = "The username of the user, it can be null if it is anonymous user or login through authenticators.")
    private String username;

    @ApiModelProperty(position = 3, required = false, value = "The preferred locale of the user.")
    private String preferredLocale;

    @ApiModelProperty(position = 4, required = false,
            value = "The preferred timezone of the user, must be the format as UTC+08:00.")
    private String preferredTimezone;

    @ApiModelProperty(position = 5, required = false, value = "User status.")
    private String status;

    @ApiModelProperty(position = 5, required = false, value = "The address book.")
    private List<UserPersonalInfoLink> addressBook = new ArrayList<>();

    @ApiModelProperty(position = 6, required = false, value = "[Nullable]User Group memberships.")
    private Link groupMemeberships;

    @ApiModelProperty(position = 7, required = false, value = "[Nullable]Groups containing the user.")
    private Link groups;

    @ApiModelProperty(position = 8, required = false, value = "[Nullable]Devices attaching to the user.")
    private Link devices;

    @ApiModelProperty(position = 9, required = false,
            value = "[Nullable]Authenticators attaching to the user.")
    private Link authenticators;

    @ApiModelProperty(position = 10, required = false, value = "[Nullable]Tos user agree.")
    private Link tosAgreements;

    @ApiModelProperty(position = 11, required = false, value = "[Nullable]User Credential history.")
    private Link credentials;

    @ApiModelProperty(position = 12, required = false, value = "[Nullable]User Credential Attempt history.")
    private Link credentialAttempts;

    @ApiModelProperty(position = 13, required = false, value = "[Nullable]User selecting optins")
    private Link optins;

    @ApiModelProperty(position = 14, required = false, value = "[Nullable]User Pii")
    private Link personalInfo;

    @ApiModelProperty(position = 15, required = false, value = "[Nullable]User Security questions")
    private Link securityQuestions;

    @ApiModelProperty(position = 16, required = false, value = "[Nullable]User Security quesitons attempts.")
    private Link securityQuestionAttempts;

    @ApiModelProperty(position = 17, required = false, value = "[Nullable]User Payment Instruments.")
    private Link paymentInstruments;

    @ApiModelProperty(position = 18, required = false, value = "[Nullable]User default payment instrument.")
    private Link defaultPaymentInstrument;

    @ApiModelProperty(position = 19, required = false, value = "[Nullable]User orders.")
    private Link orders;

    @ApiModelProperty(position = 20, required = false, value = "[Nullable]User cart.")
    private Link cart;

    @ApiModelProperty(position = 21, required = false, value = "[Nullable]User entitlements.")
    private Link entitlements;

    @JsonIgnore
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

    public String getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(String preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserPersonalInfoLink> getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(List<UserPersonalInfoLink> addressBook) {
        this.addressBook = addressBook;
    }

    public Link getGroupMemeberships() {
        return groupMemeberships;
    }

    public void setGroupMemeberships(Link groupMemeberships) {
        this.groupMemeberships = groupMemeberships;
    }

    public Link getGroups() {
        return groups;
    }

    public void setGroups(Link groups) {
        this.groups = groups;
    }

    public Link getDevices() {
        return devices;
    }

    public void setDevices(Link devices) {
        this.devices = devices;
    }

    public Link getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(Link authenticators) {
        this.authenticators = authenticators;
    }

    public Link getTosAgreements() {
        return tosAgreements;
    }

    public void setTosAgreements(Link tosAgreements) {
        this.tosAgreements = tosAgreements;
    }

    public Link getCredentials() {
        return credentials;
    }

    public void setCredentials(Link credentials) {
        this.credentials = credentials;
    }

    public Link getCredentialAttempts() {
        return credentialAttempts;
    }

    public void setCredentialAttempts(Link credentialAttempts) {
        this.credentialAttempts = credentialAttempts;
    }

    public Link getOptins() {
        return optins;
    }

    public void setOptins(Link optins) {
        this.optins = optins;
    }

    public Link getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(Link personalInfo) {
        this.personalInfo = personalInfo;
    }

    public Link getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(Link securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    public Link getSecurityQuestionAttempts() {
        return securityQuestionAttempts;
    }

    public void setSecurityQuestionAttempts(Link securityQuestionAttempts) {
        this.securityQuestionAttempts = securityQuestionAttempts;
    }

    public Link getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(Link paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
    }

    public Link getDefaultPaymentInstrument() {
        return defaultPaymentInstrument;
    }

    public void setDefaultPaymentInstrument(Link defaultPaymentInstrument) {
        this.defaultPaymentInstrument = defaultPaymentInstrument;
    }

    public Link getOrders() {
        return orders;
    }

    public void setOrders(Link orders) {
        this.orders = orders;
    }

    public Link getCart() {
        return cart;
    }

    public void setCart(Link cart) {
        this.cart = cart;
    }

    public Link getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Link entitlements) {
        this.entitlements = entitlements;
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
    }
}
