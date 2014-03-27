/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.options.list;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserSecurityQuestionListOptions extends PagingGetOptions {
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public SecurityQuestionId getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(SecurityQuestionId securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    @QueryParam("userId")
    private UserId userId;
    @QueryParam("securityQuestionId")
    private SecurityQuestionId securityQuestionId;
}
