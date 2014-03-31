/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl
import com.junbo.common.id.UserDeviceId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.validator.UserDeviceValidator
import com.junbo.identity.data.repository.UserDeviceRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserDevice
import com.junbo.identity.spec.options.list.UserDeviceListOptions
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
/**
 * Created by liangfu on 3/27/14.
 */
class UserDeviceValidatorImpl implements UserDeviceValidator {

    private UserDeviceRepository userDeviceRepository

    private UserRepository userRepository

    private Integer deviceIdMinLength
    private Integer deviceIdMaxLength

    private Integer osMinLength
    private Integer osMaxLength

    private Integer typeMinLength
    private Integer typeMaxLength

    private Integer nameMinLength
    private Integer nameMaxLength

    @Required
    void setUserDeviceRepository(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setDeviceIdMinLength(Integer deviceIdMinLength) {
        this.deviceIdMinLength = deviceIdMinLength
    }

    @Required
    void setDeviceIdMaxLength(Integer deviceIdMaxLength) {
        this.deviceIdMaxLength = deviceIdMaxLength
    }

    @Required
    void setOsMinLength(Integer osMinLength) {
        this.osMinLength = osMinLength
    }

    @Required
    void setOsMaxLength(Integer osMaxLength) {
        this.osMaxLength = osMaxLength
    }

    @Required
    void setTypeMinLength(Integer typeMinLength) {
        this.typeMinLength = typeMinLength
    }

    @Required
    void setTypeMaxLength(Integer typeMaxLength) {
        this.typeMaxLength = typeMaxLength
    }

    @Required
    void setNameMinLength(Integer nameMinLength) {
        this.nameMinLength = nameMinLength
    }

    @Required
    void setNameMaxLength(Integer nameMaxLength) {
        this.nameMaxLength = nameMaxLength
    }

    @Override
    Promise<UserDevice> validateForGet(UserId userId, UserDeviceId userDeviceId) {

        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userDeviceId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userDeviceId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            userDeviceRepository.get(userDeviceId).then { UserDevice userDevice ->
                if (userDevice == null) {
                    throw AppErrors.INSTANCE.userDeviceNotFound(userDeviceId).exception()
                }

                if (userId != userDevice.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userDeviceId doesn\'t match.').exception()
                }

                return Promise.pure(userDevice)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserDeviceListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserDevice userDevice) {

        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserDeviceInfo(userDevice)
        if (userDevice.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userDevice.userId != null && userDevice.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userDevice.userId.toString()).exception()
        }

        // todo:    The deviceId should identity only one device, other fileds can be seen as the properties of it
        return userDeviceRepository.search(new UserDeviceListOptions(
                userId: userId,
                deviceId: userDevice.deviceId
        )).then { List<UserDevice> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('deviceId').exception()
            }

            userDevice.setUserId(userId)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserDeviceId userDeviceId,
                                    UserDevice userDevice, UserDevice oldUserDevice) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        validateForGet(userId, userDeviceId).then {
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
                        userId: userId,
                        deviceId: userDevice.deviceId
                )).then { List<UserDevice> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('deviceId').exception()
                    }
                    userDevice.setUserId(userId)
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

        if (userDevice.deviceId.size() < deviceIdMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('deviceId', deviceIdMinLength).exception()
        }

        if (userDevice.deviceId.size() > deviceIdMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('deviceId', deviceIdMaxLength).exception()
        }

        if (userDevice.name == null) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }

        if (userDevice.name.size() < nameMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('name', nameMinLength).exception()
        }

        if (userDevice.name.size() > nameMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('name', nameMaxLength).exception()
        }

        if (userDevice.os == null) {
            throw AppErrors.INSTANCE.fieldRequired('os').exception()
        }

        if (userDevice.os.size() < osMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('os', osMinLength).exception()
        }

        if (userDevice.os.size() > osMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('os', osMaxLength).exception()
        }

        if (userDevice.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }

        if (userDevice.type.size() < typeMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('type', typeMinLength).exception()
        }

        if (userDevice.type.size() > typeMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('type', typeMaxLength).exception()
        }
    }
}
