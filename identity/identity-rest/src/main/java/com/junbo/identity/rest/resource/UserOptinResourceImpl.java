/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserOptinService;
import com.junbo.identity.spec.model.common.ResultsUtil;
import com.junbo.identity.spec.model.users.UserOptin;
import com.junbo.identity.spec.options.entity.UserOptinGetOptions;
import com.junbo.identity.spec.options.list.UserOptinListOption;
import com.junbo.identity.spec.resource.UserOptinResource;
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
public class UserOptinResourceImpl implements UserOptinResource {

    @Autowired
    private UserOptinService userOptinService;

    @Override
    public Promise<UserOptin> create(UserId userId, UserOptin userOptin) {
        return Promise.pure(userOptinService.save(userId, userOptin));
    }

    @Override
    public Promise<UserOptin> put(UserId userId, UserOptinId userOptinId, UserOptin userOptin) {
        return Promise.pure(userOptinService.update(userId, userOptinId, userOptin));
    }

    @Override
    public Promise<UserOptin> patch(UserId userId, UserOptinId userOptinId, UserOptin userOptin) {
        return null;
    }

    @Override
    public Promise<Void> delete(UserId userId, UserOptinId userOptinId) {
        return null;
    }

    @Override
    public Promise<UserOptin> get(UserId userId, UserOptinId userOptinId, @BeanParam UserOptinGetOptions getOptions) {
        return Promise.pure(userOptinService.get(userId, userOptinId));
    }

    @Override
    public Promise<Results<UserOptin>> list(UserId userId, @BeanParam UserOptinListOption listOptions) {
        if(listOptions == null) {
            listOptions = new UserOptinListOption();
        }
        listOptions.setUserId(userId);
        List<UserOptin> userOptinList = userOptinService.search(listOptions);
        return Promise.pure(ResultsUtil.init(userOptinList,
                listOptions.getLimit() == null ? Integer.MAX_VALUE : listOptions.getLimit()));
    }
}
