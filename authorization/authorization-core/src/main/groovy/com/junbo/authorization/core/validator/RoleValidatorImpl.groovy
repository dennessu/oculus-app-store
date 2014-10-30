/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.validator
import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.RoleId
import com.junbo.common.id.UniversalId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
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
    private List<String> allowedRoleNames
    private List<String> allowedTargetTypes
    private List<String> allowedFilterTypes
    private OrganizationResource organizationResource

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

    @Required
    void setAllowedRoleNames(List<String> allowedRoleNames) {
        this.allowedRoleNames = allowedRoleNames
    }

    @Required
    void setAllowedTargetTypes(List<String> allowedTargetTypes) {
        this.allowedTargetTypes = allowedTargetTypes
    }

    @Required
    void setAllowedFilterTypes(List<String> allowedFilterTypes) {
        this.allowedFilterTypes = allowedFilterTypes
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Override
    Promise<Void> validateForCreate(Role role) {
        checkBasicFields(role)

        return checkFilterLink(role.target.filterLink).then { UniversalId resourceId ->
            role.target.filterLinkIdType = resourceId.class.canonicalName
            role.target.filterLinkId = resourceId.toString()
            return roleRepository.findByRoleName(role.name, role.target.targetType,
                    role.target.filterType, role.target.filterLinkIdType, role.target.filterLinkId).then { Role existing ->
                if (existing != null) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('name').exception()
                }
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForGet(RoleId roleId) {
        if (roleId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForUpdate(Role role, Role oldRole) {

        checkBasicFields(role)

        if (role.name != oldRole.name) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('name', 'role name cannot be changed').exception()
        }

        if (role.id == null || ((RoleId) role.id).value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }

        if (role.id != oldRole.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('roleId').exception()
        }

        return checkFilterLink(role.target.filterLink).then { UniversalId resourceId ->
            role.target.filterLinkIdType = resourceId.class.canonicalName
            role.target.filterLinkId = resourceId.toString()

            return roleRepository.findByRoleName(role.name, role.target.targetType,
                    role.target.filterType, role.target.filterLinkIdType, role.target.filterLinkId).then { Role existing ->
                if (existing != null && existing.getId() != role.getId()) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('name').exception()
                }
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForList(RoleListOptions options) {
        Assert.notNull(options)

        if (options.name == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('name').exception()
        }

        if (options.filterType == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('filterType').exception()
        }

        if (options.targetType == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('targetType').exception()
        }

        if (options.filterLink == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('filterLink').exception()
        }

        UniversalId resourceId = IdUtil.fromLink(new Link(href: options.filterLink))

        if (resourceId == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('filterLink').exception()
        }

        if (!(resourceId instanceof OrganizationId)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('filterLink', 'filterLink only support organization').exception()
        }

        options.filterLinkIdType = resourceId.class.canonicalName
        options.filterLinkId = resourceId.toString()

        return Promise.pure(null)
    }

    private void checkBasicFields(Role role) {
        Assert.notNull(role, 'role is null')

        if (StringUtils.isEmpty(role.name)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('name').exception()
        }

        String value = allowedRoleNames.find { String allowedRoleName ->
            return allowedRoleName.equalsIgnoreCase(role.name)
        }
        if (StringUtils.isEmpty(value)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('name', allowedRoleNames.join(',')).exception()
        }

        if (role.target == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('target').exception()
        }

        if (StringUtils.isEmpty(role.target.targetType)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('target.targetType').exception()
        }
        value = allowedTargetTypes.find { String allowedTargetType ->
            return allowedTargetType.equalsIgnoreCase(role.target.targetType)
        }
        if (StringUtils.isEmpty(value)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('target.targetType', allowedTargetTypes.join(',')).exception()
        }

        if (StringUtils.isEmpty(role.target.filterType)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('target.filterType').exception()
        }
        value = allowedFilterTypes.find { String allowedFilterType ->
            return allowedFilterType.equalsIgnoreCase(role.target.filterType)
        }
        if (StringUtils.isEmpty(value)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('target.filterType', allowedFilterTypes.join(',')).exception()
        }

    }

    private Promise<UniversalId> checkFilterLink(Link filterLink) {
        if (filterLink == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('target.filterLink').exception()
        }

        if (StringUtils.isEmpty(filterLink.href)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('target.filterLink.href').exception()
        }

        if (StringUtils.isEmpty(filterLink.id)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('target.filterLink.id').exception()
        }

        UniversalId resourceId = IdUtil.fromLink(filterLink)

        if (resourceId == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('target.filterLink').exception()
        }

        if (resourceId instanceof OrganizationId) {
            return organizationResource.get(resourceId, new OrganizationGetOptions()).then { Organization existing ->
                if (existing == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('target.filterLink', 'filterLinker must be exists').exception()
                }

                return Promise.pure(resourceId)
            }
        } else {
            throw AppCommonErrors.INSTANCE.fieldInvalid('target.filterLink', 'filterLinker must be organization').exception()
        }
    }
}
