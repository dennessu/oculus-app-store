/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.group.GroupService;
import com.junbo.identity.spec.model.users.Group;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.entity.GroupGetOptions;
import com.junbo.identity.spec.options.list.GroupListOptions;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.identity.spec.resource.GroupResource;
import com.junbo.langur.core.promise.Promise;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@Scope("prototype")
@Transactional
@CompileStatic
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
    public Promise<Results<Group>> list(GroupListOptions listOptions) {
        List<Group> groups = groupService.search(listOptions);
        Results<Group> results = new Results<Group>();
        results.setItems(groups);
        return Promise.pure(results);
    }

    @Override
    public Promise<Results<UserGroup>> listUserGroups(GroupId groupId, UserGroupListOptions listOptions) {
        return null;
    }
}
