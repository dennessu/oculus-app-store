/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.validator

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.common.error.AppError
import com.junbo.common.id.Id
import com.junbo.common.id.RoleId
import com.junbo.authorization.spec.error.AppErrors
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.common.id.util.IdUtil
import com.junbo.common.util.IdFormatter
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

        Id resourceId = IdUtil.fromHref(role.target.filterLink.href)

        if (resourceId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('target.filterLink').exception()
        }

        if (IdFormatter.encodeId(resourceId) != role.target.filterLink.id) {
            throw AppErrors.INSTANCE.fieldInvalid('target.filterLink').exception()
        }

        return roleRepository.findByRoleName(role.name, role.target.targetType,
                role.target.filterType, role.target.filterLink.href).then { Role existing ->
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

        return roleRepository.findByRoleName(role.name, role.target.targetType,
                role.target.filterType, role.target.filterLink.href).then { Role existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForList(RoleListOptions options) {
        Assert.notNull(options)

        if (options.name == null || options.filterType == null || options.targetType == null) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }

        return Promise.pure(null)
    }
}
