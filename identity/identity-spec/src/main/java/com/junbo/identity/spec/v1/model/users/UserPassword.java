/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.users;

import com.junbo.common.id.UserPasswordId;
import com.junbo.common.util.Identifiable;

import java.util.Date;

/**
 * Created by kg on 3/10/14.
 */
public class UserPassword extends ResourceMeta implements Identifiable<UserPasswordId> {

    private UserPasswordId id;

    private String value;

    private String strength;

    private Boolean active;

    private Date expiresBy;

    private Boolean changeAtNextLogin;

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
}
