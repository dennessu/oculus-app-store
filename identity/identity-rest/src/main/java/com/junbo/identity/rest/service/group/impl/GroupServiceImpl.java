/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.group.impl;

import com.junbo.common.id.GroupId;
import com.junbo.identity.data.dao.GroupDAO;
import com.junbo.identity.rest.service.group.GroupService;
import com.junbo.identity.rest.service.validator.GroupValidator;
import com.junbo.identity.spec.model.options.GroupGetOption;
import com.junbo.identity.spec.model.users.Group;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Component
public class GroupServiceImpl implements GroupService{
    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private IdGeneratorFacade idGeneratorFacade;

    @Override
    public Group get(GroupId groupId) {
        groupValidator.validateGet(groupId);
        return groupDAO.get(groupId);
    }

    @Override
    public Group create(Group group) {
        groupValidator.validateCreate(group);
        group.setId(new GroupId(idGeneratorFacade.nextId(GroupId.class)));
        return groupDAO.save(group);
    }

    @Override
    public Group update(GroupId groupId, Group group) {
        groupValidator.validateUpdate(groupId, group);
        return groupDAO.update(group);
    }

    @Override
    public Group patch(GroupId groupId, Group group) {
        groupValidator.validateUpdate(groupId, group);
        Group existing = get(groupId);
        if(!StringUtils.isEmpty(group.getValue())) {
            existing.setValue(group.getValue());
        }
        if(group.getActive() != null) {
            existing.setActive(group.getActive());
        }
        return groupDAO.update(existing);
    }

    @Override
    public List<Group> search(GroupGetOption getOption) {
        return groupDAO.search(getOption == null ? new GroupGetOption() : getOption);
    }
}
