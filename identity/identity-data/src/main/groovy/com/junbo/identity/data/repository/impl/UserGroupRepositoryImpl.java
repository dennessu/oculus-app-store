/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserGroupId;
import com.junbo.identity.data.dao.GroupUserDAO;
import com.junbo.identity.data.dao.UserGroupDAO;
import com.junbo.identity.data.entity.group.GroupUserEntity;
import com.junbo.identity.data.entity.user.UserGroupEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserGroupRepository;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.identity.spec.model.users.UserGroup;
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
public class UserGroupRepositoryImpl implements UserGroupRepository {
    @Autowired
    @Qualifier("userGroupDAO")
    private UserGroupDAO userGroupDAO;

    @Autowired
    @Qualifier("groupUserDAO")
    private GroupUserDAO groupUserDAO;

    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public UserGroup save(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext());
        userGroupDAO.save(userGroupEntity);

        GroupUserEntity groupUserEntity = new GroupUserEntity();
        groupUserEntity.setGroupId(userGroupEntity.getGroupId());
        groupUserEntity.setUserId(userGroupEntity.getUserId());
        groupUserEntity.setCreatedBy(userGroupEntity.getCreatedBy());
        groupUserEntity.setCreatedTime(userGroupEntity.getCreatedTime());
        groupUserDAO.save(groupUserEntity);

        return get(new UserGroupId(userGroupEntity.getId()));
    }

    @Override
    public UserGroup update(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext());
        userGroupDAO.update(userGroupEntity);

        GroupUserEntity groupUserEntity =
                groupUserDAO.findByGroupId(userGroupEntity.getGroupId(), userGroupEntity.getUserId());
        groupUserEntity.setUpdatedBy(userGroupEntity.getUpdatedBy());
        groupUserEntity.setUpdatedTime(userGroupEntity.getUpdatedTime());
        groupUserDAO.update(groupUserEntity);

        return get(entity.getId());
    }

    @Override
    public UserGroup get(UserGroupId id) {
        return modelMapper.toUserGroup(userGroupDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<UserGroup> search(UserGroupListOptions getOption) {
        List entities = userGroupDAO.search(getOption.getUserId().getValue(), getOption);

        List<UserGroup> results = new ArrayList<UserGroup>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserGroup((UserGroupEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserGroupId id) {
        UserGroupEntity entity = userGroupDAO.get(id.getValue());

        GroupUserEntity groupUserEntity = groupUserDAO.findByGroupId(entity.getGroupId(), entity.getUserId());
        groupUserDAO.delete(groupUserEntity.getId());
        userGroupDAO.delete(id.getValue());
    }
}
