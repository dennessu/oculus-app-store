/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.data.dao.SecurityQuestionDAO;
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.SecurityQuestionRepository;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class SecurityQuestionRepositoryImpl implements SecurityQuestionRepository {
    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public SecurityQuestion save(SecurityQuestion entity) {
        SecurityQuestionEntity securityQuestionEntity = modelMapper.toSecurityQuestion(entity, new MappingContext());
        securityQuestionDAO.save(securityQuestionEntity);

        return get(new SecurityQuestionId(securityQuestionEntity.getId()));
    }

    @Override
    public SecurityQuestion update(SecurityQuestion entity) {
        SecurityQuestionEntity securityQuestionEntity = modelMapper.toSecurityQuestion(entity, new MappingContext());
        securityQuestionDAO.update(securityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public SecurityQuestion get(SecurityQuestionId id) {
        return modelMapper.toSecurityQuestion(securityQuestionDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<SecurityQuestion> search(SecurityQuestionListOptions listOption) {
        List entities = securityQuestionDAO.search(listOption);

        List<SecurityQuestion> results = new ArrayList<SecurityQuestion>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toSecurityQuestion((SecurityQuestionEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(SecurityQuestionId id) {
        securityQuestionDAO.delete(id.getValue());
    }
}
