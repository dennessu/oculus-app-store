/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserEmailService;
import com.junbo.identity.spec.model.common.ResultsUtil;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.identity.spec.options.entity.UserEmailGetOptions;
import com.junbo.identity.spec.options.list.UserEmailListOptions;
import com.junbo.identity.spec.resource.UserEmailResource;
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
public class UserEmailResourceImpl implements UserEmailResource {

    @Autowired
    private UserEmailService userEmailService;

    @Override
    public Promise<UserEmail> create(UserId userId, UserEmail userEmail) {
        return Promise.pure(userEmailService.save(userId, userEmail));
    }

    @Override
    public Promise<UserEmail> put(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        return Promise.pure(userEmailService.update(userId, userEmailId, userEmail));
    }

    @Override
    public Promise<UserEmail> patch(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> delete(UserId userId, UserEmailId userEmailId) {
        return null;
    }

    @Override
    public Promise<UserEmail> get(UserId userId, UserEmailId userEmailId, @BeanParam UserEmailGetOptions getOptions) {
        return Promise.pure(userEmailService.get(userId, userEmailId));
    }

    @Override
    public Promise<Results<UserEmail>> list(UserId userId, @BeanParam UserEmailListOptions listOptions) {
        if(listOptions == null) {
            listOptions = new UserEmailListOptions();
        }
        listOptions.setUserId(userId);
        List<UserEmail> userEmailList = userEmailService.search(listOptions);
        return Promise.pure(ResultsUtil.init(userEmailList,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }

    @Override
    public Promise<Results<UserEmail>> list(@BeanParam UserEmailListOptions listOptions) {
        List<UserEmail> userEmailList = userEmailService.search(listOptions);
        return Promise.pure(ResultsUtil.init(userEmailList,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }
}
