/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.DeviceType;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 8/11/14.
 */
public class postDeviceType {
    @BeforeSuite
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postCurrency() throws Exception {
        DeviceType deviceType = IdentityModel.DefaultDeviceType(null);

        DeviceType postedDeviceType = Identity.DeviceTypeDefault(deviceType);
        Validator.Validate("Type code validation", deviceType.getTypeCode(), postedDeviceType.getTypeCode());

        DeviceType deviceTypeGet = Identity.DeviceTypeGet(deviceType.getTypeCode());
        Validator.Validate("Type code validation", deviceType.getTypeCode(), deviceTypeGet.getTypeCode());

        postedDeviceType.setInstructionManual(RandomHelper.randomAlphabetic(140));
        DeviceType deviceTypePut = Identity.DeviceTypePut(postedDeviceType);
        Validator.Validate("Type code validation", postedDeviceType.getInstructionManual(), deviceTypePut.getInstructionManual());

        Results<DeviceType> deviceTypeResults = Identity.DeviceTypeGetAll();
        assert deviceTypeResults.getItems().size() != 0;

        Identity.DeviceTypeDelete(deviceType.getTypeCode());
    }
}
