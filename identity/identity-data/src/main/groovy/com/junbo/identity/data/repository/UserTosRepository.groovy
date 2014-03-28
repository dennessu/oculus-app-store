/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserTosId
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.identity.spec.options.list.UserTosListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
@CompileStatic
interface UserTosRepository {

    Promise<UserTos> create(UserTos entity)

    Promise<UserTos> update(UserTos entity)

    Promise<UserTos> get(UserTosId id)

    Promise<List<UserTos>> search(UserTosListOptions getOption)

    Promise<Void> delete(UserTosId id)
}
