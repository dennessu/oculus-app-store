/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.cloudant.CloudantUnique;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class User extends PropertyAssignedAwareResourceMeta<UserId> implements CloudantUnique {
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] Link to the User resource.")
    @JsonProperty("self")
    private UserId id;

    @ApiModelProperty(position = 2, required = true,
            value = "[Nullable] PII Link with type USERNAME, or null if isAnonymous or if this user logs in only through authenticators.")
    private UserPersonalInfoId username;

    @ApiModelProperty(position = 3, required = false, value = " [Nullable] Null or the preferred-name / nick-name")
    private String nickName;

    @ApiModelProperty(position = 3, required = false, value = "Link to the the User's preferred Locale.")
    private LocaleId preferredLocale;

    @ApiModelProperty(position = 4, required = false, value = "The preferred timezone of the user, must be the format as UTC+08:00.")
    private String preferredTimezone;

    @ApiModelProperty(position = 5, required = true, value = "True if/only if this User is anonymous (happens when 'username' is non-null " +
            "and/or the 'authenticators' list is non-empty)")
    private Boolean isAnonymous;

    @ApiModelProperty(position = 6, required = false, value = "User status. It must be in [ACTIVE, SUSPEND, BANNED DELETED].")
    private String status;

    @ApiModelProperty(position = 5, required = false, value = "The address book, an array of personalInfo Links," +
            "the Links to the current addresses that the user has, every link has a label such as \"Grandma's Home Address\".")
    private List<UserPersonalInfoLink> addresses;

    @ApiModelProperty(position = 5, required = false, value = "The phone list, an array of personalInfo Links, " +
            "the Links to the current phone info list that the user has, every link has a label such as \"mobile\".")
    private List<UserPersonalInfoLink> phones;

    @ApiModelProperty(position = 5, required = false, value = "The email list, an array of personalInfo Links, " +
            "the Links to the current email list that the user has,every link has a label such as \"business email\".")
    private List<UserPersonalInfoLink> emails;

    @ApiModelProperty(position = 6, required = false, value = "The PersonalInfo resource that contains the user's name (given name, family name, etc.)")
    private UserPersonalInfoId name;

    @ApiModelProperty(position = 6, required = false, value = "Country Link to User's Country of Residence.")
    @JsonProperty("cor")
    private CountryId countryOfResidence;

    @ApiModelProperty(position = 7, required = false, value = "The personalInfo Link to the personalInfo resource, which is date of birth of the user.")
    private UserPersonalInfoId dob;

    @ApiModelProperty(position = 8, required = false, value = "The textMsg list, an array of personalInfo Links, " +
            "the Links to the current txtMessage list that the user has,every link has a label such as \"secret1\".")
    private List<UserPersonalInfoLink> textMessages;

    @ApiModelProperty(position = 9, required = false, value = "The QQ list, an array of personalInfo Links, " +
            "the Links to the current QQ list that the user has,every link has a label such as \"business QQ\".")
    private List<UserPersonalInfoLink> qqs;

    @ApiModelProperty(position = 10, required = false, value = "The WhatsApp list, an array of personalInfo Links, " +
            "the Links to the current WhatsApp list that the user has,every link has a label such as \"business WhatsApp\".")
    private List<UserPersonalInfoLink> whatsApps;

    @ApiModelProperty(position = 11, required = false, value = "The personalInfo Link to the personalInfo resource, which is Passport of the user.")
    private UserPersonalInfoId passport;

    @ApiModelProperty(position = 12, required = false, value = "The personalInfo Link to the personalInfo resource, which is Government ID of the user.")
    private UserPersonalInfoId governmentId;

    @ApiModelProperty(position = 13, required = false, value = "The personalInfo Link to the personalInfo resource, which is Driver License of the user.")
    private UserPersonalInfoId driversLicense;

    @ApiModelProperty(position = 14, required = false, value = "The personalInfo Link to the personalInfo resource, which is Gender of the user.")
    private UserPersonalInfoId gender;

    @ApiModelProperty(position = 15, required = false, value = "A mapping between country code and User VAT number, country code must be exist valid country code.")
    private Map<String, UserVAT> vat = new HashMap<>();


    @ApiModelProperty(position = 18, required = false, value = "User profile information object, contains the profile info migrated from share.")
    private UserProfile profile;

    @ApiModelProperty(position = 6, required = false, value = "A link to the Groups resource, search the groups that contain the user.")
    @HateoasLink("/user-group-memberships?userId={id}")
    private Link groupMemeberships;

    @ApiModelProperty(position = 7, required = false, value = "[Nullable]Groups containing the user.")
    @HateoasLink("/groups?userId={id}")
    private Link groups;

    @ApiModelProperty(position = 8, required = false, value = "Devices attaching to the user.")
    @HateoasLink("/devices?userId={id}")
    private Link devices;

    @ApiModelProperty(position = 9, required = false, value = "[Not nullable, possibly empty]Link to Authenticators attaching to the user.")
    @HateoasLink("/authenticators?userId={id}")
    private Link authenticators;

    @ApiModelProperty(position = 10, required = false, value = "[Not nullable, possibly empty]Array of links to the Tos user agree record.")
    @HateoasLink("tos-agreements?userId={id}")
    private Link tosAgreements;

    @ApiModelProperty(position = 11, required = false, value = "[Client Immutable] A Link to UserCredential resource to Search user password type credentials of the user.")
    @HateoasLink("/users/{id}/credentials?credentialType=PASSWORD")
    private Link credentials;

    @ApiModelProperty(position = 12, required = false, value = "[Client Immutable] A Link to CredentialAttempt-List resource to " +
            "Search credential attempts of the user.")
    @HateoasLink("/credential-attempts?userId={id}")
    private Link credentialAttempts;

    @ApiModelProperty(position = 13, required = false, value = " [Client Immutable] A Link to OptIn-List resource to Search opt-in of the user.")
    @HateoasLink("/opt-ins?userId={id}")
    private Link optins;

    @ApiModelProperty(position = 15, required = false, value = "[Client Immutable] A Link to SecurityQuestion-List resource to " +
            "Search Security Questions of the user.")
    @HateoasLink("/users/{id}/security-questions")
    private Link securityQuestions;

    @ApiModelProperty(position = 16, required = false, value = "[Client Immutable] A Link to SecurityQuestionAttempt-List resource " +
            "to Search Security Questions Attempts of the user.")
    @HateoasLink("/users/{id}/security-question-attempts")
    private Link securityQuestionAttempts;

    @ApiModelProperty(position = 17, required = false, value = "[Client Immutable] A Link to PaymentInstrument-List resource to " +
            "Search Payment Instruments of the user.")
    @HateoasLink("/payment-instruments?userId={id}")
    private Link paymentInstruments;

    @ApiModelProperty(position = 18, required = false, value = "A mapping between country code and user Tax ememption information, country code must be exist valid country code")
    private Map<String, TaxExempt> taxExemption;

    @ApiModelProperty(position = 19, required = false, value = "[Client Immutable] A Link to Order-List resource to Search orders of the user.")
    @HateoasLink("/orders?userId={id}")
    private Link orders;

    @ApiModelProperty(position = 20, required = false, value = "[Client Immutable] A Link to Cart-List resource to Search the prLoginResourceImplimary cart of the user.")
    @HateoasLink("/users/{id}/carts/primary")
    private Link cart;

    @ApiModelProperty(position = 21, required = false, value = "[Client Immutable] A Link to Entitlement-List resource to Search the entitlements of the user.")
    @HateoasLink("/entitlements?userId={id}")
    private Link entitlements;

    @ApiModelProperty(position = 22, required = false, value = " A Link to Payment Instrument which will be represent to the user as default Payment Instrument, " +
            "the Default PI should be either an Oculus eWallet or Credit Card, as these are the payment types availalbe on web, " +
            "on Android and in VR,")
    private PaymentInstrumentId defaultPI;

    // This field is just for migration only, please don't use it in any API
    @JsonIgnore
    private Long migratedUserId;

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public void setUsername(UserPersonalInfoId username) {
        this.username = username;
        support.setPropertyAssigned("username");
    }

    public UserPersonalInfoId getUsername() {
        return username;
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

    public UserPersonalInfoId getName() {
        return name;
    }

    public void setName(UserPersonalInfoId name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public UserPersonalInfoId getDob() {
        return dob;
    }

    public void setDob(UserPersonalInfoId dob) {
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

    public UserPersonalInfoId getPassport() {
        return passport;
    }

    public void setPassport(UserPersonalInfoId passport) {
        this.passport = passport;
        support.setPropertyAssigned("passport");
    }

    public UserPersonalInfoId getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(UserPersonalInfoId governmentId) {
        this.governmentId = governmentId;
        support.setPropertyAssigned("governmentId");
    }

    public UserPersonalInfoId getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(UserPersonalInfoId driversLicense) {
        this.driversLicense = driversLicense;
        support.setPropertyAssigned("driversLicense");
    }

    public UserPersonalInfoId getGender() {
        return gender;
    }

    public void setGender(UserPersonalInfoId gender) {
        this.gender = gender;
        support.setPropertyAssigned("gender");
    }

    public CountryId getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(CountryId countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
        support.setPropertyAssigned("cor");
        support.setPropertyAssigned("countryOfResidence");
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        support.setPropertyAssigned("nickName");
    }

    public Map<String, UserVAT> getVat() {
        return vat;
    }

    public void setVat(Map<String, UserVAT> vat) {
        this.vat = vat;
        support.setPropertyAssigned("vat");
    }

    public Map<String, TaxExempt> getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(Map<String, TaxExempt> taxExemption) {
        this.taxExemption = taxExemption;
        support.setPropertyAssigned("taxExemption");
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
        support.setPropertyAssigned("profile");
    }

    public Long getMigratedUserId() {
        return migratedUserId;
    }

    public void setMigratedUserId(Long migratedUserId) {
        this.migratedUserId = migratedUserId;
    }

    public PaymentInstrumentId getDefaultPI() {
        return defaultPI;
    }

    public void setDefaultPI(PaymentInstrumentId defaultPI) {
        this.defaultPI = defaultPI;
        support.setPropertyAssigned("defaultPI");
    }

    /**
     * Get the unique keys of the CloudantEntity.
     * Each of the keys are globally unique.
     * The unique keys for the same object must always be the same length. If some key is not required, place a null.
     *
     * @return The unique keys.
     */
    @Override
    public String[] getUniqueKeys() {
        return new String[] {
                getMigrationUniqueKey()
        };
    }

    private String getMigrationUniqueKey() {
        if (migratedUserId == null) {
            return null;
        } else {
            return "USER_MIGRATION_ID: " + migratedUserId.toString();
        }
    }
}
