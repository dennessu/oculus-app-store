/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.spec.model.subscription.SubsRatingType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by lizwu on 5/27/14.
 */
public class ProcessorRegisterTest extends BaseTest {
    @Test
    public void test(){
        ProcessorRegister.getProcessor(SubsRatingType.PURCHASE.toString());
        ProcessorRegister.getProcessor(SubsRatingType.RENEW.toString());
        ProcessorRegister.getProcessor(SubsRatingType.CANCEL.toString());

        try {
            ProcessorRegister.getProcessor("ILLEGAL_TYPE");
            Assert.fail();
        } catch (IllegalStateException e) {

        }
    }
}
