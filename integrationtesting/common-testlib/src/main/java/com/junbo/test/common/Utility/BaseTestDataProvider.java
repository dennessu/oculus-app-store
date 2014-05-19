/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Utility;

import com.junbo.test.common.libs.DBHelper;

/**
 * Created by Yunlong on 4/4/14.
 */
public abstract class BaseTestDataProvider {
    protected DBHelper dbHelper = new DBHelper();

    public BaseTestDataProvider() {
    }

}
