/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.UserGroupGetOptions;
import com.junbo.identity.spec.options.UserGroupListOptions;
import com.junbo.identity.spec.resource.UserGroupResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserGroupResourceImpl implements UserGroupResource {
    @Override
    public Promise<UserGroup> create(UserId userId, UserGroup userGroup) {
        return null;
    }

    @Override
    public Promise<UserGroup> put(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {
        return null;
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
        return null;
    }

    @Override
    public Promise<ResultList<UserGroup>> list(UserId userId, @BeanParam UserGroupListOptions listOptions) {
        return null;
    }
}
