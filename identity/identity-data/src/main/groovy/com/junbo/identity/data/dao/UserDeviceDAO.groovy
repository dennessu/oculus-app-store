/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions

/**
 * User Device Profile DAO is used to fetch/update/delete/get user device profile data from the database
 */
interface UserDeviceDAO {

    UserDeviceEntity save(UserDeviceEntity entity)

    UserDeviceEntity update(UserDeviceEntity entity)

    UserDeviceEntity get(Long id)

    List<UserDeviceEntity> search(Long userId, UserDeviceListOptions getOption)

    void delete(Long id)

    List<UserDeviceEntity> findByDeviceId(Long deviceId, UserDeviceListOptions getOption)
}