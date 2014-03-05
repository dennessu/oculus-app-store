/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserDeviceProfileDAO;
import com.junbo.identity.rest.service.user.UserDeviceProfileService;
import com.junbo.identity.spec.model.user.UserDeviceProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserDeviceProfileServiceImpl implements UserDeviceProfileService {
    @Autowired
    private UserDeviceProfileDAO userDeviceProfileDAO;

    @Override
    public UserDeviceProfile save(Long userId, UserDeviceProfile userDeviceProfile) {
        return userDeviceProfileDAO.save(userDeviceProfile);
    }

    @Override
    public UserDeviceProfile update(Long userId, Long deviceProfileId, UserDeviceProfile userDeviceProfileProfile) {
        return userDeviceProfileDAO.update(userDeviceProfileProfile);
    }

    @Override
    public UserDeviceProfile get(Long userId, Long deviceProfileId) {
        return userDeviceProfileDAO.get(deviceProfileId);
    }

    @Override
    public List<UserDeviceProfile> getByUserId(Long userId, String type) {
        return userDeviceProfileDAO.findByUser(userId, type);
    }

    @Override
    public void deleteProfile(Long userId, Long deviceProfileId) {
        userDeviceProfileDAO.delete(deviceProfileId);
    }
}
