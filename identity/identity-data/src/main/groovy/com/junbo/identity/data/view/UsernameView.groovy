/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * UserUsername View
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UsernameView implements EntityView<Long, UserEntity, String> {


    @Override
    String getName() {
        return 'user_username'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserEntity> getEntityType() {
        return UserEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.canonicalUsername != null
    }

    @Override
    List<String> mapEntity(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.canonicalUsername]
    }
}
