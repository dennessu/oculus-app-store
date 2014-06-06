/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserCryptoKeyId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;

/**
 * Created by liangfu on 5/7/14.
 */
public class UserCryptoKey extends ResourceMeta implements Identifiable<UserCryptoKeyId> {
    private UserCryptoKeyId id;

    @JsonProperty("user")
    private UserId userId;

    @JsonIgnore
    private String value;

    @JsonIgnore
    private String encryptValue;

    @JsonIgnore
    private Integer keyVersion;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserCryptoKeyId getId() {
        return id;
    }

    public void setId(UserCryptoKeyId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEncryptValue() {
        return encryptValue;
    }

    public void setEncryptValue(String encryptValue) {
        this.encryptValue = encryptValue;
    }

    public Integer getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(Integer keyVersion) {
        this.keyVersion = keyVersion;
    }
}
