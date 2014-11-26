/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr;

import com.junbo.test.csr.utility.CsrTestDataProvider;
import com.junbo.test.csr.utility.CsrValidationHelper;

/**
 * Created by weiyu_000 on 11/26/14.
 */
public abstract class CsrBaseTestClass {
    CsrTestDataProvider testDataProvider =  new CsrTestDataProvider();
    CsrValidationHelper validationHelper = new CsrValidationHelper();

}
