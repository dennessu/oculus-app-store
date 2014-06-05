/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.MasterKeyId;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by liangfu on 5/12/14.
 */
public class MasterKey extends ResourceMeta<MasterKeyId> {

    @CloudantIgnore
    private MasterKeyId id;

    private String value;

    @JsonIgnore
    private Integer keyVersion;

    @JsonIgnore
    private String encryptValue;

    public MasterKeyId getId() {
        return id;
    }

    public void setId(MasterKeyId id) {
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
