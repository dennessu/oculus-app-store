/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserGroupId
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserGroupRepository {
    Promise<UserGroup> create(UserGroup entity)

    Promise<UserGroup> update(UserGroup entity)

    Promise<UserGroup> get(UserGroupId id)

    Promise<List<UserGroup>> search(UserGroupListOptions getOption)

    Promise<Void> delete(UserGroupId id)
}
