package com.junbo.authorization.core.validator
import com.junbo.authorization.db.repository.RoleAssignmentRepository
import com.junbo.authorization.db.repository.RoleRepository
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.UniversalId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
class RoleAssignmentValidatorImpl implements RoleAssignmentValidator {
    private RoleRepository roleRepository

    private RoleAssignmentRepository roleAssignmentRepository

    @Required
    void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository
    }

    @Required
    void setRoleAssignmentRepository(RoleAssignmentRepository roleAssignmentRepository) {
        this.roleAssignmentRepository = roleAssignmentRepository
    }

    @Override
    Promise<Role> validateForCreate(RoleAssignment roleAssignment) {
        Assert.notNull(roleAssignment, 'roleAssignment is null')

        if (roleAssignment.roleId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('roleId').exception()
        }

        if (roleAssignment.assignee == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('assignee').exception()
        }

        return roleRepository.get(roleAssignment.roleId).then { Role role ->
            if (role == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('roleId').exception()
            }

            UniversalId resourceId = IdUtil.fromLink(roleAssignment.assignee)

            if (resourceId == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('assignee').exception()
            }

            roleAssignment.assigneeType = resourceId.class.canonicalName
            roleAssignment.assigneeId = resourceId.toString()

            return Promise.pure(role)
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
}
