/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.UserId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.identity.spec.v1.model.Email;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserName;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.option.model.UserGetOptions;
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions;
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource;
import com.junbo.identity.spec.v1.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.UserInfoFacade;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.spec.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user info facade implementation.
 */
public class UserInfoFacadeImpl implements UserInfoFacade{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoFacadeImpl.class);
    private UserPersonalInfoResource piiClient;
    private UserResource userResource;

    @Override
    public Promise<UserInfo> getUserInfo(final Long userId) {
        if(userId == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }
        return userResource.get(new UserId(userId), new UserGetOptions())
                .then(new Promise.Func<User, Promise<UserInfo>>() {
                    @Override
                    public Promise<UserInfo> apply(final User user) {
                        UserInfo userName = getUserName(user).get();
                        String email = getUserEmail(user).get();
                        userName.setEmail(email);
                        return Promise.pure(userName);
                    };
                });
    }

    private Promise<UserInfo> getUserName(final User user){
        if(user == null || user.getId() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }
        final Long userId = user.getId().getValue();
        final UserInfo result = new UserInfo();
        result.setUserId(userId);
        if(user.getName() == null){
            return Promise.pure(result);
        }
        return piiClient.get(user.getName(), new UserPersonalInfoGetOptions())
                .then(new Promise.Func<UserPersonalInfo, Promise<UserInfo>>() {
                    @Override
                    public Promise<UserInfo> apply(UserPersonalInfo userPersonalInfo) {
                        if (userPersonalInfo == null) {
                            throw AppClientExceptions.INSTANCE.userNameNotFound(user.getName().getValue().toString()).exception();
                        }
                        try {
                            UserName userName = ObjectMapperProvider.instance().treeToValue(
                                    userPersonalInfo.getValue(), UserName.class);
                            if(userName != null){
                                result.setFirstName(userName.getGivenName());
                                result.setLastName(userName.getFamilyName());
                                result.setMiddleName(userName.getMiddleName());
                            }
                            return Promise.pure(result);
                        } catch (JsonProcessingException e) {
                            LOGGER.error("error parse json for user:" + userId);
                            throw AppClientExceptions.INSTANCE.userNameNotFound(user.getName().getValue().toString()).exception();
                        }
                    }
                });
    }

    private Promise<String> getUserEmail(final User user){
        if(user == null || user.getId() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }
        final Long userId = user.getId().getValue();
        if(user.getEmails() == null || user.getEmails().isEmpty()){
            return Promise.pure(null);
        }
        return piiClient.get(user.getEmails().get(0).getValue(), new UserPersonalInfoGetOptions())
                .then(new Promise.Func<UserPersonalInfo, Promise<String>>() {
                    @Override
                    public Promise<String> apply(UserPersonalInfo userPersonalInfo) {
                        if (userPersonalInfo == null) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid("user_id").exception();
                        }
                        try {
                            Email email = ObjectMapperProvider.instance().treeToValue(
                                    userPersonalInfo.getValue(), Email.class);
                            if(email != null){
                                return Promise.pure(email.getInfo());
                            }
                            return Promise.pure(null);
                        } catch (JsonProcessingException e) {
                            LOGGER.error("error parse json for user:" + userId);
                            throw AppCommonErrors.INSTANCE.fieldInvalid("user_id").exception();
                        }
                    }
                });
    }

    public void setPiiClient(UserPersonalInfoResource piiClient) {
        this.piiClient = piiClient;
    }

    public void setUserResource(UserResource userResource) {
        this.userResource = userResource;
    }

}
