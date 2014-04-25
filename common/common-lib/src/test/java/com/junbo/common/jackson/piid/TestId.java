/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.piid;

import com.junbo.common.jackson.aware.CompoundAware;

/**
 * TestId.
 */
public class TestId implements CompoundAware {
    private Long userId;

    private Long testId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    @Override
    public Long getPrimaryId() {
        return testId;
    }
}
