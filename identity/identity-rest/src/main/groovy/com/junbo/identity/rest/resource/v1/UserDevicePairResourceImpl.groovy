package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserDeviceId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserDeviceFilter
import com.junbo.identity.core.service.validator.UserDeviceValidator
import com.junbo.identity.data.repository.UserDeviceRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.identity.spec.v1.option.model.UserDeviceGetOptions
import com.junbo.identity.spec.v1.resource.UserDevicePairResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.ext.Provider

/**
 * Created by liangfu on 4/9/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserDevicePairResourceImpl implements UserDevicePairResource {

    @Autowired
    private UserDeviceRepository userDeviceRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserDeviceFilter userDeviceFilter

    @Autowired
    private UserDeviceValidator userDeviceValidator

    @Override
    Promise<UserDevice> create(UserDevice userDevice) {
        if (userDevice == null) {
            throw new IllegalArgumentException('userDevice is null')
        }

        userDevice = userDeviceFilter.filterForCreate(userDevice)

        userDeviceValidator.validateForCreate(userDevice).then {
            userDeviceRepository.create(userDevice).then { UserDevice newUserDevice ->
                created201Marker.mark((Id)newUserDevice.id)

                newUserDevice = userDeviceFilter.filterForGet(newUserDevice, null)
                return Promise.pure(newUserDevice)
            }
        }
    }

    @Override
    Promise<UserDevice> get(UserDeviceId userDeviceId, UserDeviceGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userDeviceValidator.validateForGet(userDeviceId).then { UserDevice newUserDevice ->
            newUserDevice = userDeviceFilter.filterForGet(newUserDevice,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserDevice)
        }
    }

    @Override
    Promise<UserDevice> patch(UserDeviceId userDeviceId, UserDevice userDevice) {
        if (userDeviceId == null) {
            throw new IllegalArgumentException('userDeviceId is null')
        }

        if (userDevice == null) {
            throw new IllegalArgumentException('userDevice is null')
        }

        return userDeviceRepository.get(userDeviceId).then { UserDevice oldUserDevice ->
            if (oldUserDevice == null) {
                throw AppErrors.INSTANCE.userDeviceNotFound(userDeviceId).exception()
            }

            userDevice = userDeviceFilter.filterForPatch(userDevice, oldUserDevice)

            userDeviceValidator.validateForUpdate(userDeviceId, userDevice, oldUserDevice).then {

                userDeviceRepository.update(userDevice).then { UserDevice newUserDevice ->
                    newUserDevice = userDeviceFilter.filterForGet(newUserDevice, null)
                    return Promise.pure(newUserDevice)
                }
            }
        }
    }

    @Override
    Promise<UserDevice> put(UserDeviceId userDeviceId, UserDevice userDevice) {
        if (userDeviceId == null) {
            throw new IllegalArgumentException('userDeviceId is null')
        }

        if (userDevice == null) {
            throw new IllegalArgumentException('userDevice is null')
        }

        return userDeviceRepository.get(userDeviceId).then { UserDevice oldUserDevice ->
            if (oldUserDevice == null) {
                throw AppErrors.INSTANCE.userDeviceNotFound(userDeviceId).exception()
            }

            userDevice = userDeviceFilter.filterForPut(userDevice, oldUserDevice)

            userDeviceValidator.validateForUpdate(userDeviceId, userDevice, oldUserDevice).then {
                userDeviceRepository.update(userDevice).then { UserDevice newUserDevice ->
                    newUserDevice = userDeviceFilter.filterForGet(newUserDevice, null)
                    return Promise.pure(newUserDevice)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserDeviceId userDeviceId) {
        return userDeviceValidator.validateForGet(userDeviceId).then {
            userDeviceRepository.delete(userDeviceId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<UserDevice>> list(UserDeviceListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userDeviceValidator.validateForSearch(listOptions).then {
            userDeviceRepository.search(listOptions).then { List<UserDevice> userDeviceList ->
                def result = new Results<UserDevice>(items: [])

                userDeviceList.each { UserDevice newUserDevice ->
                    if (newUserDevice != null) {
                        newUserDevice = userDeviceFilter.filterForGet(newUserDevice,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserDevice != null) {
                        result.items.add(newUserDevice)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
