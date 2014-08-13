/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;

/**
 * The UserProfile class.
 */
public class StoreUserProfile {

    @JsonProperty("user")
    private UserId userId;

    private String username;

    private StoreUserEmail email;

    private String password;

    private String pin;

    private Boolean tfaEnabled;

    private Double ipd;

    private Double height;

    private String headline;

    private String avatar;

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

    public StoreUserEmail getEmail() {
        return email;
    }

    public void setEmail(StoreUserEmail email) {
        this.email = email;
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

    public Boolean getTfaEnabled() {
        return tfaEnabled;
    }

    public void setTfaEnabled(Boolean tfaEnabled) {
        this.tfaEnabled = tfaEnabled;
    }

    public Double getIpd() {
        return ipd;
    }

    public void setIpd(Double ipd) {
        this.ipd = ipd;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
