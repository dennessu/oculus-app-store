/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.data.util.Constants;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.data.entity.user.UserStatus;
import com.junbo.identity.rest.service.user.UserService;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.spec.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Java cod for UserResourceImpl.
 */

@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserResourceImpl implements UserResource {
    @Autowired
    private UserService userService;

    @Override
    public Promise<User> postUser(User user) {
        user = userService.save(user);
        return Promise.pure(user);
    }

    @Override
    public Promise<Results<User>> getUsers(String userName, String userNamePrefix, Integer cursor, Integer count) {
        List<User> users = null;

        if(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userNamePrefix)) {
            throw AppErrors.INSTANCE.unnecessaryParameterField(Constants.USER_NAME_PREFIX).exception();
        }
        Results<User> result = new Results<User>();
        if(!StringUtils.isEmpty(userName)) {
            users = userService.getByUserName(userName, UserStatus.ACTIVE.toString());
            result = ResultListUtil.init(users, Integer.MAX_VALUE);
        }
        else {
            users = userService.searchUser(userNamePrefix, UserStatus.ACTIVE.toString(), cursor, count);
            result = ResultListUtil.init(users, count);
        }

        return Promise.pure(result);
    }

    @Override
    public Promise<User> getUser(UserId id) {
        return Promise.pure(userService.get(id.getValue()));
    }

    @Override
    public Promise<User> putUser(UserId id, User user) {
        return Promise.pure(userService.update(id.getValue(), user));
    }

    @Override
    public Promise<User> authenticateUser(String userName, String password) {
        List<User> users = userService.getByUserName(userName, UserStatus.ACTIVE.toString());
        if(CollectionUtils.isEmpty(users) || users.size() > 1) {
            throw AppErrors.INSTANCE.userStatusError("userName = " + userName).exception();
        }

        return Promise.pure(userService.authenticate(users.get(0).getUserName(), password));
    }

    @Override
    public Promise<User> updatePassword(UserId id, String oldPassword, String newPassword) {
        User user = userService.get(id.getValue());
        userService.authenticate(user.getUserName(), oldPassword);
        userService.savePassword(user, newPassword);
        return Promise.pure(user);
    }

    @Override
    public Promise<User> restPassword(UserId id, String newPassword) {
        User user = userService.get(id.getValue());
        userService.savePassword(user, newPassword);
        return Promise.pure(null);
    }
}
