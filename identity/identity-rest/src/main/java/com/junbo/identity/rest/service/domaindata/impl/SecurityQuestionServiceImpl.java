/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.domaindata.impl;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.data.repository.SecurityQuestionRepository;
import com.junbo.identity.rest.service.domaindata.SecurityQuestionService;
import com.junbo.identity.rest.service.validator.SecurityQuestionValidator;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by haomin on 14-3-19.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SecurityQuestionServiceImpl implements SecurityQuestionService {
    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @Autowired
    private SecurityQuestionValidator securityQuestionValidator;

    @Override
    public SecurityQuestion get(SecurityQuestionId securityQuestionId) {
        securityQuestionValidator.validateGet(securityQuestionId);
        return securityQuestionRepository.get(securityQuestionId);
    }

    @Override
    public SecurityQuestion create(SecurityQuestion securityQuestion) {
        securityQuestionValidator.validateCreate(securityQuestion);
        return securityQuestionRepository.save(securityQuestion);
    }

    @Override
    public SecurityQuestion update(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        securityQuestionValidator.validateUpdate(securityQuestionId, securityQuestion);

        return securityQuestionRepository.update(securityQuestion);
    }

    @Override
    public SecurityQuestion patch(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        securityQuestionValidator.validataPatch(securityQuestionId, securityQuestion);
        SecurityQuestion existing = get(securityQuestion.getId());

        if(!StringUtils.isEmpty(securityQuestion.getValue())) {
            existing.setValue(securityQuestion.getValue());
        }
        if(securityQuestion.getActive() != null) {
            existing.setActive(securityQuestion.getActive());
        }
        return securityQuestionRepository.update(existing);
    }

    @Override
    public List<SecurityQuestion> search(SecurityQuestionListOptions listOption) {
        securityQuestionValidator.validateSearch(listOption);
        return securityQuestionRepository.search(listOption);
    }
}
