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
public class HashUserPersonalInfo extends PropertyAssignedAwareResourceMeta<UserPersonalInfoId> {

    private UserPersonalInfoId id;

    private UserPersonalInfoId userPersonalInfoId;

    private String hashSearchInfo;

    public UserPersonalInfoId getId() {
        return id;
    }

    public void setId(UserPersonalInfoId id) {
        this.id = id;
    }

    public String getHashSearchInfo() {
        return hashSearchInfo;
    }

    public void setHashSearchInfo(String hashSearchInfo) {
        this.hashSearchInfo = hashSearchInfo;
    }
}
