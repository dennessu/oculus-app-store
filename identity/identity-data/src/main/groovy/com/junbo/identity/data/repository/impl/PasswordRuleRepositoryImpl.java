/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.PasswordRuleId;
import com.junbo.identity.data.dao.PasswordRuleDAO;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.PasswordRuleRepository;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 2/24/14.
 */
@Component
public class PasswordRuleRepositoryImpl implements PasswordRuleRepository {

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Autowired
    private PasswordRuleDAO passwordRuleDAO;

    @Override
    public PasswordRule get(PasswordRuleId id) {
        return modelMapper.toPasswordRule(passwordRuleDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public PasswordRule save(PasswordRule passwordRule) {
        PasswordRuleEntity entity = modelMapper.toPasswordRule(passwordRule, new MappingContext());
        passwordRuleDAO.save(entity);

        return get(passwordRule.getId());
    }

    @Override
    public void delete(PasswordRuleId id) {
        passwordRuleDAO.delete(id.getValue());
    }

    @Override
    public PasswordRule update(PasswordRule passwordRule) {
        PasswordRuleEntity entity = modelMapper.toPasswordRule(passwordRule, new MappingContext());
        passwordRuleDAO.update(entity);

        return get(passwordRule.getId());
    }
}
