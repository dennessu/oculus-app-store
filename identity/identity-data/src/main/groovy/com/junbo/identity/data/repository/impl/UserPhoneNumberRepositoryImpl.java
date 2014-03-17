/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.data.dao.UserPhoneNumberDAO;
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserPhoneNumberRepository;
import com.junbo.identity.spec.model.options.UserPhoneNumberGetOption;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
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
public class UserPhoneNumberRepositoryImpl implements UserPhoneNumberRepository {
    @Autowired
    @Qualifier("userPhoneNumberDAO")
    private UserPhoneNumberDAO userPhoneNumberDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public UserPhoneNumber save(UserPhoneNumber entity) {
        UserPhoneNumberEntity userPhoneNumberEntity = modelMapper.toUserPhoneNumber(entity, new MappingContext());
        userPhoneNumberDAO.save(userPhoneNumberEntity);

        return get(entity.getId());
    }

    @Override
    public UserPhoneNumber update(UserPhoneNumber entity) {
        UserPhoneNumberEntity userPhoneNumberEntity = modelMapper.toUserPhoneNumber(entity, new MappingContext());
        userPhoneNumberDAO.save(userPhoneNumberEntity);

        return get(entity.getId());
    }

    @Override
    public UserPhoneNumber get(UserPhoneNumberId id) {
        return modelMapper.toUserPhoneNumber(userPhoneNumberDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<UserPhoneNumber> search(UserPhoneNumberGetOption getOption) {
        List entities = userPhoneNumberDAO.search(getOption);

        List<UserPhoneNumber> results = new ArrayList<UserPhoneNumber>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserPhoneNumber((UserPhoneNumberEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserPhoneNumberId id) {
        userPhoneNumberDAO.delete(id.getValue());
    }
}
