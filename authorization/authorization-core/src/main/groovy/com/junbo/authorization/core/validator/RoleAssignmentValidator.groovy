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
interface RoleAssignmentValidator {
    Promise<Void> validateForCreate(RoleAssignment roleAssignment)

    Promise<Void> validateForGet(RoleAssignmentId roleAssignmentId)

    Promise<Void> validateForDelete(RoleAssignment roleAssignment)

    Promise<Void> validateForList(RoleAssignmentListOptions options)
}