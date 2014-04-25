/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/3/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class AuthenticatorValueView implements EntityView<Long, UserAuthenticatorEntity, String>  {

    @Override
    String getName() {
        return 'authenticator_authenticatorvalue'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserAuthenticatorEntity> getEntityType() {
        return UserAuthenticatorEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(UserAuthenticatorEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.externalId != null
    }

    @Override
    List<String> mapEntity(UserAuthenticatorEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.externalId]
    }
}
