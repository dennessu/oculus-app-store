/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserDeviceId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.users.UserDevice
import com.junbo.identity.spec.options.list.UserDeviceListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
interface UserDeviceValidator {
    Promise<UserDevice> validateForGet(UserId userId, UserDeviceId userDeviceId)
    Promise<Void> validateForSearch(UserDeviceListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserDevice userDevice)
    Promise<Void> validateForUpdate(UserId userId, UserDeviceId userDeviceId,
                                    UserDevice userDevice, UserDevice oldUserDevice)
}