/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.validator.UserDeviceValidator;
import com.junbo.identity.data.repository.UserDeviceRepository;
import com.junbo.identity.core.service.user.UserDeviceService;
import com.junbo.identity.spec.options.list.UserDeviceListOptions;
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

    @Autowired
    private UserDeviceValidator validator;

    @Override
    public UserDevice save(UserId userId, UserDevice userDevice) {
        validator.validateCreate(userId, userDevice);
        userDevice.setUserId(userId);
        return userDeviceRepository.save(userDevice);
    }

    @Override
    public UserDevice update(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        validator.validateUpdate(userId, userDeviceId, userDevice);
        userDevice.setUserId(userId);
        return userDeviceRepository.update(userDevice);
    }

    @Override
    public UserDevice get(UserId userId, UserDeviceId userDeviceId) {
        return userDeviceRepository.get(userDeviceId);
    }

    @Override
    public List<UserDevice> search(UserDeviceListOptions getOption) {
        return userDeviceRepository.search(getOption);
    }

    @Override
    public void delete(UserId userId, UserDeviceId userDeviceId) {
        userDeviceRepository.delete(userDeviceId);
    }
}
