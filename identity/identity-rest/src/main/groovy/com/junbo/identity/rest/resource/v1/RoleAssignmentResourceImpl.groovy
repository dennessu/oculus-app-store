/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.RoleAssignmentFilter
import com.junbo.identity.core.service.validator.RoleAssignmentValidator
import com.junbo.identity.data.repository.RoleAssignmentRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Role
import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.identity.spec.v1.option.list.RoleAssignmentListOptions
import com.junbo.identity.spec.v1.resource.RoleAssignmentResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

/**
 * RoleAssignmentResourceImpl.
 */
@CompileStatic
@Transactional
class RoleAssignmentResourceImpl implements RoleAssignmentResource {
    private RoleAssignmentRepository roleAssignmentRepository

    private RoleAssignmentValidator roleAssignmentValidator

    private RoleAssignmentFilter roleAssignmentFilter

    private Created201Marker created201Marker

    @Required
    void setRoleAssignmentRepository(RoleAssignmentRepository roleAssignmentRepository) {
        this.roleAssignmentRepository = roleAssignmentRepository
    }

    @Required
    void setRoleAssignmentValidator(RoleAssignmentValidator roleAssignmentValidator) {
        this.roleAssignmentValidator = roleAssignmentValidator
    }

    @Required
    void setRoleAssignmentFilter(RoleAssignmentFilter roleAssignmentFilter) {
        this.roleAssignmentFilter = roleAssignmentFilter
    }

    @Required
    void setCreated201Marker(Created201Marker created201Marker) {
        this.created201Marker = created201Marker
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        RoleAssignment filtered = roleAssignmentFilter.filterForCreate(roleAssignment)
        return roleAssignmentValidator.validateForCreate(filtered).then {
            return roleAssignmentRepository.create(filtered).then { RoleAssignment newRoleAssignment ->
                created201Marker.mark((Id) (newRoleAssignment.id))

                return Promise.pure(roleAssignmentFilter.filterForGet(newRoleAssignment, null))
            }
        }
    }

    @Override
    Promise<Role> get(RoleAssignmentId roleAssignmentId) {
        return roleAssignmentValidator.validateForGet(roleAssignmentId).then { RoleAssignment roleAssignment ->
            if (roleAssignment == null) {
                throw AppErrors.INSTANCE.roleAssignmentNotFound(roleAssignmentId).exception()
            }

            return Promise.pure(roleAssignmentFilter.filterForGet(roleAssignment, null))
        }
    }

    @Override
    Promise<Role> patch(RoleAssignmentId roleAssignmentId, RoleAssignment roleAssignment) {
        return roleAssignmentValidator.validateForGet(roleAssignmentId).then { RoleAssignment oldRoleAssignment ->
            if (oldRoleAssignment == null) {
                throw AppErrors.INSTANCE.roleAssignmentNotFound(roleAssignmentId).exception()
            }

            RoleAssignment filtered = roleAssignmentFilter.filterForPatch(roleAssignment, oldRoleAssignment)

            return roleAssignmentValidator.validateForUpdate(filtered, oldRoleAssignment).then {
                return roleAssignmentRepository.update(filtered).then { RoleAssignment newRoleAssignment ->
                    return Promise.pure(roleAssignmentFilter.filterForGet(newRoleAssignment, null))
                }
            }
        }
    }

    @Override
    Promise<Role> put(RoleAssignmentId roleAssignmentId, RoleAssignment roleAssignment) {
        return roleAssignmentValidator.validateForGet(roleAssignmentId).then { RoleAssignment oldRoleAssignment ->
            if (oldRoleAssignment == null) {
                throw AppErrors.INSTANCE.roleAssignmentNotFound(roleAssignmentId).exception()
            }

            RoleAssignment filtered = roleAssignmentFilter.filterForPut(roleAssignment, oldRoleAssignment)

            return roleAssignmentValidator.validateForUpdate(filtered, oldRoleAssignment).then {
                return roleAssignmentRepository.update(filtered).then { RoleAssignment newRoleAssignment ->
                    return Promise.pure(roleAssignmentFilter.filterForGet(newRoleAssignment, null))
                }
            }
        }
    }

    @Override
    Promise<Results<RoleAssignment>> list(RoleAssignmentListOptions options) {
        return roleAssignmentValidator.validateForList(options).then {
            return roleAssignmentRepository.findByRoleIdAssignee(options.roleId, options.assigneeType, options.assigneeId)
                    .then { RoleAssignment roleAssignment ->
                def results = new Results<RoleAssignment>(items: [])

                RoleAssignment filtered = roleAssignmentFilter.filterForGet(roleAssignment, null)

                if (filtered != null) {
                    results.items.add(filtered)
                }

                return Promise.pure(results)
            }
        }
    }
}
