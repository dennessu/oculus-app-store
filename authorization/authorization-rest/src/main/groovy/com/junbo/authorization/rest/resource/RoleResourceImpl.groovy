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
import com.junbo.authorization.core.filter.RoleFilter
import com.junbo.authorization.core.validator.RoleValidator
import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.authorization.spec.error.AppErrors
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.authorization.spec.resource.RoleResource
import com.junbo.common.id.Id
import com.junbo.common.id.RoleId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.transaction.annotation.Transactional

/**
 * RoleResourceImpl.
 */
@CompileStatic
@Transactional
class RoleResourceImpl implements RoleResource {
    private RoleRepository roleRepository

    private RoleValidator roleValidator

    private RoleFilter roleFilter

    private AuthorizeService authorizeService

    private RoleAuthorizeCallbackFactory roleAuthorizeCallbackFactory

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

    @Required
    void setRoleValidator(RoleValidator roleValidator) {
        this.roleValidator = roleValidator
    }

    @Required
    void setRoleFilter(RoleFilter roleFilter) {
        this.roleFilter = roleFilter
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
    Promise<Role> create(Role role) {
        def callback = roleAuthorizeCallbackFactory.create(role)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('write')) {
                throw AppErrors.INSTANCE.forbidden().exception()
            }

            Role filtered = roleFilter.filterForPost(role)
            return roleValidator.validateForCreate(filtered).then {
                return roleRepository.create(filtered).then { Role newRole ->
                    Created201Marker.mark((Id) newRole.id)

                    return Promise.pure(roleFilter.filterForGet(newRole))
                }
            }
        }
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        return roleValidator.validateForGet(roleId).then {
            return roleRepository.get(roleId).then { Role role ->
                if (role == null) {
                    throw AppErrors.INSTANCE.roleNotFound(roleId).exception()
                }

                return Promise.pure(roleFilter.filterForGet(role))
            }
        }
    }

    @Override
    Promise<Role> patch(RoleId roleId, Role role) {
        return get(roleId).then { Role oldRole ->
            if (oldRole == null) {
                throw AppErrors.INSTANCE.roleNotFound(roleId).exception()
            }

            Role filtered = roleFilter.filterForPatch(role, oldRole)

            return roleValidator.validateForUpdate(filtered, oldRole).then {
                return roleRepository.update(filtered).then { Role newRole ->
                    return Promise.pure(roleFilter.filterForGet(newRole))
                }
            }
        }
    }

    @Override
    Promise<Role> put(RoleId roleId, Role role) {
        return get(roleId).then { Role oldRole ->
            if (oldRole == null) {
                throw AppErrors.INSTANCE.roleNotFound(roleId).exception()
            }

            Role filtered = roleFilter.filterForPut(role, oldRole)

            return roleValidator.validateForUpdate(filtered, oldRole).then {
                return roleRepository.update(filtered).then { Role newRole ->
                    return Promise.pure(roleFilter.filterForGet(newRole))
                }
            }
        }
    }

    @Override
    Promise<Results<Role>> list(RoleListOptions options) {
        return roleValidator.validateForList(options).then {
            def results = new Results<Role>(items: [])

            return roleRepository.findByRoleName(options.name, options.targetType,
                    options.filterType, options.filterLinkIdType, options.filterLinkId).then { Role role ->
                Role filtered = roleFilter.filterForGet(role)

                if (filtered != null) {
                    results.items.add(filtered)
                }

                return Promise.pure(results)
            }
        }
    }
}
