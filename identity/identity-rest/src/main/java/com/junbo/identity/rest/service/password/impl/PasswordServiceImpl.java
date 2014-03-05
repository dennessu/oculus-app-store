/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.password.impl;

import com.junbo.identity.data.dao.PasswordDAO;
import com.junbo.identity.data.util.PasswordDAOUtil;
import com.junbo.identity.rest.service.password.PasswordService;
import com.junbo.identity.rest.service.validator.PasswordRuleValidator;
import com.junbo.identity.spec.model.password.PasswordRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for password Service.
 */
@Component
@Transactional
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private PasswordDAO passwordDAO;

    @Autowired
    private PasswordRuleValidator validator;

    @Override
    public void validatePassword(String password) {
        PasswordDAOUtil.validatePassword(password);
    }

    @Override
    public PasswordRule save(PasswordRule passwordRule) {
        return passwordDAO.save(passwordRule);
    }

    @Override
    public void delete(Long id) {
        passwordDAO.delete(id);
    }

    @Override
    public PasswordRule get(Long id) {
        return passwordDAO.get(id);
    }

    @Override
    public PasswordRule update(PasswordRule passwordRule) {
        return passwordDAO.update(passwordRule);
    }
}
