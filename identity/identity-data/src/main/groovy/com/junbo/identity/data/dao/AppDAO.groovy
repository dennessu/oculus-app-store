/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.app.App

/**
 * Created by liangfu on 2/19/14.
 */
interface AppDAO {
    App save(App app)
    App update(App app)
    App get(Long appId)
    void delete(Long appId)
}
