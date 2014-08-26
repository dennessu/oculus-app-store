/*
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
*
* Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
*/
package com.junbo.authorization.rest.resource
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.authorization.core.authorize.callback.RoleAuthorizeCallbackFactory
import com.junbo.authorization.core.filter.RoleAssignmentFilter
import com.junbo.authorization.core.validator.RoleAssignmentValidator
import com.junbo.authorization.db.repository.RoleAssignmentRepository
import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.authorization.spec.resource.RoleAssignmentResource
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.UniversalId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
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

    private RoleRepository roleRepository

    private RoleAssignmentRepository roleAssignmentRepository

    private RoleAssignmentValidator roleAssignmentValidator

    private RoleAssignmentFilter roleAssignmentFilter

    private AuthorizeService authorizeService

    private RoleAuthorizeCallbackFactory roleAuthorizeCallbackFactory

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

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
    void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService
    }

    @Required
    void setRoleAuthorizeCallbackFactory(RoleAuthorizeCallbackFactory roleAuthorizeCallbackFactory) {
        this.roleAuthorizeCallbackFactory = roleAuthorizeCallbackFactory
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        RoleAssignment filtered = roleAssignmentFilter.filterForPost(roleAssignment)
        return roleAssignmentValidator.validateForCreate(filtered).then { Role role ->
            def callback = roleAuthorizeCallbackFactory.create(role)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }
                return roleAssignmentRepository.create(filtered).then { RoleAssignment newRoleAssignment ->
                    Created201Marker.mark((UniversalId) (newRoleAssignment.id))

                    return Promise.pure(roleAssignmentFilter.filterForGet(newRoleAssignment))
                }
            }
        }
    }

    @Override
    Promise<RoleAssignment> get(RoleAssignmentId roleAssignmentId) {
        return internalGet(roleAssignmentId).then { RoleAssignment roleAssignment ->
            def callback = roleAuthorizeCallbackFactory.create(roleAssignment.getRoleId())
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound('role-assignment', roleAssignmentId.toString()).exception()
                }
                return Promise.pure(roleAssignment)
            }
        }
    }

    Promise<RoleAssignment> internalGet(RoleAssignmentId roleAssignmentId) {
        return roleAssignmentValidator.validateForGet(roleAssignmentId).then {
            return roleAssignmentRepository.get(roleAssignmentId).then { RoleAssignment roleAssignment ->
                if (roleAssignment == null) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound('role-assignment', roleAssignmentId.toString()).exception()
                }

                return Promise.pure(roleAssignmentFilter.filterForGet(roleAssignment))
            }
        }
    }

    @Override
    Promise<Void> delete(RoleAssignmentId roleAssignmentId) {
        return internalGet(roleAssignmentId).then { RoleAssignment roleAssignment ->
            def callback = roleAuthorizeCallbackFactory.create(roleAssignment.getRoleId())
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return roleAssignmentValidator.validateForDelete(roleAssignment).then {
                    return roleAssignmentRepository.delete(roleAssignmentId).then {
                        return Promise.pure(null)
                    }
                }
            }
        }
    }

    @Override
    Promise<Results<RoleAssignment>> list(RoleAssignmentListOptions options) {
        return roleAssignmentValidator.validateForList(options).then {
            return roleAssignmentRepository.findByRoleIdAssignee(options.roleId, options.assigneeIdType,
                    options.assigneeId).then { RoleAssignment roleAssignment ->
                def results = new Results<RoleAssignment>(items: [])

                RoleAssignment filtered = roleAssignmentFilter.filterForGet(roleAssignment)

                if (filtered != null) {
                    def callback = roleAuthorizeCallbackFactory.create(filtered.getRoleId())
                    return RightsScope.with(authorizeService.authorize(callback)) {
                        if (AuthorizeContext.hasRights('read')) {
                            results.items.add(filtered)
                        }
                        return Promise.pure(results)
                    }
                }
                return Promise.pure(results)
            }
        }
    }
}
