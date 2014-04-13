/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.model.users;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.common.util.Identifiable;

import java.util.Date;

/**
 * Created by kg on 3/10/14.
 */
public class UserPassword extends ResourceMeta implements Identifiable<UserPasswordId> {

    private UserPasswordId id;

    private String value;

    private Boolean active;

    private Date expiresBy;

    private Boolean changeAtNextLogin;

    // Read only field
    private String strength;

    // Won't return field
    private String passwordSalt;
    private String passwordHash;
    private UserId userId;

    public UserPasswordId getId() {
        return id;
    }

    public void setId(UserPasswordId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
    }

    public Boolean getChangeAtNextLogin() {
        return changeAtNextLogin;
    }

    public void setChangeAtNextLogin(Boolean changeAtNextLogin) {
        this.changeAtNextLogin = changeAtNextLogin;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
