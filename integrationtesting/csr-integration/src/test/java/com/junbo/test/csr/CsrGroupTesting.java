/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr;

import com.junbo.csr.spec.def.SearchType;
import com.junbo.csr.spec.model.CsrGroup;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by weiyu_000 on 11/25/14.
 */
public class CsrGroupTesting extends CsrBaseTestClass {


    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get csr group for different tiers",
            steps = {
                    "1. Get csr group",
                    "2. verify response"

            }
    )
    @Test
    public void testGetGroupTiers() throws Exception {
        testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        List<CsrGroup> csrGroups = testDataProvider.getCsrGroups();

        validationHelper.validateCsrGroups(csrGroups);
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get csr group by group name",
            steps = {
                    "1. Get csr group by group name",
                    "2. verify response"

            }
    )
    @Test
    public void testGetGroupTiersByGroupName() throws Exception {
        testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        List<CsrGroup> csrGroups = testDataProvider.getCsrGroups("CSR_T1", null);

        validationHelper.verifyEqual(csrGroups.get(0).getTier(), "T1", "verify get csr group by group name");
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get csr group by user id",
            steps = {
                    "1. Get csr group by user id",
                    "2. verify response"
            }
    )
    @Test
    public void testGetGroupTiersByUserId() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        List<CsrGroup> csrGroups = testDataProvider.getCsrGroups(null, uid);

        validationHelper.verifyEqual(csrGroups.get(0).getTier(), "T3", "verify get csr group by uid");
        validationHelper.verifyEqual(csrGroups.get(0).getGroupName(), "CSR_T3", "verify get csr group by uid");
    }

}
