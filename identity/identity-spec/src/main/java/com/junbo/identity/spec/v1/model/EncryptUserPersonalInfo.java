/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.id.EncryptUserPersonalInfoId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;

/**
 * Created by liangfu on 5/14/14.
 */
public class EncryptUserPersonalInfo extends PropertyAssignedAwareResourceMeta implements Identifiable<EncryptUserPersonalInfoId> {

    private EncryptUserPersonalInfoId id;

    private UserPersonalInfoId userPersonalInfoId;

    private String encryptUserPersonalInfo;

    private String hashSearchInfo;

    public EncryptUserPersonalInfoId getId() {
        return id;
    }

    public void setId(EncryptUserPersonalInfoId id) {
        this.id = id;
    }

    public UserPersonalInfoId getUserPersonalInfoId() {
        return userPersonalInfoId;
    }

    public void setUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId) {
        this.userPersonalInfoId = userPersonalInfoId;
    }

    public String getEncryptUserPersonalInfo() {
        return encryptUserPersonalInfo;
    }

    public void setEncryptUserPersonalInfo(String encryptUserPersonalInfo) {
        this.encryptUserPersonalInfo = encryptUserPersonalInfo;
    }

    public String getHashSearchInfo() {
        return hashSearchInfo;
    }

    public void setHashSearchInfo(String hashSearchInfo) {
        this.hashSearchInfo = hashSearchInfo;
    }
}
