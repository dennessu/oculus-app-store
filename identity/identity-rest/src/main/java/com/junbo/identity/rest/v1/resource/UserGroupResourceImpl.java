/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserGroupResource;
import com.junbo.identity.spec.v1.model.options.UserGroupGetOption;
import com.junbo.identity.spec.v1.model.users.UserGroup;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserGroupResourceImpl implements UserGroupResource {
    @Override
    public Promise<UserGroup> create(GroupId groupId, UserGroup userGroup) {
        return null;
    }

    @Override
    public Promise<UserGroup> update(GroupId groupId, UserId userId, UserGroup userGroup) {
        return null;
    }

    @Override
    public Promise<UserGroup> patch(GroupId groupId, UserId userId, UserGroup userGroup) {
        return null;
    }

    @Override
    public Promise<UserGroup> get(GroupId groupId, UserId userId) {
        return null;
    }

    @Override
    public Promise<UserGroup> delete(GroupId groupId, UserId userId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserGroup>> list(GroupId groupId, @BeanParam UserGroupGetOption getOption) {
        return null;
    }
}
