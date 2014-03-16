/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.common.id.UserDeviceId;
import com.junbo.identity.data.dao.UserDeviceDAO;
import com.junbo.identity.rest.service.user.UserDeviceService;
import com.junbo.identity.spec.model.options.UserDeviceGetOption;
import com.junbo.identity.spec.model.users.UserDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserDeviceServiceImpl implements UserDeviceService {
    @Autowired
    private UserDeviceDAO userDeviceDAO;

    @Override
    public UserDevice save(Long userId, UserDevice userDeviceProfile) {
        return userDeviceDAO.save(userDeviceProfile);
    }

    @Override
    public UserDevice update(Long userId, Long deviceProfileId, UserDevice userDeviceProfileProfile) {
        return userDeviceDAO.update(userDeviceProfileProfile);
    }

    @Override
    public UserDevice get(Long userId, Long deviceProfileId) {
        return userDeviceDAO.get(new UserDeviceId(deviceProfileId));
    }

    @Override
    public List<UserDevice> search(UserDeviceGetOption getOption) {
        return userDeviceDAO.search(getOption);
    }

    @Override
    public void delete(Long userId, Long deviceId) {
        userDeviceDAO.delete(new UserDeviceId(deviceId));
    }
}
