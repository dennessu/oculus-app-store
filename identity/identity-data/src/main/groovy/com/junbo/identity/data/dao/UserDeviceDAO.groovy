/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.common.id.UserDeviceId
import com.junbo.identity.spec.model.options.UserDeviceGetOption
import com.junbo.identity.spec.model.users.UserDevice

/**
 * User Device Profile DAO is used to fetch/update/delete/get user device profile data from the database
 */
interface UserDeviceDAO {

    UserDevice save(UserDevice entity)

    UserDevice update(UserDevice entity)

    UserDevice get(UserDeviceId id)

    List<UserDevice> search(UserDeviceGetOption getOption)

    void delete(UserDeviceId id)
}