/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.common.util.Identifiable;

import java.util.Date;

/**
 * Created by kg on 3/10/14.
 */

public class UserPin extends ResourceMeta implements Identifiable<UserPinId> {

    @JsonProperty("self")
    private UserPinId id;

    // write only
    private String value;

    private Date expiresBy;

    private Boolean changeAtNextLogin;

    // read only
    private UserId userId;

    private Boolean active;

    // Won't return field
    @JsonIgnore
    private String pinSalt;
    @JsonIgnore
    private String pinPepper;
    @JsonIgnore
    private String pinHash;

    public UserPinId getId() {
        return id;
    }

    public void setId(UserPinId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        support.setPropertyAssigned("active");
    }

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
        support.setPropertyAssigned("expiresBy");
    }

    public Boolean getChangeAtNextLogin() {
        return changeAtNextLogin;
    }

    public void setChangeAtNextLogin(Boolean changeAtNextLogin) {
        this.changeAtNextLogin = changeAtNextLogin;
        support.setPropertyAssigned("changeAtNextLogin");
    }

    public String getPinSalt() {
        return pinSalt;
    }

    public void setPinSalt(String pinSalt) {
        this.pinSalt = pinSalt;
        support.setPropertyAssigned("pinSalt");
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
        support.setPropertyAssigned("pinHash");
    }

    public String getPinPepper() {
        return pinPepper;
    }

    public void setPinPepper(String pinPepper) {
        this.pinPepper = pinPepper;
        support.setPropertyAssigned("pinPepper");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
    }
}
