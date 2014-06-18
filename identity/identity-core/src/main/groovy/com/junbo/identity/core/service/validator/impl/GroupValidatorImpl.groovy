/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.GroupId
import com.junbo.identity.core.service.validator.GroupValidator
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 3/14/14.
 */
@CompileStatic
class GroupValidatorImpl implements GroupValidator {
    // todo:    Need to implement group search must have organization id validation

    private GroupRepository groupRepository
    
    private Integer groupValueMinLength

    private Integer groupValueMaxLength

    private OrganizationRepository organizationRepository

    @Required
    void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository
    }

    @Required
    void setGroupValueMinLength(Integer groupValueMinLength) {
        this.groupValueMinLength = groupValueMinLength
    }

    @Required
    void setGroupValueMaxLength(Integer groupValueMaxLength) {
        this.groupValueMaxLength = groupValueMaxLength
    }

    @Required
    void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository
    }

    @Override
    Promise<Group> validateForGet(GroupId groupId) {
        if (groupId == null || groupId.value == null) {
            throw new IllegalArgumentException('groupId is null')
        }

        return groupRepository.get(groupId).then { Group group ->
            if (group == null) {
                throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
            }

            return Promise.pure(group)
        }
    }

    @Override
    Promise<Void> validateForSearch(GroupListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.organizationId == null) {
            throw AppErrors.INSTANCE.parameterRequired('organizaionId').exception()
        }

        // todo:    How to define userId
        if (options.name == null && options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('name').exception()
        }

        if (options.name != null && options.userId != null) {
            throw AppErrors.INSTANCE.parameterInvalid('name and userId can\'t search together.').exception()
        }
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Group group) {
        basicCheckForGroup(group)
        if (group.active != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('active').exception()
        }
        if (group.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (group.organizationId == null) {
            throw AppErrors.INSTANCE.fieldRequired('organization').exception()
        }

        return organizationRepository.get(group.organizationId).then { Organization organization ->
            if (organization == null) {
                throw AppErrors.INSTANCE.fieldInvalid('organization').exception()
            }

            return groupRepository.searchByOrganizationIdAndName(group.organizationId, group.name, Integer.MAX_VALUE, 0).then { Group existing ->
                if (existing != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
                }

                group.setActive(true)
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(GroupId groupId, Group group, Group oldGroup) {
        basicCheckForGroup(group)
        if (groupId == null || groupId.value == null) {
            throw new IllegalArgumentException()
        }
        if (group.active == null) {
            throw AppErrors.INSTANCE.fieldRequired('active').exception()
        }
        if (group.id == null || ((GroupId)group.id).value == null) {
            throw AppErrors.INSTANCE.fieldRequired('id').exception()
        }
        if (groupId.value != ((GroupId)group.id).value) {
            throw AppErrors.INSTANCE.fieldInvalid('groupId', group.id.toString()).exception()
        }

        if (group.organizationId != oldGroup.organizationId) {
            throw AppErrors.INSTANCE.fieldInvalid('organizationId').exception()
        }

        if (group.name != oldGroup.name) {
            return groupRepository.searchByOrganizationIdAndName(group.organizationId, group.name, Integer.MAX_VALUE, 0).then { Group existing ->
                if (existing != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
                }

                return Promise.pure(null)
            }
        }
        return Promise.pure(null)
    }

    private void basicCheckForGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException()
        }
        if (StringUtils.isEmpty(group.name)) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }
        if (group.name.length() > groupValueMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('name', groupValueMaxLength).exception()
        }
        if (group.name.length() < groupValueMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('name', groupValueMinLength).exception()
        }
    }
}
