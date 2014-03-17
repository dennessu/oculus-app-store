/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.password.impl;

import com.junbo.common.id.PasswordRuleId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.data.repository.PasswordRuleRepository;
import com.junbo.identity.data.util.PasswordDAOUtil;
import com.junbo.identity.rest.service.password.PasswordService;
import com.junbo.identity.rest.service.util.UserPasswordUtil;
import com.junbo.identity.rest.service.validator.PasswordRuleValidator;
import com.junbo.identity.spec.model.options.UserPasswordGetOption;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface for password Service.
 */
@Component
@Transactional
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private PasswordRuleRepository passwordRepository;

    @Autowired
    private PasswordRuleValidator validator;

    @Autowired
    private IdGeneratorFacade idGeneratorFacade;

    @Override
    public void validatePassword(String password) {
        PasswordDAOUtil.validatePassword(password);
    }

    @Override
    public void save(UserId userId, UserPassword userPassword) {
        userPassword.setId(new UserPasswordId(idGeneratorFacade.nextId(UserPasswordId.class, userId.getValue())));
        userPassword.setStrength(UserPasswordUtil.calcPwdStrength(userPassword.getValue()));

    }

    @Override
    public UserPassword get(UserId userId, UserPasswordId id) {
        return null;
    }

    @Override
    public List<UserPassword> search(UserId userId, UserPasswordGetOption getOption) {
        return null;
    }

    @Override
    public PasswordRule save(PasswordRule passwordRule) {
        return passwordRepository.save(passwordRule);
    }

    @Override
    public void delete(Long id) {
        passwordRepository.delete(new PasswordRuleId(id));
    }

    @Override
    public PasswordRule get(Long id) {
        return passwordRepository.get(new PasswordRuleId(id));
    }

    @Override
    public PasswordRule update(PasswordRule passwordRule) {
        return passwordRepository.update(passwordRule);
    }
}
