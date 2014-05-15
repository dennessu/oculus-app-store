/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
interface UserPasswordRepository extends BaseRepository<UserPassword, UserPasswordId> {
    @ReadMethod
    Promise<List<UserPassword>> search(UserPasswordListOptions getOption)
}
