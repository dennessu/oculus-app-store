/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserSecurityQuestionAttemptListOptions extends PagingGetOptions {

    @QueryParam("properties")
    private String properties;

    @QueryParam("userSecurityQuestionId")
    private UserSecurityQuestionId userSecurityQuestionId;

    @QueryParam("userId")
    private UserId userId;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public UserSecurityQuestionId getUserSecurityQuestionId() {
        return userSecurityQuestionId;
    }

    public void setUserSecurityQuestionId(UserSecurityQuestionId userSecurityQuestionId) {
        this.userSecurityQuestionId = userSecurityQuestionId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
