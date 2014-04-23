/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserTeleCodeEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleDAO {
    UserTeleCodeEntity create(UserTeleCodeEntity entity)
    UserTeleCodeEntity update(UserTeleCodeEntity entity)
    UserTeleCodeEntity get(Long id)
    void delete(Long id)

    // Return active tele code (expiresBy must be after now)
    UserTeleCodeEntity getActiveUserTeleCode(Long userId, String phoneNumber)
}
