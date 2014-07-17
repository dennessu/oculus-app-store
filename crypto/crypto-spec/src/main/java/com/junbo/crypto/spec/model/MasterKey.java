/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by liangfu on 5/12/14.
 */
public class MasterKey extends ResourceMeta<Long> {

    private String value;

    @JsonIgnore
    private Long keyVersion;

    @JsonIgnore
    private String encryptValue;

    @Override
    public Long getId() {
        return keyVersion;
    }

    @Override
    public void setId(Long id) {
        this.keyVersion = id;
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

    public Long getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(Long keyVersion) {
        this.keyVersion = keyVersion;
    }
}
