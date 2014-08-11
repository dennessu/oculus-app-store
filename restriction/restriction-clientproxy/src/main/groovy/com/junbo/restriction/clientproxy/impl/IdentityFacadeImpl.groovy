/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.clientproxy.IdentityFacade
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Impl of Identity Facade.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {

    @Resource(name = 'restrictionIdentityUserClient')
    private UserResource userResource

    @Resource(name = 'restrictionIdentityUserPersonalInfoClient')
    private UserPersonalInfoResource userPersonalInfoResource

    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    Promise<User> getUser(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions())
    }

    @Override
    Promise<Date> getUserDob(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions()).then { User user ->
            if (user.dob != null && user.dob.value != null) {
                return userPersonalInfoResource.get(user.dob, new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                    if (info != null && info.type.equalsIgnoreCase('dob')) {
                        try {
                            Date date = ObjectMapperProvider.instance().treeToValue(info.value, Date)
                            return Promise.pure(date)
                        }
                        catch (Exception e) {
                            return Promise.pure(null)
                        }
                    }
                }
            }

            return Promise.pure(null)
        }
    }
}
