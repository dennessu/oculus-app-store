/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.rest.service.user.UserTosAcceptanceService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.model.user.UserTosAcceptance;
import com.junbo.identity.spec.resource.UserTosAcceptanceResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 2/13/14.
 */
@Component
@Provider
@org.springframework.context.annotation.Scope("prototype")
public class UserTosAcceptanceResourceImpl implements UserTosAcceptanceResource {
    @Autowired
    private UserTosAcceptanceService userTosAcceptanceService;

    @Override
    public Promise<UserTosAcceptance> postUserTosAcceptance(UserId userId, UserTosAcceptance userTosAcceptance) {
        return Promise.pure(userTosAcceptanceService.save(userId.getValue(), userTosAcceptance));
    }

    @Override
    public Promise<ResultList<UserTosAcceptance>> getUserTosAcceptances(UserId userId, String tos,
                                                                        Integer cursor, Integer count) {
        List<UserTosAcceptance> tosAcceptances = userTosAcceptanceService.getByUserId(userId.getValue(), tos);
        return Promise.pure(ResultListUtil.init(tosAcceptances, count));
    }

    @Override
    public Promise<UserTosAcceptance> getUserTosAcceptance(UserId userId, UserTosId tosAcceptanceId) {
        return Promise.pure(userTosAcceptanceService.get(userId.getValue(), tosAcceptanceId.getValue()));
    }

    @Override
    public Promise<UserTosAcceptance> updateUserTosAcceptance(UserId userId,
                                      UserTosId tosAcceptanceId, UserTosAcceptance userTosAcceptance) {
        return Promise.pure(userTosAcceptanceService.update(userId.getValue(),
                tosAcceptanceId.getValue(), userTosAcceptance));
    }

    @Override
    public Promise<Void> deleteUserTosAcceptance(UserId userId, UserTosId tosAcceptanceId) {
        userTosAcceptanceService.delete(userId.getValue(), tosAcceptanceId.getValue());
        return Promise.pure(null);
    }
}
