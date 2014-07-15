/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

import java.util.Date;

/**
 * The CreateUserRequest class.
 */
public class CreateUserRequest {

    private String username;

    private String firstName;

    private String middleName;

    private String lastName;

    private String nickName;

    private String password;

    private String pinCode;

    private String cor;

    private String preferredLocale;

    private String emailAddress;

    private Date dob;

    private Boolean tosAgreed;

    private Boolean newsPromotionsAgreed;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(String preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Boolean getTosAgreed() {
        return tosAgreed;
    }

    public void setTosAgreed(Boolean tosAgreed) {
        this.tosAgreed = tosAgreed;
    }

    public Boolean getNewsPromotionsAgreed() {
        return newsPromotionsAgreed;
    }

    public void setNewsPromotionsAgreed(Boolean newsPromotionsAgreed) {
        this.newsPromotionsAgreed = newsPromotionsAgreed;
    }
}
