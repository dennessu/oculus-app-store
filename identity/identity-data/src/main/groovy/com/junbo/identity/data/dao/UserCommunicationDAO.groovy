/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserCommunicationEntity
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 3/18/14.
 */
@CompileStatic
interface UserCommunicationDAO {
    UserCommunicationEntity save(UserCommunicationEntity entity)
    UserCommunicationEntity update(UserCommunicationEntity entity)
    UserCommunicationEntity get(Long id)
    void delete(Long id)
    List<UserCommunicationEntity> searchByUserId(Long userId)
    List<UserCommunicationEntity> searchByCommunicationId(Long communicationId)
}