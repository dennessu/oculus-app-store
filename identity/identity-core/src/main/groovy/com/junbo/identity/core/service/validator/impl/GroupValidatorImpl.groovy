/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.GroupId
import com.junbo.identity.core.service.validator.GroupValidator
import com.junbo.identity.service.GroupService
import com.junbo.identity.service.OrganizationService
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
    private GroupService groupService

    private Integer groupValueMinLength

    private Integer groupValueMaxLength

    private OrganizationService organizationService

    @Required
    void setGroupService(GroupService groupService) {
        this.groupService = groupService
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
    void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService
    }

    @Override
    Promise<Group> validateForGet(GroupId groupId) {
        if (groupId == null || groupId.value == null) {
            throw new IllegalArgumentException('groupId is null')
        }

        return groupService.get(groupId).then { Group group ->
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

        if (options.userId != null) {
            if (options.name != null || options.organizationId != null) {
                throw AppCommonErrors.INSTANCE.parameterInvalid('name and organizationId', 'userId can\'t be search together with organizationId or name').exception()
            }

            return Promise.pure(null)
        }

        if (options.organizationId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('organizaionId or userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Group group) {
        basicCheckForGroup(group)
        if (group.active != null && !group.active) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('active', 'active must be true during group creation').exception()
        }
        if (group.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        if (group.organizationId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('organization').exception()
        }

        return organizationService.get(group.organizationId).then { Organization organization ->
            if (organization == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('organization').exception()
            }

            return groupService.searchByOrganizationIdAndName(group.organizationId, group.name, 1, 0).then { Group existing ->
                if (existing != null) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('name').exception()
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
            throw AppCommonErrors.INSTANCE.fieldRequired('active').exception()
        }
        if (group.id == null || ((GroupId)group.id).value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }
        if (groupId.value != ((GroupId)group.id).value) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('groupId', group.id.toString(), groupId).exception()
        }

        if (group.organizationId != oldGroup.organizationId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('organizationId').exception()
        }

        if (group.name != oldGroup.name) {
            return groupService.searchByOrganizationIdAndName(group.organizationId, group.name, Integer.MAX_VALUE, 0).then { Group existing ->
                if (existing != null) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('name').exception()
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
            throw AppCommonErrors.INSTANCE.fieldRequired('name').exception()
        }
        if (group.name.length() > groupValueMaxLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('name', groupValueMaxLength).exception()
        }
        if (group.name.length() < groupValueMinLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('name', groupValueMinLength).exception()
        }
    }
}
