/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.csr.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CsrLogId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.csr.spec.def.CsrLogActionType;

/**
 * Created by haomin on 14-7-4.
 */
public class CsrLog extends ResourceMeta<CsrLogId> {
    public CsrLogId getId() {
        return id;
    }

    public void setId(CsrLogId id) {
        this.id = id;
    }

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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegarding() {
        return regarding;
    }

    public void setRegarding(String regarding) {
        this.regarding = regarding;
    }

    public CsrLogActionType getAction() {
        return action;
    }

    public void setAction(CsrLogActionType action) {
        this.action = action;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @JsonProperty("self")
    private CsrLogId id;
    private UserId userId;
    private String username;
    private String countryCode;
    private String regarding;
    private CsrLogActionType action;
    private String property;
}
