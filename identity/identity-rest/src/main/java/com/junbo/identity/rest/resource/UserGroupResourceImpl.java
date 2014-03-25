/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserGroupService;
import com.junbo.identity.spec.model.common.ResultsUtil;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.entity.UserGroupGetOptions;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.identity.spec.resource.UserGroupResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserGroupResourceImpl implements UserGroupResource {

    @Autowired
    private UserGroupService userGroupService;

    @Override
    public Promise<UserGroup> create(UserId userId, UserGroup userGroup) {
        return Promise.pure(userGroupService.save(userId, userGroup));
    }

    @Override
    public Promise<UserGroup> put(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {
        return Promise.pure(userGroupService.update(userId, userGroupId, userGroup));
    }

    @Override
    public Promise<UserGroup> patch(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {
        return null;
    }

    @Override
    public Promise<UserGroup> delete(UserId userId, UserGroupId userGroupId) {
        return null;
    }

    @Override
    public Promise<UserGroup> get(UserId userId, UserGroupId userGroupId, @BeanParam UserGroupGetOptions getOptions) {
        return Promise.pure(userGroupService.get(userId, userGroupId));
    }

    @Override
    public Promise<Results<UserGroup>> list(UserId userId, @BeanParam UserGroupListOptions listOptions) {
        if(listOptions == null) {
            listOptions = new UserGroupListOptions();
        }
        listOptions.setUserId(userId);
        List<UserGroup> userGroupList = userGroupService.search(listOptions);
        return Promise.pure(ResultsUtil.init(userGroupList,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }
}
