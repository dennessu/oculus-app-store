/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.piid;


import com.junbo.common.jackson.piid.annotation.TestCompoundId;

/**
 * PaymentTransaction.
 */
public class PaymentTransaction {
    @TestCompoundId
    private TestId testId;

    public TestId getTestId() {
        return testId;
    }

    public void setTestId(TestId testId) {
        this.testId = testId;
    }
}
