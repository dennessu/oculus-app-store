/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.piid;


import com.junbo.common.jackson.piid.annotation.TestCompoundId;

import java.util.List;

/**
 * TestEntity.
 */
public class Person {
    @TestCompoundId
    private List<TestId> testIdList;

    public List<TestId> getTestIdList() {
        return testIdList;
    }

    public void setTestIdList(List<TestId> testIdList) {
        this.testIdList = testIdList;
    }
}
