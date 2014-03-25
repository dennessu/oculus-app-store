/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.core.service.user.UserSecurityQuestionService;
import com.junbo.identity.core.service.util.UserPasswordUtil;
import com.junbo.identity.core.service.validator.UserSecurityQuestionValidator;
import com.junbo.identity.data.repository.UserSecurityQuestionRepository;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
@Transactional
public class UserSecurityQuestionServiceImpl implements UserSecurityQuestionService {

    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository;

    @Autowired
    private UserSecurityQuestionValidator validator;

    @Override
    public UserSecurityQuestion save(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        validator.validateCreate(userId, userSecurityQuestion);
        userSecurityQuestion.setUserId(userId);
        userSecurityQuestion.setAnswerSalt(UUID.randomUUID().toString());
        userSecurityQuestion.setAnswerHash(
                UserPasswordUtil.hashPassword(userSecurityQuestion.getAnswer(), userSecurityQuestion.getAnswerSalt()));
        return userSecurityQuestionRepository.save(userSecurityQuestion);
    }

    @Override
    public UserSecurityQuestion get(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {
        validator.validateResourceAccessible(userId, userSecurityQuestionId);
        return userSecurityQuestionRepository.get(userSecurityQuestionId);
    }

    @Override
    public List<UserSecurityQuestion> search(UserSecurityQuestionListOption listOption) {
        return userSecurityQuestionRepository.search(listOption);
    }
}
