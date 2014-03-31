/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserOptinId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserOptinFilter
import com.junbo.identity.core.service.validator.UserOptinValidator
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.identity.spec.options.entity.UserOptinGetOptions
import com.junbo.identity.spec.options.list.UserOptinListOptions
import com.junbo.identity.spec.resource.UserOptinResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.BeanParam
import javax.ws.rs.ext.Provider
/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserOptinResourceImpl implements UserOptinResource {

    @Autowired
    private UserOptinRepository userOptinRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserOptinFilter userOptinFilter

    @Autowired
    private UserOptinValidator userOptinValidator

    @Override
    Promise<UserOptin> create(UserId userId, UserOptin userOptin) {
        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        userOptin = userOptinFilter.filterForCreate(userOptin)

        userOptinValidator.validateForCreate(userId, userOptin).then {
            userOptinRepository.create(userOptin).then { UserOptin newUserOptin ->
                created201Marker.mark((Id)newUserOptin.id)

                newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                return Promise.pure(newUserOptin)
            }
        }
    }

    @Override
    Promise<UserOptin> put(UserId userId, UserOptinId userOptinId, UserOptin userOptin) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userOptinId == null) {
            throw new IllegalArgumentException('userOptinId is null')
        }

        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        return userOptinRepository.get(userOptinId).then { UserOptin oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            userOptin = userOptinFilter.filterForPut(userOptin, oldUserOptin)

            return userOptinValidator.validateForUpdate(userId, userOptinId, userOptin, oldUserOptin).then {
                userOptinRepository.update(userOptin).then { UserOptin newUserOptin ->
                    newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                    return Promise.pure(newUserOptin)
                }
            }
        }
    }

    @Override
    Promise<UserOptin> patch(UserId userId, UserOptinId userOptinId, UserOptin userOptin) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userOptinId == null) {
            throw new IllegalArgumentException('userOptinId is null')
        }

        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        return userOptinRepository.get(userOptinId).then { UserOptin oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            userOptin = userOptinFilter.filterForPatch(userOptin, oldUserOptin)

            userOptinValidator.validateForUpdate(userId, userOptinId, userOptin, oldUserOptin).then {

                userOptinRepository.update(userOptin).then { UserOptin newUserOptin ->
                    newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                    return Promise.pure(newUserOptin)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserOptinId userOptinId) {
        return userOptinValidator.validateForGet(userId, userOptinId).then {
            userOptinRepository.delete(userOptinId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<UserOptin> get(UserId userId, UserOptinId userOptinId, @BeanParam UserOptinGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userOptinValidator.validateForGet(userId, userOptinId).then { UserOptin newUserOptin ->
            newUserOptin = userOptinFilter.filterForGet(newUserOptin,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserOptin)
        }
    }

    @Override
    Promise<Results<UserOptin>> list(UserId userId, @BeanParam UserOptinListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userOptinValidator.validateForSearch(listOptions).then {
            userOptinRepository.search(listOptions).then { List<UserOptin> userOptinList ->
                def result = new Results<UserOptin>(items: [])

                userOptinList.each { UserOptin newUserOptin ->
                    if (newUserOptin != null) {
                        newUserOptin = userOptinFilter.filterForGet(newUserOptin,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserOptin != null) {
                        result.items.add(newUserOptin)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
