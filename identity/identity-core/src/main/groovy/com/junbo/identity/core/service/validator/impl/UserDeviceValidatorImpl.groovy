/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserDeviceId
import com.junbo.identity.core.service.validator.UserDeviceValidator
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.data.repository.UserDeviceRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
class UserDeviceValidatorImpl implements UserDeviceValidator {

    private UserDeviceRepository userDeviceRepository

    private UserRepository userRepository

    private DeviceRepository deviceRepository

    @Override
    Promise<UserDevice> validateForGet(UserDeviceId userDeviceId) {

        if (userDeviceId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userDeviceId').exception()
        }

        return userDeviceRepository.get(userDeviceId).then { UserDevice userDevice ->
            if (userDevice == null) {
                throw AppErrors.INSTANCE.userDeviceNotFound(userDeviceId).exception()
            }

            return Promise.pure(userDevice)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserDeviceListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.deviceId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or deviceId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserDevice userDevice) {

        checkBasicUserDeviceInfo(userDevice)
        if (userDevice.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return userDeviceRepository.search(new UserDeviceListOptions(
                userId: userDevice.userId,
                deviceId: userDevice.deviceId
        )).then { List<UserDevice> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('deviceId').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserDeviceId userDeviceId, UserDevice userDevice, UserDevice oldUserDevice) {
        validateForGet(userDeviceId).then {
            checkBasicUserDeviceInfo(userDevice)

            if (userDevice.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userDevice.id != userDeviceId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userDeviceId.value.toString()).exception()
            }

            if (userDevice.id != oldUserDevice.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserDevice.id.toString()).exception()
            }

            if (userDevice.deviceId != oldUserDevice.deviceId) {
                return userDeviceRepository.search(new UserDeviceListOptions(
                        userId: userDevice.userId,
                        deviceId: userDevice.deviceId
                )).then { List<UserDevice> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('deviceId').exception()
                    }

                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    private void checkBasicUserDeviceInfo(UserDevice userDevice) {
        if (userDevice == null) {
            throw new IllegalArgumentException('userDevice is null')
        }

        if (userDevice.deviceId == null) {
            throw AppErrors.INSTANCE.fieldRequired('deviceId').exception()
        }

        if (userDevice.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        userRepository.get(userDevice.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userDevice.userId).exception()
            }

            if (existingUser.active == null || existingUser.active == false) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userDevice.userId).exception()
            }

            deviceRepository.get(userDevice.deviceId).then { Device existingDevice ->
                if (existingDevice == null) {
                    throw AppErrors.INSTANCE.deviceNotFound(userDevice.deviceId).exception()
                }

                userDeviceRepository.search(new UserDeviceListOptions(
                        userId: userDevice.userId,
                        deviceId: userDevice.deviceId
                )).then { List<UserDevice> existingUserDeviceList ->
                    if (!CollectionUtils.isEmpty(existingUserDeviceList)) {
                        throw AppErrors.INSTANCE.fieldInvalid('deviceId').exception()
                    }
                }
            }
        }
    }

    @Required
    void setUserDeviceRepository(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository
    }
}
