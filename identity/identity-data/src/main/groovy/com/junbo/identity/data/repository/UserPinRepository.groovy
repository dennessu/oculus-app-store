/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserPinId
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.options.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/16/14.
 */

@CompileStatic
interface UserPinRepository {

    Promise<UserPin> save(UserPin entity)

    Promise<UserPin> update(UserPin entity)

    Promise<UserPin> get(UserPinId id)

    Promise<List<UserPin>> search(UserPinListOptions getOption)

    Promise<Void> delete(UserPinId id)
}
