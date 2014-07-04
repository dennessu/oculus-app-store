/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;

/**
 * Created by liangfu on 5/14/14.
 */
public class EncryptUserPersonalInfo extends PropertyAssignedAwareResourceMeta<UserPersonalInfoId> {

    private UserPersonalInfoId id;

    private String encryptUserPersonalInfo;

    public UserPersonalInfoId getId() {
        return id;
    }

    public void setId(UserPersonalInfoId id) {
        this.id = id;
    }

    public String getEncryptUserPersonalInfo() {
        return encryptUserPersonalInfo;
    }

    public void setEncryptUserPersonalInfo(String encryptUserPersonalInfo) {
        this.encryptUserPersonalInfo = encryptUserPersonalInfo;
    }
}
