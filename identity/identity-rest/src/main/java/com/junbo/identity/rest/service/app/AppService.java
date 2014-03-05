/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.app;

import com.junbo.identity.spec.model.app.App;

/**
 * Created by liangfu on 2/19/14.
 */
public interface AppService {
    App get(Long appId);

    App post(App app);

    App updateApp(App app);

    void deleteApp(Long appId);
}
