/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserEmailId;
import com.junbo.identity.data.dao.UserEmailDAO;
import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserEmailRepository;
import com.junbo.identity.spec.model.options.UserEmailGetOption;
import com.junbo.identity.spec.model.users.UserEmail;
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
public class UserEmailRepositoryImpl implements UserEmailRepository {
    @Autowired
    @Qualifier("userEmailDAO")
    private UserEmailDAO userEmailDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public void delete(UserEmailId id) {
        userEmailDAO.delete(id.getValue());
    }

    @Override
    public List<UserEmail> search(UserEmailGetOption getOption) {

        List entities = userEmailDAO.search(getOption);

        List<UserEmail> results = new ArrayList<UserEmail>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserEmail((UserEmailEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public UserEmail get(UserEmailId id) {
        return modelMapper.toUserEmail(userEmailDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public UserEmail update(UserEmail entity) {
        UserEmailEntity userEmailEntity = modelMapper.toUserEmail(entity, new MappingContext());
        userEmailDAO.update(userEmailEntity);

        return get(entity.getId());
    }

    @Override
    public UserEmail save(UserEmail entity) {
        UserEmailEntity userEmailEntity = modelMapper.toUserEmail(entity, new MappingContext());
        userEmailDAO.save(userEmailEntity);

        return get(new UserEmailId(userEmailEntity.getId()));
    }
}
