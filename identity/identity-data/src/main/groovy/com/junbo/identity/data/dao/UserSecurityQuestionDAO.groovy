/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserSecurityQuestionDAO {
    UserSecurityQuestionEntity save(UserSecurityQuestionEntity entity)

    UserSecurityQuestionEntity update(UserSecurityQuestionEntity entity)

    UserSecurityQuestionEntity get(Long id)

    List<UserSecurityQuestionEntity> search(Long userId, UserSecurityQuestionListOptions getOption)

    void delete(Long id)
}
