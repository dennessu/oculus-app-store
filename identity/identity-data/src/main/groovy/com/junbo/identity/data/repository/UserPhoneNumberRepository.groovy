/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserPhoneNumberId
import com.junbo.identity.spec.model.users.UserPhoneNumber
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserPhoneNumberRepository {
    Promise<UserPhoneNumber> create(UserPhoneNumber entity)

    Promise<UserPhoneNumber> update(UserPhoneNumber entity)

    Promise<UserPhoneNumber> get(UserPhoneNumberId id)

    Promise<List<UserPhoneNumber>> search(UserPhoneNumberListOptions getOption)

    Promise<Void> delete(UserPhoneNumberId id)
}
