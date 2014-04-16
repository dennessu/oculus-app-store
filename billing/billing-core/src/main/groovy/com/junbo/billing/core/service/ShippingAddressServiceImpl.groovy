/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.db.repository.ShippingAddressRepository
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by xmchen on 14-1-26.
 */
@CompileStatic
@Transactional
class ShippingAddressServiceImpl implements ShippingAddressService {
    @Autowired
    ShippingAddressRepository shippingAddressRepository

    @Autowired
    IdentityFacade identityFacade

    @Autowired
    TaxService taxService

    private static final Logger LOGGER = LoggerFactory.getLogger(ShippingAddressServiceImpl)

    @Override
    Promise<ShippingAddress> addShippingAddress(Long userId, ShippingAddress address) {

        if (userId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('userId').exception()
        }
        address.setUserId(new UserId(userId))

        return validateUser(address.userId.value).then {
            return validateAddress(address).recover { Throwable throwable ->
                LOGGER.error('name=Error_Add_ShippingAddress. The posted address is invalid verified by tax service. ')
                throw throwable
            }.then { ShippingAddress returnedAddress ->
                return Promise.pure(shippingAddressRepository.saveShippingAddress(returnedAddress))
            }
        }
    }

    @Override
    Promise<List<ShippingAddress>> getShippingAddresses(Long userId) {

        return validateUser(userId).then {
            return Promise.pure(shippingAddressRepository.getShippingAddresses(userId))
        }

    }

    @Override
    Promise<ShippingAddress> getShippingAddress(Long userId, Long addressId) {
        ShippingAddress address = shippingAddressRepository.getShippingAddress(addressId)

        if (address == null) {
            throw AppErrors.INSTANCE.shippingAddressNotFound(addressId.toString()).exception()
        }

        if (address.userId.value != userId) {
            throw AppErrors.INSTANCE.userShippingAddressNotMatch(userId.toString(), addressId.toString()).exception()
        }

        return Promise.pure(address)
    }

    @Override
    void deleteShippingAddress(Long userId, Long addressId) {
        shippingAddressRepository.deleteShippingAddress(addressId)
    }

    private Promise<Void> validateUser(Long userId) {
        identityFacade.getUser(userId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_User. userId: ' + userId, throwable)
            throw AppErrors.INSTANCE.userNotFound(userId.toString()).exception()
        }.then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId.toString()).exception()
            }
            /*if (user.status != 'ACTIVE') {
                throw AppErrors.INSTANCE.userStatusInvalid(userId.toString()).exception()
            }*/

            return Promise.pure(null)
        }
    }

    private Promise<ShippingAddress> validateAddress(ShippingAddress address) {
        if (address.street == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('street').exception()
        }
        if (address.city == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('city').exception()
        }
        if (address.country == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('country').exception()
        } else {
            if (address.state == null && 'US' == address.country) {
                throw AppErrors.INSTANCE.fieldMissingValue('state').exception()
            }
        }
        if (address.postalCode == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('postalCode').exception()
        }
        if (address.firstName == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('firstName').exception()
        }
        if (address.lastName == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('lastName').exception()
        }

        return taxService.validateShippingAddress(address)
    }
}
