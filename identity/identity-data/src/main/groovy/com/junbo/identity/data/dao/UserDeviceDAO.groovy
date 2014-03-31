/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.spec.options.list.UserDeviceListOptions
import com.junbo.sharding.annotations.SeedParam

/**
 * User Device Profile DAO is used to fetch/update/delete/get user device profile data from the database
 */
interface UserDeviceDAO {

    UserDeviceEntity save(UserDeviceEntity entity)

    UserDeviceEntity update(UserDeviceEntity entity)

    UserDeviceEntity get(@SeedParam Long id)

    List<UserDeviceEntity> search(@SeedParam Long userId, UserDeviceListOptions getOption)

    void delete(@SeedParam Long id)
}