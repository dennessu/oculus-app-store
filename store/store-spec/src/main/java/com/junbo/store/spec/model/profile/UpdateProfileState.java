/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.profile;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;

import java.util.Date;

/**
 * Created by liangfu on 8/27/2014.
 */
public class UpdateProfileState {
    private UserId userId;

    private UserPersonalInfoId emailPIIId;

    private Date timeStamp;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserPersonalInfoId getEmailPIIId() {
        return emailPIIId;
    }

    public void setEmailPIIId(UserPersonalInfoId emailPIIId) {
        this.emailPIIId = emailPIIId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
