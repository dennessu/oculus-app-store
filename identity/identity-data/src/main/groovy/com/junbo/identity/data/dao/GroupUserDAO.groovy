/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.group.GroupUserEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/21/14.
 */
@CompileStatic
interface GroupUserDAO {

    GroupUserEntity create(GroupUserEntity entity)

    GroupUserEntity update(GroupUserEntity entity)

    GroupUserEntity findByGroupIdAndUserId(Long groupId, Long userId)

    List<GroupUserEntity> findByGroupId(Long groupId)

    GroupUserEntity get(Long id)

    void delete(Long id)
}
