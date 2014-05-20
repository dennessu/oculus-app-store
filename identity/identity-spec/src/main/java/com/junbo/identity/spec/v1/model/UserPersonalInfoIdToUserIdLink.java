/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.id.UserPersonalInfoIdToUserIdLinkId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;

/**
 * Created by liangfu on 5/14/14.
 */
public class UserPersonalInfoIdToUserIdLink extends PropertyAssignedAwareResourceMeta
        implements Identifiable<UserPersonalInfoIdToUserIdLinkId> {

    private UserPersonalInfoIdToUserIdLinkId id;

    private UserPersonalInfoId userPersonalInfoId;

    private UserId userId;

    public UserPersonalInfoIdToUserIdLinkId getId() {
        return id;
    }

    public void setId(UserPersonalInfoIdToUserIdLinkId id) {
        this.id = id;
    }

    public UserPersonalInfoId getUserPersonalInfoId() {
        return userPersonalInfoId;
    }

    public void setUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId) {
        this.userPersonalInfoId = userPersonalInfoId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
