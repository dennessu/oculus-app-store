/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.PIType;
import com.junbo.test.common.HttpclientHelper;
import org.apache.commons.collections.CollectionUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 11/5/14.
 */
public class PostPIType {
    @BeforeClass(alwaysRun = true)
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod(alwaysRun = true)
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postPIType() throws Exception {
        String typeCode = "CREDITCARD";
        Results<PIType> piTypeResults = Identity.PITypeSearch(typeCode, null);

        if (CollectionUtils.isEmpty(piTypeResults.getItems())) {
            Identity.PITypePostDefault(typeCode);
        }

        // Search for typeCode
        piTypeResults = Identity.PITypeSearch(typeCode, null);
        assert piTypeResults != null;
        assert piTypeResults.getTotal() == 1;
        assert piTypeResults.getItems().size() == 1;

        piTypeResults = Identity.PITypeSearch(typeCode, 10);
        assert piTypeResults != null;
        assert piTypeResults.getTotal() == 1;
        assert piTypeResults.getItems().size() == 1;

        piTypeResults = Identity.PITypeSearch(typeCode, 0);
        assert piTypeResults != null;
        assert piTypeResults.getTotal() == 1;
        assert piTypeResults.getItems().size() == 0;

        // Search for getAll
        Long oldTotal, newTotal;
        piTypeResults = Identity.PITypeSearch(null, null);
        assert piTypeResults != null;
        assert piTypeResults.getTotal() >= 1;
        oldTotal = piTypeResults.getTotal();
        assert piTypeResults.getItems().size() >= 1;

        piTypeResults = Identity.PITypeSearch(null, 10);
        assert piTypeResults != null;
        assert piTypeResults.getTotal() >= 1;
        newTotal = piTypeResults.getTotal();
        assert oldTotal.equals(newTotal);
        assert piTypeResults.getItems().size() >= 1;

        piTypeResults = Identity.PITypeSearch(null, 1);
        assert piTypeResults != null;
        assert piTypeResults.getTotal() >= 1;
        newTotal = piTypeResults.getTotal();
        assert oldTotal.equals(newTotal);
        assert piTypeResults.getItems().size() == 1;
    }
}
