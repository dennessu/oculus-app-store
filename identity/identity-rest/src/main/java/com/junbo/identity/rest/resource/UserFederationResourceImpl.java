/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.identity.rest.service.user.UserFederationService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.model.user.UserFederation;
import com.junbo.identity.spec.resource.UserFederationResource;
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
public class UserFederationResourceImpl implements UserFederationResource {
    @Autowired
    private UserFederationService userFederationService;

    @Override
    public Promise<UserFederation> postUserFederation(Long userId, UserFederation userFederation) {
        return Promise.pure(userFederationService.save(userId, userFederation));
    }

    @Override
    public Promise<ResultList<UserFederation>> getUserFederations(Long userId, String type,
                                                                  Integer cursor, Integer count) {
        List<UserFederation> userFederations = userFederationService.getByUserId(userId, type);
        return Promise.pure(ResultListUtil.init(userFederations, count));
    }

    @Override
    public Promise<UserFederation> getUserFederation(Long userId, Long federationId) {
        return Promise.pure(userFederationService.get(userId, federationId));
    }

    @Override
    public Promise<UserFederation> updateUserFederation(Long userId, Long federationId, UserFederation userFederation) {
        return Promise.pure(userFederationService.update(userId, federationId, userFederation));
    }

    @Override
    public Promise<Void> deleteUserFederation(Long userId, Long federationId) {
        userFederationService.delete(userId, federationId);
        return Promise.pure(null);
    }
}
