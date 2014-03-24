/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
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
    public UserDevice save(UserId userId, UserDevice userDevice) {
        return userDeviceRepository.save(userDevice);
    }

    @Override
    public UserDevice update(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        return userDeviceRepository.update(userDevice);
    }

    @Override
    public UserDevice get(UserId userId, UserDeviceId userDeviceId) {
        return userDeviceRepository.get(userDeviceId);
    }

    @Override
    public List<UserDevice> search(UserDeviceListOption getOption) {
        return userDeviceRepository.search(getOption);
    }

    @Override
    public void delete(UserId userId, UserDeviceId userDeviceId) {
        userDeviceRepository.delete(userDeviceId);
    }
}
