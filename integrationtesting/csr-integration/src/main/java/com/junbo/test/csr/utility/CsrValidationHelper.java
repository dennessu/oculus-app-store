/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.utility;

import com.junbo.csr.spec.model.CsrGroup;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.exception.TestException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by weiyu_000 on 11/25/14.
 */
public class CsrValidationHelper extends ValidationHelper {

    public void validateCsrGroups(List<CsrGroup> groups) {
        List<String> expectedGroups = Arrays.asList("CSR_T1", "CSR_T2", "CSR_T3", "CSR_PENDING", "CSR_INACTIVE");
        verifyEqual(groups.size(), expectedGroups.size(), "verify csr groups size");
        for (String groupName : expectedGroups) {
            boolean isFound = false;
            for (CsrGroup csrGroup : groups) {
                if (groupName.equals(csrGroup.getGroupName())) {
                    isFound = true;
                    verifyEqual(csrGroup.getTier(), groupName.substring(groupName.indexOf("_") + 1), "verify tier name");
                    break;
                }
            }
            if (!isFound) {
                throw new TestException(String.format("fail to find csr group name: %s", groupName));
            }
        }
    }

}
