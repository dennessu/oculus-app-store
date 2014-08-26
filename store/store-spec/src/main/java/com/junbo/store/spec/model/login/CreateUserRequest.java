/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

import com.junbo.common.id.CommunicationId;
import com.junbo.common.id.TosId;
import com.junbo.store.spec.model.ChallengeAnswer;

import java.util.Date;
import java.util.List;

/**
 * The CreateUserRequest class.
 */
public class CreateUserRequest {

    private ChallengeAnswer challengeAnswer;

    private String username;

    private String firstName;

    private String middleName;

    private String lastName;

    private String nickName;

    private String password;

    private String pin;

    private String cor;

    private String preferredLocale;

    private String email;

    private Date dob;

    private List<TosId> tosAgreed;

    private List<CommunicationId> newsPromotionsAgreed;

    public ChallengeAnswer getChallengeAnswer() {
        return challengeAnswer;
    }

    public void setChallengeAnswer(ChallengeAnswer challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public List<TosId> getTosAgreed() {
        return tosAgreed;
    }

    public void setTosAgreed(List<TosId> tosAgreed) {
        this.tosAgreed = tosAgreed;
    }

    public List<CommunicationId> getNewsPromotionsAgreed() {
        return newsPromotionsAgreed;
    }

    public void setNewsPromotionsAgreed(List<CommunicationId> newsPromotionsAgreed) {
        this.newsPromotionsAgreed = newsPromotionsAgreed;
    }
}
