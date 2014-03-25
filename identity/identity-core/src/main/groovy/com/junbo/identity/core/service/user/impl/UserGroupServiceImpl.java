/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.user.UserGroupService;
import com.junbo.identity.core.service.validator.UserGroupValidator;
import com.junbo.identity.data.repository.UserGroupRepository;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
@Transactional
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupValidator validator;

    @Override
    public UserGroup save(UserId userId, UserGroup userGroup) {
        validator.validateCreate(userId, userGroup);
        userGroup.setUserId(userId);
        return userGroupRepository.save(userGroup);
    }

    @Override
    public UserGroup update(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {
        validator.validateUpdate(userId, userGroupId, userGroup);
        userGroup.setUserId(userId);
        return userGroupRepository.update(userGroup);
    }

    @Override
    public UserGroup patch(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {
        return null;
    }

    @Override
    public UserGroup get(UserId userId, UserGroupId userGroupId) {
        validator.validateResourceAccessible(userId, userGroupId);
        return userGroupRepository.get(userGroupId);
    }

    @Override
    public List<UserGroup> search(UserGroupListOptions getOption) {
        return userGroupRepository.search(getOption);
    }

    @Override
    public void delete(UserId userId, UserGroupId userGroupId) {

    }
}
