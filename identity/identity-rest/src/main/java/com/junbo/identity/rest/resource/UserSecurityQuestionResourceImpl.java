/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserSecurityQuestionService;
import com.junbo.identity.spec.model.common.ResultsUtil;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.options.entity.UserSecurityQuestionGetOptions;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions;
import com.junbo.identity.spec.resource.UserSecurityQuestionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserSecurityQuestionResourceImpl implements UserSecurityQuestionResource {

    @Autowired
    private UserSecurityQuestionService userSecurityQuestionService;

    @Override
    public Promise<UserSecurityQuestion> create(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        return Promise.pure(userSecurityQuestionService.save(userId, userSecurityQuestion));
    }

    @Override
    public Promise<UserSecurityQuestion> get(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                             @BeanParam UserSecurityQuestionGetOptions getOptions) {
        return Promise.pure(userSecurityQuestionService.get(userId, userSecurityQuestionId));
    }

    @Override
    public Promise<Results<UserSecurityQuestion>> list(UserId userId,
                                                          @BeanParam UserSecurityQuestionListOptions listOptions) {
        if(listOptions == null) {
            listOptions = new UserSecurityQuestionListOptions();
        }
        listOptions.setUserId(userId);
        List<UserSecurityQuestion> userSecurityQuestions = userSecurityQuestionService.search(listOptions);
        return Promise.pure(ResultsUtil.init(userSecurityQuestions,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }
}
