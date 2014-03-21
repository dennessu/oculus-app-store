/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.GroupId;
import com.junbo.identity.core.service.validator.GroupValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.Group;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by liangfu on 3/14/14.
 */
@Component
public class GroupValidatorImpl implements GroupValidator {

    @Override
    public void validateGet(GroupId groupId) {
        if(groupId == null || groupId.getValue() == null) {
            throw AppErrors.INSTANCE.invalidParameter("groupId is null.").exception();
        }
    }

    @Override
    public void validateCreate(Group group) {
        basicCheckForGroup(group);
        if(group.getId() != null) {
            throw AppErrors.INSTANCE.invalidParameter("group.id isn't null.").exception();
        }
    }

    @Override
    public void validateUpdate(GroupId groupId, Group group) {
        basicCheckForGroup(group);
        if(groupId == null || groupId.getValue() == null) {
            throw AppErrors.INSTANCE.invalidParameter("groupId is null.").exception();
        }
        if(group.getId() == null || group.getId().getValue() == null) {
            throw AppErrors.INSTANCE.invalidParameter("group.id is null.").exception();
        }
        if(groupId.getValue().equals(group.getId().getValue())) {
            throw AppErrors.INSTANCE.invalidParameter("groupId doesn't equal to group.id.").exception();
        }
    }

    @Override
    public void validateDelete(GroupId groupId) {
        if(groupId == null || groupId.getValue() == null) {
            throw AppErrors.INSTANCE.invalidParameter("groupId is null.").exception();
        }
    }

    private void basicCheckForGroup(Group group) {
        if(group == null) {
            throw AppErrors.INSTANCE.invalidParameter("group is null.").exception();
        }
        if(StringUtils.isEmpty(group.getName())) {
            throw AppErrors.INSTANCE.invalidParameter("group.name is null.").exception();
        }
        if(group.getActive() == null) {
            throw AppErrors.INSTANCE.invalidParameter("group.active is null").exception();
        }
    }
}
