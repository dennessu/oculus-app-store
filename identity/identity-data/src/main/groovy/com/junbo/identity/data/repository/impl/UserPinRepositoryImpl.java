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
import com.junbo.identity.spec.options.list.UserPinListOptions;
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
    @Qualifier("userPinDAO")
    private UserPinDAO userPinDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public UserPin save(UserPin entity) {
        UserPinEntity userPinEntity = modelMapper.toUserPin(entity, new MappingContext());
        userPinDAO.save(userPinEntity);

        return get(new UserPinId(userPinEntity.getId()));
    }

    @Override
    public UserPin update(UserPin entity) {
        UserPinEntity userPINEntity = modelMapper.toUserPin(entity, new MappingContext());
        userPinDAO.update(userPINEntity);

        return get(entity.getId());
    }

    @Override
    public UserPin get(UserPinId id) {
        return modelMapper.toUserPin(userPinDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<UserPin> search(UserPinListOptions getOption) {
        List entities = userPinDAO.search(getOption.getUserId().getValue(), getOption);

        List<UserPin> results = new ArrayList<UserPin>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserPin((UserPinEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserPinId id) {
        userPinDAO.delete(id.getValue());
    }
}
