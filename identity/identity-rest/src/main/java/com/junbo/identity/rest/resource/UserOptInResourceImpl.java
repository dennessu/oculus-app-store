/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

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
    public Promise<UserOptIn> postUserOptIn(Long userId, UserOptIn userOptIn) {
        return Promise.pure(userOptInService.save(userId, userOptIn));
    }

    @Override
    public Promise<ResultList<UserOptIn>> getUserOptIns(Long userId, String type, Integer cursor, Integer count) {
        List<UserOptIn> userOptIns = userOptInService.getByUserId(userId, type);
        return Promise.pure(ResultListUtil.init(userOptIns, count));
    }

    @Override
    public Promise<UserOptIn> getUserOptIn(Long userId, Long optInId) {
        return Promise.pure(userOptInService.get(userId, optInId));
    }

    @Override
    public Promise<UserOptIn> updateUserOptIn(Long userId, Long optInId, UserOptIn userOptIn) {
        return Promise.pure(userOptInService.update(userId, optInId, userOptIn));
    }

    @Override
    public Promise<Void> deleteUserOptIn(Long userId, Long optInId) {
        userOptInService.delete(userId, optInId);
        return Promise.pure(null);
    }
}
