/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.ui.testcase;

import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;


/**
 * Created by weiyu_000 on 8/11/14.
 */
public class CSR extends CSRTestCaseBase{

    @Property(
            priority = Priority.BVT,
            features = "CSR UI",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "sample test",
            steps = {
                    "1. Launch browser and navigate to specific url"
            }
    )
    @Test
    public void TestLogin() throws Exception {


    }

}
