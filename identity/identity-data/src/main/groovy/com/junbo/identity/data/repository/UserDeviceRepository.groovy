/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserDeviceId
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * User Device Profile DAO is used to fetch/update/delete/get user device profile data from the database
 */
@CompileStatic
interface UserDeviceRepository {

    Promise<UserDevice> create(UserDevice entity)

    Promise<UserDevice> update(UserDevice entity)

    Promise<UserDevice> get(UserDeviceId id)

    Promise<List<UserDevice>> search(UserDeviceListOptions getOption)

    Promise<Void> delete(UserDeviceId id)
}