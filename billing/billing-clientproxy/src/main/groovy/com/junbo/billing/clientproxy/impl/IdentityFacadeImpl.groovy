/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by xmchen on 14-2-20.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {

    @Resource(name = 'billingIdentityUserClient')
    private UserResource userResource

    @Resource(name = 'billingIdentityUserPersonalInfoClient')
    private UserPersonalInfoResource userPersonalInfoResource

    @Override
    Promise<User> getUser(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions())
    }

    @Override
    Promise<Address> getAddress(Long addressId) {
        if (addressId == null) {
            return Promise.pure(null)
        }
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
    Promise<String> getUsername(Long usernameId) {
        if (usernameId == null) {
            return Promise.pure(null)
        }


        return userPersonalInfoResource.get(new UserPersonalInfoId(usernameId), new UserPersonalInfoGetOptions())
                .then { UserPersonalInfo info ->
            if (info == null || !info.type.equalsIgnoreCase('USERNAME')) {
                return Promise.pure(null)
            }

            try {
                UserLoginName userLoginName = ObjectMapperProvider.instance().treeToValue(info.value, UserLoginName)
                return Promise.pure(userLoginName.userName)
            } catch (Exception ex) {
                return Promise.pure(null)
            }
        }
    }
}