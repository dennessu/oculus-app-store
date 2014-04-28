/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.email.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Impl of IdentityFacade.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {

    @Resource(name = 'emailIdentityUserClient')
    private UserResource userResource

    @Resource(name='emailIdentityUserPersonalInfoClient')
    private UserPersonalInfoResource userPersonalInfoResource

    Promise<User> getUser(Long userId) {
        userResource.get(new UserId(userId), new UserGetOptions()).recover {
            throw AppErrors.INSTANCE.invalidUserId('').exception()
        }.then {
            return Promise.pure(it)
        }
    }

    Promise<String> getUserEmail(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions()).then { User user ->
            if (user.emails != null && user.emails.size() != 0) {
                for (UserPersonalInfoLink link : user.emails) {
                    if (!link.isdefault) {
                        continue
                    }
                    return userPersonalInfoResource.get(link.value,
                            new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                        if (info == null) {
                            return Promise.pure(null)
                        }
                        try {
                            Email email = ObjectMapperProvider.instance().treeToValue(info.value, Email)
                            return Promise.pure(email.value)
                        }
                        catch (Exception e) {
                            return Promise.pure(null)
                        }
                    }
                }
                return Promise.pure(null)
            }
        }
    }
}
