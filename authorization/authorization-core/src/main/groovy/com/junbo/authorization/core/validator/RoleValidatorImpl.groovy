/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.validator

import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.common.id.Id
import com.junbo.common.id.RoleId
import com.junbo.authorization.spec.error.AppErrors
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * RoleValidatorImpl.
 */
@CompileStatic
class RoleValidatorImpl implements RoleValidator {
    private static final String CREATE_RIGHT = 'create'
    private RoleRepository roleRepository

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

    @Override
    Promise<Void> validateForCreate(Role role) {
        Assert.notNull(role, 'role is null')

        if (StringUtils.isEmpty(role.name)) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }

        if (role.target == null) {
            throw AppErrors.INSTANCE.fieldRequired('target').exception()
        }

        if (role.target.targetType == null) {
            throw AppErrors.INSTANCE.fieldRequired('target.targetType').exception()
        }

        if (role.target.filterType == null) {
            throw AppErrors.INSTANCE.fieldRequired('target.filterType').exception()
        }

        if (role.target.filterLink == null) {
            throw AppErrors.INSTANCE.fieldRequired('target.filterLink').exception()
        }

        Id resourceId = IdUtil.fromLink(role.target.filterLink)

        if (resourceId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('target.filterLink').exception()
        }

        role.target.filterLinkIdType = resourceId.class.canonicalName
        role.target.filterLinkId = resourceId.value

        return roleRepository.findByRoleName(role.name, role.target.targetType,
                role.target.filterType, role.target.filterLinkIdType, role.target.filterLinkId).then { Role existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForGet(RoleId roleId) {
        if (roleId == null) {
            throw AppErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForUpdate(Role role, Role oldRole) {
        Assert.notNull(role, 'role is null')
        if (StringUtils.isEmpty(role.name)) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }

        if (role.id == null || ((RoleId) role.id).value == null) {
            throw AppErrors.INSTANCE.fieldRequired('id').exception()
        }

        if (role.id != oldRole.id) {
            throw AppErrors.INSTANCE.fieldInvalid('roleId').exception()
        }

        Id resourceId = IdUtil.fromLink(role.target.filterLink)

        if (resourceId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('target.filterLink').exception()
        }

        role.target.filterLinkIdType = resourceId.class.canonicalName
        role.target.filterLinkId = resourceId.value

        return roleRepository.findByRoleName(role.name, role.target.targetType,
                role.target.filterType, role.target.filterLinkIdType, role.target.filterLinkId).then { Role existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForList(RoleListOptions options) {
        Assert.notNull(options)

        if (options.name == null) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }

        if (options.filterType == null) {
            throw AppErrors.INSTANCE.fieldRequired('filterType').exception()
        }

        if (options.targetType == null) {
            throw AppErrors.INSTANCE.fieldRequired('targetType').exception()
        }

        if (options.filterLink == null) {
            throw AppErrors.INSTANCE.fieldRequired('filterLink').exception()
        }

        Id resourceId = IdUtil.fromLink(new Link(href: options.filterLink))

        if (resourceId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('filterLink').exception()
        }

        options.filterLinkIdType = resourceId.class.canonicalName
        options.filterLinkId = resourceId.value

        return Promise.pure(null)
    }
}
