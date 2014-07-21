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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

/**
 * Impl of IdentityFacade.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {
    private final static Logger LOGGER = LoggerFactory.getLogger(IdentityFacadeImpl)

    @Resource(name = 'emailIdentityUserClient')
    private UserResource userResource

    @Resource(name='emailIdentityUserPersonalInfoClient')
    private UserPersonalInfoResource userPersonalInfoResource

    Promise<String> getUserEmail(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions()).then { User user ->
            if (!user?.emails?.any()) {
                throw AppErrors.INSTANCE.emptyUserEmail().exception()
            }
            def infoLink = user.emails.find { UserPersonalInfoLink link ->
                link.isDefault
            }
            if (infoLink == null) {
                throw AppErrors.INSTANCE.emptyUserEmail().exception()
            }
            return userPersonalInfoResource.get(infoLink.value,
                    new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                try {
                    if (info?.lastValidateTime == null) {
                        throw AppErrors.INSTANCE.noValidatedUserEmail().exception()
                    }
                    def email = ObjectMapperProvider.instance().treeToValue(info.value, Email)
                    return Promise.pure(email.info)
                } catch (Exception e) {
                    LOGGER.error("Failed to convert user address:", e)
                    throw AppErrors.INSTANCE.noValidatedUserEmail().exception()
                }
            }
        }
    }

}
