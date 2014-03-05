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
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
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

    @Override
    Promise<ShippingAddress> addShippingAddress(ShippingAddress address) {

        if (address.userId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('userId').exception()
        }
        validateUser(address.userId.value)
        validateAddress(address)

        return Promise.pure(shippingAddressRepository.saveShippingAddress(address))
    }

    @Override
    Promise<List<ShippingAddress>> getShippingAddresses(Long userId) {

        validateUser(userId)

        return Promise.pure(shippingAddressRepository.getShippingAddresses(userId))
    }

    @Override
    Promise<ShippingAddress> getShippingAddress(Long addressId) {

        ShippingAddress address = shippingAddressRepository.getShippingAddress(addressId)

        if (address == null) {
            throw AppErrors.INSTANCE.shippingAddressNotFound(addressId.toString()).exception()
        }
        return Promise.pure(address)
    }

    @Override
    void deleteShippingAddress(Long addressId) {

        shippingAddressRepository.deleteShippingAddress(addressId)
    }

    private void validateUser(Long userId) {
        identityFacade.getUser(userId).then {
            User user = (User)it
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId.toString()).exception()
            }
            if (user.status != 'ACTIVE') {
                throw AppErrors.INSTANCE.userStatusInvalid(userId.toString()).exception()
            }
        }
    }

    private void validateAddress(ShippingAddress address) {
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
    }
}
