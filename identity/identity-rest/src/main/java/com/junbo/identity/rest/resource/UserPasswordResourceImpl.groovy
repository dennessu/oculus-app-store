/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserPasswordFilter
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.options.entity.UserPasswordGetOptions
import com.junbo.identity.spec.options.list.UserPasswordListOptions
import com.junbo.identity.spec.resource.UserPasswordResource
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
public class UserPasswordResourceImpl implements UserPasswordResource {

    @Autowired
    private UserPasswordRepository userPasswordRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserPasswordFilter userPasswordFilter

    @Autowired
    private UserPasswordValidator userPasswordValidator

    @Override
    public Promise<UserPassword> create(UserId userId, UserPassword userPassword) {

        if (userPassword == null) {
            throw new IllegalArgumentException('userPassword is null')
        }

        userPassword = userPasswordFilter.filterForCreate(userPassword)

        userPasswordValidator.validateForCreate(userId, userPassword).then {

            userPasswordRepository.search(new UserPasswordListOptions(
                    userId: userId,
                    active: true
            )).then { List<UserPassword> existing ->
                if (existing != null && existing.size() > 2) {
                    throw new IllegalArgumentException('userPassword have multiple active status.')
                }

                if (existing != null && existing.size() == 1) {
                    UserPassword existingUserPassword = existing.get(0)
                    existingUserPassword.setActive(false)
                    userPasswordRepository.update(existingUserPassword)
                }

                return userPasswordRepository.create(userPassword).then { UserPassword newUserPassword ->
                    created201Marker.mark((Id)newUserPassword.id)

                    newUserPassword = userPasswordFilter.filterForGet(newUserPassword, null)
                    return Promise.pure(newUserPassword)
                }
            }
        }
    }

    @Override
    public Promise<UserPassword> get(UserId userId, UserPasswordId userPasswordId,
                                     @BeanParam UserPasswordGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userPasswordValidator.validateForGet(userId, userPasswordId).then { UserPassword newUserPassword ->
            newUserPassword = userPasswordFilter.filterForGet(newUserPassword,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserPassword)
        }
    }

    @Override
    public Promise<Results<UserPassword>> list(UserId userId, @BeanParam UserPasswordListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userPasswordValidator.validateForSearch(listOptions).then {
            userPasswordRepository.search(listOptions).then { List<UserPassword> userPasswordList ->
                def result = new Results<UserPassword>(items: [])

                userPasswordList.each { UserPassword newUserPassword ->
                    if (newUserPassword != null) {
                        newUserPassword = userPasswordFilter.filterForGet(newUserPassword,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserPassword != null) {
                        result.items.add(newUserPassword)
                    }
                }
                result.setItems(userPasswordList)

                return Promise.pure(result)
            }
        }
    }
}
