/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.data.dao.UserSecurityQuestionDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserSecurityQuestionRepository;
import com.junbo.identity.spec.model.options.UserSecurityQuestionGetOption;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserSecurityQuestionRepositoryImpl implements UserSecurityQuestionRepository {
    @Autowired
    @Qualifier("userSecurityQuestionDAO")
    private UserSecurityQuestionDAO userSecurityQuestionDAO;

    @Autowired
    @Qualifier("modelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public UserSecurityQuestion save(UserSecurityQuestion entity) {
        UserSecurityQuestionEntity userSecurityQuestionEntity =
                modelMapper.toUserSecurityQuestion(entity, new MappingContext());
        userSecurityQuestionDAO.save(userSecurityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestion update(UserSecurityQuestion entity) {
        UserSecurityQuestionEntity userSecurityQuestionEntity =
                modelMapper.toUserSecurityQuestion(entity, new MappingContext());
        userSecurityQuestionDAO.update(userSecurityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestion get(UserSecurityQuestionId id) {
        return modelMapper.toUserSecurityQuestion(userSecurityQuestionDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<UserSecurityQuestion> search(UserSecurityQuestionGetOption getOption) {
        List entities = userSecurityQuestionDAO.search(getOption);

        List<UserSecurityQuestion> results = new ArrayList<UserSecurityQuestion>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserSecurityQuestion((UserSecurityQuestionEntity) entities.get(i),
                    new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserSecurityQuestionId id) {
        userSecurityQuestionDAO.delete(id.getValue());
    }
}
