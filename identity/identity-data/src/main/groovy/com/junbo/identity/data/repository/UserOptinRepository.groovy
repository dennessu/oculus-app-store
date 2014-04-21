/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserOptinId
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * User OptIn DAO is used to fetch/update/delete/get user OptIn data(such as sports, news) from the database
 */
@CompileStatic
interface UserOptinRepository extends IdentityBaseRepository<UserOptin, UserOptinId> {
    Promise<List<UserOptin>> search(UserOptinListOptions getOption)
}
