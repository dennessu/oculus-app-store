/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptInId;
import com.junbo.identity.rest.service.user.UserOptInService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.model.user.UserOptIn;
import com.junbo.identity.spec.resource.UserOptInResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 2/13/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserOptInResourceImpl implements UserOptInResource {
    @Autowired
    private UserOptInService userOptInService;

    @Override
    public Promise<UserOptIn> postUserOptIn(UserId userId, UserOptIn userOptIn) {
        return Promise.pure(userOptInService.save(userId.getValue(), userOptIn));
    }

    @Override
    public Promise<ResultList<UserOptIn>> getUserOptIns(UserId userId, String type, Integer cursor, Integer count) {
        List<UserOptIn> userOptIns = userOptInService.getByUserId(userId.getValue(), type);
        return Promise.pure(ResultListUtil.init(userOptIns, count));
    }

    @Override
    public Promise<UserOptIn> getUserOptIn(UserId userId, UserOptInId optInId) {
        return Promise.pure(userOptInService.get(userId.getValue(), optInId.getValue()));
    }

    @Override
    public Promise<UserOptIn> updateUserOptIn(UserId userId, UserOptInId optInId, UserOptIn userOptIn) {
        return Promise.pure(userOptInService.update(userId.getValue(), optInId.getValue(), userOptIn));
    }

    @Override
    public Promise<Void> deleteUserOptIn(UserId userId, UserOptInId optInId) {
        userOptInService.delete(userId.getValue(), optInId.getValue());
        return Promise.pure(null);
    }
}
