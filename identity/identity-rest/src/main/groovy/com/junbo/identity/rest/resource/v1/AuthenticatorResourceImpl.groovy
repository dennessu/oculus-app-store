/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserAuthenticatorFilter
import com.junbo.identity.core.service.validator.UserAuthenticatorValidator
import com.junbo.identity.service.UserAuthenticatorService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.identity.spec.v1.option.model.AuthenticatorGetOptions
import com.junbo.identity.spec.v1.resource.AuthenticatorResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.Response

/**
 * Created by xiali_000 on 4/8/2014.
 */
@Transactional
@CompileStatic
class AuthenticatorResourceImpl implements AuthenticatorResource {

    @Autowired
    private UserAuthenticatorService userAuthenticatorService

    @Autowired
    private UserAuthenticatorFilter userAuthenticatorFilter

    @Autowired
    private UserAuthenticatorValidator userAuthenticatorValidator

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Autowired
    private AuthorizeService authorizeService

    @Override
    Promise<UserAuthenticator> create(UserAuthenticator userAuthenticator) {
        if (userAuthenticator == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()

        }

        def callback = authorizeCallbackFactory.create(userAuthenticator.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userAuthenticator = userAuthenticatorFilter.filterForCreate(userAuthenticator)

            return userAuthenticatorValidator.validateForCreate(userAuthenticator).then {
                return userAuthenticatorService.create(userAuthenticator).then { UserAuthenticator newUserAuthenticator ->
                    Created201Marker.mark(newUserAuthenticator.getId())

                    newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator, null)
                    return Promise.pure(newUserAuthenticator)
                }
            }
        }
    }

    @Override
    Promise<UserAuthenticator> put(UserAuthenticatorId userAuthenticatorId, UserAuthenticator userAuthenticator) {

        if (userAuthenticatorId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userAuthenticator == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userAuthenticatorService.get(userAuthenticatorId).then { UserAuthenticator oldUserAuthenticator ->
            if (oldUserAuthenticator == null) {
                throw AppErrors.INSTANCE.userAuthenticatorNotFound(userAuthenticatorId).exception()
            }

            def callback = authorizeCallbackFactory.create(userAuthenticator.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userAuthenticator = userAuthenticatorFilter.filterForPut(userAuthenticator, oldUserAuthenticator)

                return userAuthenticatorValidator.validateForUpdate(
                        userAuthenticatorId, userAuthenticator, oldUserAuthenticator).then {
                    return userAuthenticatorService.update(userAuthenticator, oldUserAuthenticator).then {
                        UserAuthenticator newUserAuthenticator ->
                        newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator, null)
                        return Promise.pure(newUserAuthenticator)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserAuthenticator> get(UserAuthenticatorId userAuthenticatorId, AuthenticatorGetOptions getOptions) {

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userAuthenticatorValidator.validateForGet(userAuthenticatorId).then { UserAuthenticator authenticator ->
            def callback = authorizeCallbackFactory.create(authenticator.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userAuthenticatorNotFound(userAuthenticatorId).exception()
                }

                authenticator = userAuthenticatorFilter.filterForGet(authenticator,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(authenticator)
            }
        }

    }

    @Override
    Promise<Results<UserAuthenticator>> list(AuthenticatorListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userAuthenticatorValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { Results<UserAuthenticator> authenticatorList ->
                def result = new Results<UserAuthenticator>(items: [])
                result.total = authenticatorList.total

                authenticatorList.items.each { UserAuthenticator newUserAuthenticator ->
                    def callback = authorizeCallbackFactory.create(newUserAuthenticator.userId)
                    return RightsScope.with(authorizeService.authorize(callback)) {
                        if (newUserAuthenticator != null) {
                            newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator,
                                    listOptions.properties?.split(',') as List<String>)

                            if (AuthorizeContext.hasRights('read')) {
                                result.items.add(newUserAuthenticator)
                            } else {
                                // In case the user has no rights to read
                                result.total = result.total - 1
                            }
                        }

                        return Promise.pure()
                    }

                }
                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Response> delete(UserAuthenticatorId userAuthenticatorId) {
        if (userAuthenticatorId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        return userAuthenticatorValidator.validateForGet(userAuthenticatorId).then { UserAuthenticator authenticator ->
            def callback = authorizeCallbackFactory.create(authenticator.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userAuthenticatorService.delete(userAuthenticatorId).then {
                    return Promise.pure(Response.status(204).build())
                }
            }
        }
    }

    private Promise<Results<UserAuthenticator>> search(AuthenticatorListOptions listOptions) {
        if (listOptions.userId != null && listOptions.type != null && listOptions.externalId != null) {
            return userAuthenticatorService.searchByUserIdAndTypeAndExternalId(listOptions.userId, listOptions.type,
                    listOptions.externalId, listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null && listOptions.type != null) {
            return userAuthenticatorService.searchByUserIdAndType(listOptions.userId, listOptions.type,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null && listOptions.externalId != null) {
            return userAuthenticatorService.searchByUserIdAndExternalId(listOptions.userId, listOptions.externalId,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.externalId != null && listOptions.type != null) {
            return userAuthenticatorService.searchByExternalIdAndType(listOptions.externalId, listOptions.type,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userAuthenticatorService.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset)
        } else if (listOptions.externalId != null) {
            return userAuthenticatorService.searchByExternalId(listOptions.externalId, listOptions.limit,
                    listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Unsupported search operation').exception()
        }
    }
}
