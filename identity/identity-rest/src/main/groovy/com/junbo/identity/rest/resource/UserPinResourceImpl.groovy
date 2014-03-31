/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserPinFilter
import com.junbo.identity.core.service.validator.UserPinValidator
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.options.entity.UserPinGetOptions
import com.junbo.identity.spec.options.list.UserPinListOptions
import com.junbo.identity.spec.resource.UserPinResource
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
class UserPinResourceImpl implements UserPinResource {

    @Autowired
    private UserPinRepository userPinRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserPinFilter userPinFilter

    @Autowired
    private UserPinValidator userPinValidator

    @Override
    Promise<UserPin> create(UserId userId, UserPin userPin) {
        if (userPin == null) {
            throw new IllegalArgumentException('userPin is null')
        }

        userPin = userPinFilter.filterForCreate(userPin)

        userPinValidator.validateForCreate(userId, userPin).then {

            userPinRepository.search(new UserPinListOptions(
                    userId: userId,
                    active: true
            )).then { List<UserPin> existing ->
                if (existing != null && existing.size() > 2) {
                    throw new IllegalArgumentException('userPin have multiple active status.')
                }

                if (existing != null && existing.size() == 1) {
                    UserPin existingUserPin = existing.get(0)
                    existingUserPin.setActive(false)
                    userPinRepository.update(existingUserPin)
                }

                return userPinRepository.create(userPin).then { UserPin newUserPin ->
                    created201Marker.mark((Id)newUserPin.id)

                    newUserPin = userPinFilter.filterForGet(newUserPin, null)
                    return Promise.pure(newUserPin)
                }
            }
        }
    }

    @Override
    Promise<UserPin> get(UserId userId, UserPinId userPinId, @BeanParam UserPinGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userPinValidator.validateForGet(userId, userPinId).then { UserPin newUserPin ->
            newUserPin = userPinFilter.filterForGet(newUserPin, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserPin)
        }
    }

    @Override
    Promise<Results<UserPin>> list(UserId userId, @BeanParam UserPinListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userPinValidator.validateForSearch(listOptions).then {
            userPinRepository.search(listOptions).then { List<UserPin> userPinList ->
                def result = new Results<UserPin>(items: [])

                userPinList.each { UserPin newUserPin ->
                    if (newUserPin != null) {
                        newUserPin = userPinFilter.filterForGet(newUserPin,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserPin != null) {
                        result.items.add(newUserPin)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
