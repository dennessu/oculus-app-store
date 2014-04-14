/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.RoleAssignmentId
import com.junbo.identity.core.service.validator.RoleAssignmentValidator
import com.junbo.identity.data.repository.RoleAssignmentRepository
import com.junbo.identity.data.repository.RoleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Role
import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.identity.spec.v1.option.list.RoleAssignmentListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * RoleAssignmentValidatorImpl.
 */
@CompileStatic
class RoleAssignmentValidatorImpl implements RoleAssignmentValidator {
    private RoleAssignmentRepository roleAssignmentRepository

    private RoleRepository roleRepository

    @Required
    void setRoleAssignmentRepository(RoleAssignmentRepository roleAssignmentRepository) {
        this.roleAssignmentRepository = roleAssignmentRepository
    }

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

    @Override
    Promise<Void> validateForCreate(RoleAssignment roleAssignment) {
        Assert.notNull(roleAssignment, 'roleAssignment is null')
        if (roleAssignment.roleId == null) {
            throw AppErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        roleRepository.get(roleAssignment.roleId).then { Role role ->
            if (role == null) {
                throw AppErrors.INSTANCE.fieldInvalid('roleId').exception()
            }

            if (StringUtils.isEmpty(roleAssignment.assigneeType)) {
                throw AppErrors.INSTANCE.fieldRequired('assigneeType').exception()
            }

            if (roleAssignment.assigneeId == null) {
                throw AppErrors.INSTANCE.fieldRequired('assigneeId').exception()
            }

            roleAssignmentRepository.findByRoleIdAssignee(roleAssignment.roleId, roleAssignment.assigneeType,
                    roleAssignment.assigneeId).then { RoleAssignment existing ->
                if (existing != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('roleAssignment').exception()
                }
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<RoleAssignment> validateForGet(RoleAssignmentId roleAssignmentId) {
        if (roleAssignmentId == null) {
            throw AppErrors.INSTANCE.fieldRequired('roleAssignmentId').exception()
        }
        return roleAssignmentRepository.get(roleAssignmentId).then { RoleAssignment roleAssignment ->
            if (roleAssignment == null) {
                throw AppErrors.INSTANCE.roleAssignmentNotFound(roleAssignmentId).exception()
            }

            return Promise.pure(roleAssignment)
        }
    }

    @Override
    Promise<Void> validateForUpdate(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment) {
        Assert.notNull(roleAssignment, 'roleAssignment is null')
        if (roleAssignment.id == null || ((RoleAssignmentId) roleAssignment.id).value == null) {
            throw AppErrors.INSTANCE.fieldRequired('roleAssignmentId').exception()
        }

        if (roleAssignment.id != oldRoleAssignment.id) {
            throw AppErrors.INSTANCE.fieldInvalid('roleAssignmentId').exception()
        }

        if (roleAssignment.roleId == null) {
            throw AppErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        roleRepository.get(roleAssignment.roleId).then { Role role ->
            if (role == null) {
                throw AppErrors.INSTANCE.fieldInvalid('roleId').exception()
            }

            if (StringUtils.isEmpty(roleAssignment.assigneeType)) {
                throw AppErrors.INSTANCE.fieldRequired('assigneeType').exception()
            }

            if (StringUtils.isEmpty(roleAssignment.assigneeType)) {
                throw AppErrors.INSTANCE.fieldRequired('assigneeType').exception()
            }

            if (roleAssignment.assigneeId == null) {
                throw AppErrors.INSTANCE.fieldRequired('assigneeId').exception()
            }

            roleAssignmentRepository.findByRoleIdAssignee(roleAssignment.roleId, roleAssignment.assigneeType,
                    roleAssignment.assigneeId).then { RoleAssignment existing ->
                if (existing != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('roleAssignment').exception()
                }
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForList(RoleAssignmentListOptions options) {
        Assert.notNull(options)
        if (options.roleId == null || options.assigneeId == null || StringUtils.isEmpty(options.assigneeType)) {
            throw AppErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        return Promise.pure(null)
    }
}
