package com.junbo.authorization.core.validator

import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.common.id.RoleAssignmentId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
class RoleAssignmentValidatorImpl implements RoleAssignmentValidator {
    @Override
    Promise<Void> validateForCreate(RoleAssignment roleAssignment) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForGet(RoleAssignmentId roleAssignmentId) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForUpdate(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForList(RoleAssignmentListOptions options) {
        return Promise.pure(null)
    }
}
