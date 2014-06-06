/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.validator

import com.junbo.common.id.RoleId
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * RoleValidator.
 */
@CompileStatic
interface RoleValidator {
    Promise<Void> validateForCreate(Role role)

    Promise<Void> validateForGet(RoleId roleId)

    Promise<Void> validateForUpdate(Role role, Role oldRole)

    Promise<Void> validateForList(RoleListOptions options)
}
