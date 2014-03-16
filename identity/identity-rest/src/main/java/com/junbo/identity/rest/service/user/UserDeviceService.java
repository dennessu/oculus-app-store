/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;

import com.junbo.identity.spec.model.options.UserDeviceGetOption;
import com.junbo.identity.spec.model.users.UserDevice;
import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserDeviceService {
    UserDevice save(Long userId, UserDevice userDeviceProfile);
    UserDevice update(Long userId, Long deviceProfileId, UserDevice userDeviceProfile);
    UserDevice get(Long userId, Long deviceProfileId);
    List<UserDevice> search(UserDeviceGetOption getOption);
    void delete(Long userId, Long deviceProfileId);
}
