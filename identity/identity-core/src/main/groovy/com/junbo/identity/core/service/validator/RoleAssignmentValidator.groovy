/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator

import com.junbo.common.id.RoleAssignmentId
import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.identity.spec.v1.option.list.RoleAssignmentListOptions
import com.junbo.langur.core.promise.Promise

/**
 * RoleAssignmentValidator.
 */
interface RoleAssignmentValidator {
    Promise<Void> validateForCreate(RoleAssignment roleAssignment)

    Promise<RoleAssignment> validateForGet(RoleAssignmentId roleAssignmentId)

    Promise<Void> validateForUpdate(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment)

    Promise<Void> validateForList(RoleAssignmentListOptions options)
}