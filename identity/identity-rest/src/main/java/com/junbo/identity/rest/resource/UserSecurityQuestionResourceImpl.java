/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.options.entity.UserSecurityQuestionGetOptions;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOption;
import com.junbo.identity.spec.resource.UserSecurityQuestionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserSecurityQuestionResourceImpl implements UserSecurityQuestionResource {
    @Override
    public Promise<UserSecurityQuestion> create(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> put(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                             UserSecurityQuestion userSecurityQuestion) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> patch(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                               UserSecurityQuestion userSecurityQuestion) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> delete(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {
        return null;
    }

    @Override
    public Promise<UserSecurityQuestion> get(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                             @BeanParam UserSecurityQuestionGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<ResultList<UserSecurityQuestion>> list(UserId userId,
                                                          @BeanParam UserSecurityQuestionListOption listOptions) {
        return null;
    }
}
