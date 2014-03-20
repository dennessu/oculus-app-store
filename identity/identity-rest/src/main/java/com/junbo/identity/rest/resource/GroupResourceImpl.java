/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.GroupId;
import com.junbo.identity.core.service.group.GroupService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.Group;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.GroupGetOptions;
import com.junbo.identity.spec.options.GroupListOptions;
import com.junbo.identity.spec.options.UserGroupListOptions;
import com.junbo.identity.spec.resource.GroupResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class GroupResourceImpl implements GroupResource {
    @Autowired
    private GroupService groupService;

    @Override
    public Promise<Group> create(Group group) {
        return Promise.pure(groupService.create(group));
    }

    @Override
    public Promise<Group> put(GroupId groupId, Group group) {
        return Promise.pure(groupService.update(groupId, group));
    }

    @Override
    public Promise<Group> patch(GroupId groupId, Group group) {
        return Promise.pure(groupService.patch(groupId, group));
    }

    @Override
    public Promise<Group> get(GroupId groupId, GroupGetOptions groupGetOptions) {
        return Promise.pure(groupService.get(groupId));
    }

    @Override
    public Promise<ResultList<Group>> list(GroupListOptions listOptions) {
        return Promise.pure(null);
    }

    @Override
    public Promise<ResultList<UserGroup>> listUserGroups(GroupId groupId, UserGroupListOptions listOptions) {
        return null;
    }
}
