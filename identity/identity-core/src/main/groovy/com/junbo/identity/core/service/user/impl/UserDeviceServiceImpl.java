/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserDeviceId;
import com.junbo.identity.data.repository.UserDeviceRepository;
import com.junbo.identity.core.service.user.UserDeviceService;
import com.junbo.identity.spec.options.list.UserDeviceListOption;
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
    private UserDeviceRepository userDeviceRepository;

    @Override
    public UserDevice save(Long userId, UserDevice userDeviceProfile) {
        return userDeviceRepository.save(userDeviceProfile);
    }

    @Override
    public UserDevice update(Long userId, Long deviceProfileId, UserDevice userDeviceProfileProfile) {
        return userDeviceRepository.update(userDeviceProfileProfile);
    }

    @Override
    public UserDevice get(Long userId, Long deviceProfileId) {
        return userDeviceRepository.get(new UserDeviceId(deviceProfileId));
    }

    @Override
    public List<UserDevice> search(UserDeviceListOption getOption) {
        return userDeviceRepository.search(getOption);
    }

    @Override
    public void delete(Long userId, Long deviceId) {
        userDeviceRepository.delete(new UserDeviceId(deviceId));
    }
}
