/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
public class UserSecurityQuestionAttemptRepositoryImpl implements UserSecurityQuestionAttemptRepository {

    @Autowired
    @Qualifier("userSecurityQuestionAttemptDAO")
    private UserSecurityQuestionAttemptDAO userSecurityQuestionAttemptDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public UserSecurityQuestionAttempt save(UserSecurityQuestionAttempt entity) {
        UserSecurityQuestionAttemptEntity userSecurityQuestionAttemptEntity =
                modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext());

        userSecurityQuestionAttemptDAO.save(userSecurityQuestionAttemptEntity);

        return get(new UserSecurityQuestionAttemptId(userSecurityQuestionAttemptEntity.getId()));
    }

    @Override
    public UserSecurityQuestionAttempt update(UserSecurityQuestionAttempt entity) {
        UserSecurityQuestionAttemptEntity userSecurityQuestionAttemptEntity =
                modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext());

        userSecurityQuestionAttemptDAO.update(userSecurityQuestionAttemptEntity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionAttempt get(UserSecurityQuestionAttemptId id) {
        return modelMapper.toUserSecurityQuestionAttempt(userSecurityQuestionAttemptDAO.get(id.getValue()),
                new MappingContext());
    }

    @Override
    public List<UserSecurityQuestionAttempt> search(UserSecurityQuestionAttemptListOptions getOption) {
        List<UserSecurityQuestionAttemptEntity> entities =
                userSecurityQuestionAttemptDAO.search(getOption.getUserId().getValue(), getOption);

        List<UserSecurityQuestionAttempt> results = new ArrayList<>();
        for(UserSecurityQuestionAttemptEntity entity : entities) {
            results.add(modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext()));
        }
        return results;
    }
}
