/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserSecurityQuestionAttemptService;
import com.junbo.identity.spec.model.common.ResultsUtil;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.entity.UserSecurityQuestionAttemptGetOption;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOption;
import com.junbo.identity.spec.resource.UserSecurityQuestionAttemptResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserSecurityQuestionAttemptResourceImpl implements UserSecurityQuestionAttemptResource {

    @Autowired
    private UserSecurityQuestionAttemptService attemptService;

    @Override
    public Promise<UserSecurityQuestionAttempt> create(UserId userId,
                                                       UserSecurityQuestionAttempt userSecurityQuestionAttempt) {
        return Promise.pure(attemptService.create(userId, userSecurityQuestionAttempt));
    }

    @Override
    public Promise<UserSecurityQuestionAttempt> get(UserId userId, UserSecurityQuestionAttemptId id,
                                                    @BeanParam UserSecurityQuestionAttemptGetOption getOptions) {
        return Promise.pure(attemptService.get(userId, id));
    }

    @Override
    public Promise<Results<UserSecurityQuestionAttempt>> list(UserId userId,
                                                  @BeanParam UserSecurityQuestionAttemptListOption listOptions) {
        if(listOptions == null) {
            listOptions = new UserSecurityQuestionAttemptListOption();
        }
        listOptions.setUserId(userId);
        List<UserSecurityQuestionAttempt> attempts = attemptService.search(listOptions);
        return Promise.pure(ResultsUtil.init(attempts,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }
}
