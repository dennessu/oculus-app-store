/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.GroupId;
import com.junbo.identity.rest.v1.service.group.GroupService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.resource.v1.resource.GroupResource;
import com.junbo.identity.spec.v1.model.options.GroupGetOption;
import com.junbo.identity.spec.v1.model.users.Group;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
@Component
public class GroupResourceImpl implements GroupResource {
    @Autowired
    private GroupService groupService;

    @Override
    public Promise<Group> create(Group group) {
        return Promise.pure(groupService.create(group));
    }

    @Override
    public Promise<Group> update(GroupId groupId, Group group) {
        return Promise.pure(groupService.update(groupId, group));
    }

    @Override
    public Promise<Group> patch(GroupId groupId, Group group) {
        return Promise.pure(groupService.patch(groupId, group));
    }

    @Override
    public Promise<Group> get(GroupId groupId) {
        return Promise.pure(groupService.get(groupId));
    }

    @Override
    public Promise<ResultList<Group>> list(@BeanParam GroupGetOption getOption) {
        ResultList<Group> groups = ResultListUtil.init(groupService.search(getOption), getOption.getLimit());
        return Promise.pure(groups);
    }
}
