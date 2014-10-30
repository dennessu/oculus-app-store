package com.junbo.authorization.core.validator

import com.junbo.authorization.db.repository.RoleAssignmentRepository
import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.GroupId
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.UniversalId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
class RoleAssignmentValidatorImpl implements RoleAssignmentValidator {
    private RoleRepository roleRepository

    private RoleAssignmentRepository roleAssignmentRepository

    private UserResource userResource

    private GroupResource groupResource

    private OrganizationResource organizationResource

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

    @Required
    void setRoleAssignmentRepository(RoleAssignmentRepository roleAssignmentRepository) {
        this.roleAssignmentRepository = roleAssignmentRepository
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Override
    Promise<Role> validateForCreate(RoleAssignment roleAssignment) {
        Assert.notNull(roleAssignment, 'roleAssignment is null')

        if (roleAssignment.roleId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        return checkAssignee(roleAssignment.assignee, roleAssignment).then { Group group ->
            return roleRepository.get(roleAssignment.roleId).then { Role role ->
                if (role == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('roleId').exception()
                }

                if (group != null && role.target.filterLinkId != group.organizationId.toString()) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('target.filterLink', 'Only support same organization role assignment').exception()
                }

                return Promise.pure(role)
            }
        }
    }

    @Override
    Promise<Void> validateForGet(RoleAssignmentId roleAssignmentId) {
        if (roleAssignmentId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('roleAssignmentId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForDelete(RoleAssignment roleAssignment) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForList(RoleAssignmentListOptions options) {
        Assert.notNull(options, 'options is null')

        if (options.roleId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        if (options.assignee == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('assignee').exception()
        }

        UniversalId resourceId = IdUtil.fromLink(new Link(href: options.assignee))

        if (resourceId == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('assignee').exception()
        }

        options.assigneeIdType = resourceId.class.canonicalName
        options.assigneeId = resourceId.toString()

        return Promise.pure(null)
    }

    Promise<Group> checkAssignee(Link assignee, RoleAssignment roleAssignment) {
        if (assignee == null || StringUtils.isEmpty(assignee.href) || StringUtils.isEmpty(assignee.id)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('assignee').exception()
        }

        UniversalId resourceId = IdUtil.fromLink(assignee)

        if (resourceId == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('assignee').exception()
        }

        roleAssignment.assigneeType = resourceId.class.canonicalName
        roleAssignment.assigneeId = resourceId.toString()

        if (resourceId instanceof GroupId) {
            return groupResource.get(resourceId, new GroupGetOptions()).then { Group existing ->
                if (existing == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('assignee', 'group not exists').exception()
                }

                if (!existing.active) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('assignee', 'group not active').exception()
                }

                return Promise.pure(existing)
            }
        } else if (resourceId instanceof UserId) {
            return userResource.get(resourceId, new UserGetOptions()).then { User existing ->
                if (existing == null || existing.status != 'ACTIVE') {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('assignee', 'user not exists').exception()
                }

                return Promise.pure(null)
            }
        } else {
            throw AppCommonErrors.INSTANCE.fieldInvalid('assignee', 'assignee only support Group or User').exception()
        }
    }
}
