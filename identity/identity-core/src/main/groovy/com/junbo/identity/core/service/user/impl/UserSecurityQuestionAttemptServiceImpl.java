/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.identity.core.service.user.UserSecurityQuestionAttemptService;
import com.junbo.identity.core.service.util.UserPasswordUtil;
import com.junbo.identity.core.service.validator.UserSecurityQuestionAttemptValidator;
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository;
import com.junbo.identity.data.repository.UserSecurityQuestionRepository;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
@Transactional
public class UserSecurityQuestionAttemptServiceImpl implements UserSecurityQuestionAttemptService {
    @Autowired
    private UserSecurityQuestionAttemptValidator validator;

    @Autowired
    @Qualifier("userSecurityQuestionAttemptRepository")
    private UserSecurityQuestionAttemptRepository repository;

    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository;

    @Override
    public UserSecurityQuestionAttempt get(UserId userId, UserSecurityQuestionAttemptId userSecurityQuestionAttemptId) {
        validator.validateGet(userId, userSecurityQuestionAttemptId);
        return repository.get(userSecurityQuestionAttemptId);
    }

    @Override
    public UserSecurityQuestionAttempt create(UserId userId, UserSecurityQuestionAttempt userSecurityQuestionAttempt) {
        validator.validateCreate(userId, userSecurityQuestionAttempt);

        UserSecurityQuestionListOptions option = new UserSecurityQuestionListOptions();
        option.setUserId(userId);
        option.setSecurityQuestionId(userSecurityQuestionAttempt.getSecurityQuestionId());
        List<UserSecurityQuestion> attempts = null;
        try {
            attempts = userSecurityQuestionRepository.search(option).wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String hashValue =
                UserPasswordUtil.hashPassword(userSecurityQuestionAttempt.getValue(), attempts.get(0).getAnswerSalt());

        userSecurityQuestionAttempt.setUserId(userId);
        userSecurityQuestionAttempt.setSucceeded(hashValue.equals(attempts.get(0).getAnswerHash()));
        // todo:    Need to throw exception and create
        return repository.save(userSecurityQuestionAttempt);
    }

    @Override
    public List<UserSecurityQuestionAttempt> search(UserSecurityQuestionAttemptListOptions getOptions) {
        return repository.search(getOptions);
    }
}
