/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserTosFilter
import com.junbo.identity.core.service.validator.UserTosValidator
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.identity.spec.options.entity.UserTosGetOptions
import com.junbo.identity.spec.options.list.UserTosListOptions
import com.junbo.identity.spec.resource.UserTosResource
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
public class UserTosResourceImpl implements UserTosResource {

    @Autowired
    private UserTosRepository userTosRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserTosFilter userTosFilter

    @Autowired
    private UserTosValidator userTosValidator

    @Override
    public Promise<UserTos> create(UserId userId, UserTos userTos) {
        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        userTos = userTosFilter.filterForCreate(userTos)

        userTosValidator.validateForCreate(userId, userTos).then {
            userTosRepository.create(userTos).then { UserTos newUserTos ->
                created201Marker.mark((Id)newUserTos.id)

                newUserTos = userTosFilter.filterForGet(newUserTos, null)
                return Promise.pure(newUserTos)
            }
        }
    }

    @Override
    public Promise<UserTos> put(UserId userId, UserTosId userTosId, UserTos userTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTosId == null) {
            throw new IllegalArgumentException('userTosId is null')
        }

        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        return userTosRepository.get(userTosId).then { UserTos oldUserTos ->
            if (oldUserTos == null) {
                throw AppErrors.INSTANCE.userTosNotFound(userTosId).exception()
            }

            userTos = userTosFilter.filterForPut(userTos, oldUserTos)

            return userTosValidator.validateForUpdate(userId, userTosId, userTos, oldUserTos).then {
                userTosRepository.update(userTos).then { UserTos newUserTos ->
                    newUserTos = userTosFilter.filterForGet(newUserTos, null)
                    return Promise.pure(newUserTos)
                }
            }
        }
    }

    @Override
    public Promise<UserTos> patch(UserId userId, UserTosId userTosId, UserTos userTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTosId == null) {
            throw new IllegalArgumentException('userTosId is null')
        }

        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        return userTosRepository.get(userTosId).then { UserTos oldUserTos ->
            if (oldUserTos == null) {
                throw AppErrors.INSTANCE.userTosNotFound(userTosId).exception()
            }

            userTos = userTosFilter.filterForPatch(userTos, oldUserTos)

            userTosValidator.validateForUpdate(userId, userTosId, userTos, oldUserTos).then {

                userTosRepository.update(userTos).then { UserTos newUserTos ->
                    newUserTos = userTosFilter.filterForGet(newUserTos, null)
                    return Promise.pure(newUserTos)
                }
            }
        }
    }

    @Override
    public Promise<Void> delete(UserId userId, UserTosId userTosId) {
        return userTosValidator.validateForGet(userId, userTosId).then {
            userTosRepository.delete(userTosId)

            return Promise.pure(null)
        }
    }

    @Override
    public Promise<UserTos> get(UserId userId, UserTosId userTosId, @BeanParam UserTosGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userTosValidator.validateForGet(userId, userTosId).then { UserTos newUserTos ->
            newUserTos = userTosFilter.filterForGet(newUserTos,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserTos)
        }
    }

    @Override
    public Promise<Results<UserTos>> list(UserId userId, @BeanParam UserTosListOptions listOptions) {

        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userTosValidator.validateForSearch(listOptions).then {
            userTosRepository.search(listOptions).then { List<UserTos> userTosList ->
                def result = new Results<UserTos>(items: [])

                userTosList.each { UserTos newUserTos ->
                    if (newUserTos != null) {
                        newUserTos = userTosFilter.filterForGet(newUserTos,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserTos != null) {
                        result.items.add(newUserTos)
                    }
                }
                result.setItems(userTosList)

                return Promise.pure(result)
            }
        }
    }
}
