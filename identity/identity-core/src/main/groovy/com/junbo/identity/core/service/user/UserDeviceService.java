/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.options.list.UserDeviceListOptions;
import com.junbo.identity.spec.model.users.UserDevice;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserDeviceService {
    UserDevice save(UserId userId, UserDevice userDevice);
    UserDevice update(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice);
    UserDevice get(UserId userId, UserDeviceId userDeviceId);
    List<UserDevice> search(UserDeviceListOptions getOption);
    void delete(UserId userId, UserDeviceId userDeviceId);
}
