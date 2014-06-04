/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.identity.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.identity.IdentityFacade
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by linyi on 14-2-19.
 */
@Component('orderIdentityFacade')
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {
    @Resource(name='order.identityUserClient')
    UserResource userResource

    @Resource(name='order.identityUserPersonalInfoClient')
    UserPersonalInfoResource userPersonalInfoResource

    @Resource(name='order.identityCurrencyClient')
    CurrencyResource currencyResource

    @Override
    Promise<User> getUser(Long userId) {
        UserGetOptions options = new UserGetOptions()
        return userResource.get(new UserId(userId), options)
    }

    @Override
    Promise<User> createUser(User user) {
        Promise<User> userPromise = null

        try {
            userPromise = userResource.create(user)
        }
        catch (Exception e) {
            return Promise.pure(null)
        }

        return userPromise
    }

    @Override
    Promise<Address> getAddress(Long addressId) {
        return userPersonalInfoResource.get(new UserPersonalInfoId(addressId), new UserPersonalInfoGetOptions())
                .then { UserPersonalInfo info ->
            if (info == null || !info.type.equalsIgnoreCase('address')) {
                return Promise.pure(null)
            }

            try {
                Address address = ObjectMapperProvider.instance().treeToValue(info.value, Address)
                return Promise.pure(address)
            } catch (Exception ex) {
                return Promise.pure(null)
            }
        }

    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Currency> getCurrency(String currency) {
        return currencyResource.get(new CurrencyId(currency), new CurrencyGetOptions())
    }
}
