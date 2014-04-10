/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserPhoneNumberEntity
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserPhoneNumberDAO {
    UserPhoneNumberEntity save(UserPhoneNumberEntity entity)

    UserPhoneNumberEntity update(UserPhoneNumberEntity entity)

    UserPhoneNumberEntity get(Long id)

    List<UserPhoneNumberEntity> search(Long userPiiId, UserPhoneNumberListOptions getOption)

    void delete(Long id)
}
