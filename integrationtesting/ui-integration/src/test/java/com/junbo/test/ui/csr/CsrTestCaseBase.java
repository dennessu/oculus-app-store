/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.ui.csr;

import com.junbo.test.ui.TestCaseBase;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Created by weiyu_000 on 8/11/14.
 */
public class CSRTestCaseBase extends TestCaseBase {

    @BeforeMethod
    protected void beforeMethod(Method method) {
        super.beforeMethod(method);

    }

    @Override
    protected void initTestEnvironment() {

    }


}
