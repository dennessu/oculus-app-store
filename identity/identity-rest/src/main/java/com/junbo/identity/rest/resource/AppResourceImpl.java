/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.identity.rest.service.app.AppService;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.app.App;
import com.junbo.identity.spec.resource.AppResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 2/19/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class AppResourceImpl implements AppResource {
    @Autowired
    private AppService appService;

    @Override
    public Promise<App> getApp(Long appId) {
        return Promise.pure(appService.get(appId));
    }

    @Override
    public Promise<App> postApp(App app) {
        return Promise.pure(appService.post(app));
    }

    @Override
    public Promise<App> updateApp(Long appId, App app) {
        if(appId.compareTo(app.getId().getValue()) != 0) {
            throw AppErrors.INSTANCE.inputParametersMismatch("appId", "app.appId").exception();
        }
        return Promise.pure(appService.updateApp(app));
    }

    @Override
    public Promise<App> deleteApp(Long appId) {
        appService.deleteApp(appId);
        return Promise.pure(null);
    }
}
