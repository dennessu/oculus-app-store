/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.user.UserDeviceProfile

/**
 * User Device Profile DAO is used to fetch/update/delete/get user device profile data from the database
 */
interface UserDeviceProfileDAO {

    UserDeviceProfile save(UserDeviceProfile entity)

    UserDeviceProfile update(UserDeviceProfile entity)

    UserDeviceProfile get(Long id)

    List<UserDeviceProfile> findByUser(Long userId, String type)

    void delete(Long id)
}