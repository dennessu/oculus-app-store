/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserSecurityQuestionResource;
import com.junbo.identity.spec.v1.model.options.UserSecurityQuestionGetOption;
import com.junbo.identity.spec.v1.model.users.UserSecurityQuestion;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserSecurityQuestionResourceImpl implements UserSecurityQuestionResource {
    @Override
    public Promise<UserSecurityQuestion> create(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> update(UserId userId, UserSecurityQuestionId userSecurityQuestionId, UserSecurityQuestion userSecurityQuestion) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> patch(UserId userId, UserSecurityQuestionId userSecurityQuestionId, UserSecurityQuestion userSecurityQuestion) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> get(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> delete(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserSecurityQuestion>> get(UserId userId, @BeanParam UserSecurityQuestionGetOption getOption) {
        return null;
    }
}
