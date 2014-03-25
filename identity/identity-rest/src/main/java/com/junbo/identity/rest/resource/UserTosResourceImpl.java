/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserTosService;
import com.junbo.identity.spec.model.common.ResultsUtil;
import com.junbo.identity.spec.model.users.UserTos;
import com.junbo.identity.spec.options.entity.UserTosGetOptions;
import com.junbo.identity.spec.options.list.UserTosListOptions;
import com.junbo.identity.spec.resource.UserTosResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserTosResourceImpl implements UserTosResource {
    @Autowired
    private UserTosService userTosService;

    @Override
    public Promise<UserTos> create(UserId userId, UserTos userTos) {
        return Promise.pure(userTosService.save(userId, userTos));
    }

    @Override
    public Promise<UserTos> put(UserId userId, UserTosId userTosId, UserTos userTos) {
        return Promise.pure(userTosService.update(userId, userTosId, userTos));
    }

    @Override
    public Promise<UserTos> patch(UserId userId, UserTosId userTosId, UserTos userTos) {
        return null;
    }

    @Override
    public Promise<Void> delete(UserId userId, UserTosId userTosId) {
        return null;
    }

    @Override
    public Promise<UserTos> get(UserId userId, UserTosId userTosId, @BeanParam UserTosGetOptions getOptions) {
        return Promise.pure(userTosService.get(userId, userTosId));
    }

    @Override
    public Promise<Results<UserTos>> list(UserId userId, @BeanParam UserTosListOptions listOptions) {
        if(listOptions == null) {
            listOptions = new UserTosListOptions();
        }
        listOptions.setUserId(userId);
        List<UserTos> userTosList = userTosService.search(listOptions);
        return Promise.pure(ResultsUtil.init(userTosList,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }
}
