/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.app.impl;

import com.junbo.identity.data.dao.AppDAO;
import com.junbo.identity.rest.service.app.AppService;
import com.junbo.identity.spec.model.app.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liangfu on 2/19/14.
 */
@Component
@Transactional
public class AppServiceImpl implements AppService {
    @Autowired
    private AppDAO appDAO;

    @Override
    public App get(Long appId) {
        return appDAO.get(appId);
    }

    @Override
    public App post(App app) {
        return appDAO.save(app);
    }

    @Override
    public App updateApp(App app) {
        return appDAO.update(app);
    }

    @Override
    public void deleteApp(Long appId) {
        appDAO.delete(appId);
    }
}
