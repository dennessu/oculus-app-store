/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserPinId;
import com.junbo.identity.data.dao.UserPinDAO;
import com.junbo.identity.data.entity.user.UserPinEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserPinRepository;
import com.junbo.identity.spec.model.options.UserPinGetOption;
import com.junbo.identity.spec.model.users.UserPin;
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
public class UserPinRepositoryImpl implements UserPinRepository {
    @Autowired
    @Qualifier("userPINDAO")
    private UserPinDAO userPINDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public UserPin save(UserPin entity) {
        UserPinEntity userPINEntity = modelMapper.toUserPIN(entity, new MappingContext());
        userPINDAO.save(userPINEntity);

        return get(entity.getId());
    }

    @Override
    public UserPin update(UserPin entity) {
        UserPinEntity userPINEntity = modelMapper.toUserPIN(entity, new MappingContext());
        userPINDAO.update(userPINEntity);

        return get(entity.getId());
    }

    @Override
    public UserPin get(UserPinId id) {
        return modelMapper.toUserPIN(userPINDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<UserPin> search(UserPinGetOption getOption) {
        List entities = userPINDAO.search(getOption.getUserId().getValue(), getOption);

        List<UserPin> results = new ArrayList<UserPin>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserPIN((UserPinEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserPinId id) {
        userPINDAO.delete(id.getValue());
    }
}
