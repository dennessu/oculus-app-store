/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.group.impl;

import com.junbo.common.id.GroupId;
import com.junbo.identity.data.repository.GroupRepository;
import com.junbo.identity.core.service.group.GroupService;
import com.junbo.identity.core.service.validator.GroupValidator;
import com.junbo.identity.spec.options.list.GroupListOptions;
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
    private GroupRepository groupRepository;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private IdGeneratorFacade idGeneratorFacade;

    @Override
    public Group get(GroupId groupId) {
        groupValidator.validateGet(groupId);
        return groupRepository.get(groupId);
    }

    @Override
    public Group create(Group group) {
        groupValidator.validateCreate(group);
        group.setId(new GroupId(idGeneratorFacade.nextId(GroupId.class)));
        return groupRepository.save(group);
    }

    @Override
    public Group update(GroupId groupId, Group group) {
        groupValidator.validateUpdate(groupId, group);
        return groupRepository.update(group);
    }

    @Override
    public Group patch(GroupId groupId, Group group) {
        groupValidator.validateUpdate(groupId, group);
        Group existing = get(groupId);
        if(!StringUtils.isEmpty(group.getName())) {
            existing.setName(group.getName());
        }
        if(group.getActive() != null) {
            existing.setActive(group.getActive());
        }
        return groupRepository.update(existing);
    }

    @Override
    public List<Group> search(GroupListOptions getOption) {
        return groupRepository.findByValue(getOption.getValue());
    }
}
