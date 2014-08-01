/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.identity.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by linyi on 14-2-19.
 */
@Component('orderIdentityFacade')
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {
    @Resource(name = 'order.identityUserClient')
    UserResource userResource

    @Resource(name = 'order.identityUserPersonalInfoClient')
    UserPersonalInfoResource userPersonalInfoResource

    @Resource(name = 'order.identityOrganizationClient')
    OrganizationResource organizationResource

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityFacadeImpl)


    @Override
    Promise<User> getUser(Long userId) {
        UserGetOptions options = new UserGetOptions()
        return userResource.get(new UserId(userId), options).syncRecover { Throwable throwable ->
            LOGGER.error('name=IdentityFacadeImpl_Get_User_Error, userId = {}', userId.toString())
            throw convertError(throwable).exception()
        }.syncThen { User user ->
            assert user != null
            return user
        }
    }

    @Override
    Promise<User> createUser(User user) {
        return userResource.create(user).syncRecover { Throwable throwable ->
            LOGGER.error('name=IdentityFacadeImpl_Create_User_Error')
            throw convertError(throwable).exception()
        }.syncThen {
            assert user != null
            return user
        }
    }

    @Override
    Promise<Address> getAddress(Long addressId) {
        if (addressId == null) {
            return Promise.pure(null)
        }
        return getUserPersonalInfo(new UserPersonalInfoId(addressId)).syncRecover { Throwable throwable ->
            LOGGER.error('name=IdentityFacadeImpl_Get_Address_Error, userId = {}', addressId.toString())
            throw convertError(throwable).exception()
        }.syncThen { UserPersonalInfo info ->
            if (info == null || !info.type.equalsIgnoreCase('address')) {
                LOGGER.error('name=IdentityFacadeImpl_Invalid_Get_Address_Result')
                throw AppErrors.INSTANCE.identityResultInvalid('Null response or invalid type').exception()
            }
            try {
                Address address = ObjectMapperProvider.instance().treeToValue(info.value, Address)
                return address
            } catch (Exception ex) {
                LOGGER.error('name=IdentityFacadeImpl_Invalid_Get_Address_Result')
                throw AppErrors.INSTANCE.identityResultInvalid('Fail to parse response to address').exception()
            }
        }

    }

    @Override
    Promise<String> getEmail(UserPersonalInfoId emailId) {
        if (emailId == null) {
            return Promise.pure(null)
        }

        return getUserPersonalInfo(emailId).syncRecover { Throwable throwable ->
            LOGGER.error('name=IdentityFacadeImpl_Get_Email_Error, emailId = {}', emailId.toString())
            throw convertError(throwable).exception()
        }.syncThen { UserPersonalInfo info ->
            if (info == null || !info.type.equalsIgnoreCase('email')) {
                LOGGER.error('name=IdentityFacadeImpl_Invalid_Get_Email_Result')
                throw AppErrors.INSTANCE.identityResultInvalid('Null response or invalid type').exception()
            }
            try {
                Email email = ObjectMapperProvider.instance().treeToValue(info.value, Email)
                return email.info
            } catch (Exception ex) {
                LOGGER.error('name=IdentityFacadeImpl_Invalid_Get_Email_Result')
                throw AppErrors.INSTANCE.identityResultInvalid('Fail to parse response to email').exception()
            }
        }
    }

    @Override
    Promise<Organization> getOrganization(Long organizationId) {
        if (organizationId == null) {
            return Promise.pure(null)
        }
        return organizationResource.get(new OrganizationId(organizationId), new OrganizationGetOptions())
                .syncRecover { Throwable throwable ->
            LOGGER.error('name=IdentityFacadeImpl_Get_Organization_Error, organizationId = {}', organizationId.toString() )
            throw convertError(throwable).exception()
        }.syncThen { Organization org ->
            assert org != null
            return org
        }
    }

    @Override
    Promise<UserPersonalInfo> getUserPersonalInfo(UserPersonalInfoId id) {
        if (id == null) {
            return Promise.pure(null)
        }
        return userPersonalInfoResource.get(id, new UserPersonalInfoGetOptions())
    }

    private AppError convertError(Throwable throwable) {
        if (throwable instanceof AppErrorException) {
            return AppErrors.INSTANCE.identityConnectionError()
        } else {
            return AppCommonErrors.INSTANCE.internalServerError(new Exception(throwable))
        }
    }
}
