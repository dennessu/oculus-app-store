/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.RoleId
import com.junbo.identity.core.service.validator.RoleValidator
import com.junbo.identity.data.repository.RoleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Role
import com.junbo.identity.spec.v1.option.list.RoleListOptions
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

        if (StringUtils.isEmpty(role.resourceType)) {
            throw AppErrors.INSTANCE.fieldRequired('resourceType').exception()
        }

        if (role.resourceId == null) {
            throw AppErrors.INSTANCE.fieldRequired('resourceId').exception()
        }

        roleRepository.findByRoleName(role.name, role.resourceType,
                role.resourceId, role.subResourceType).then { Role existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Role> validateForGet(RoleId roleId) {
        if (roleId == null) {
            throw AppErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        return roleRepository.get(roleId).then { Role role ->
            if (role == null) {
                throw AppErrors.INSTANCE.roleNotFound(roleId).exception()
            }

            return Promise.pure(role)
        }
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

        roleRepository.findByRoleName(role.name, role.resourceType,
                role.resourceId, role.subResourceType).then { Role existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }
            return Promise.pure(null)
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForList(RoleListOptions options) {
        Assert.notNull(options)

        if (options.name == null || options.resourceId == null || options.resourceType == null) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }

        return Promise.pure(null)
    }
}
