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
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 8/11/14.
 */
public class postDeviceType {

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

    @Property(
            priority = Priority.BVT,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test deviceType POST/PUT/GET",
            environment = "onebox",
            steps = {
                    "1. post a device type" +
                            "/n 2. get the device type" +
                            "/n 3. update the device type"
            }
    )
    @Test(groups = "bvt")
    public void postDeviceType() throws Exception {
        DeviceType deviceType = IdentityModel.DefaultDeviceType(null);

        DeviceType postedDeviceType = Identity.DeviceTypeDefault(deviceType);
        Validator.Validate("Type code validation", deviceType.getTypeCode(), postedDeviceType.getTypeCode());

        DeviceType deviceTypeGet = Identity.DeviceTypeGet(deviceType.getTypeCode());
        Validator.Validate("Type code validation", deviceType.getTypeCode(), deviceTypeGet.getTypeCode());

        postedDeviceType.setInstructionManual(RandomHelper.randomAlphabetic(140));
        DeviceType deviceTypePut = Identity.DeviceTypePut(postedDeviceType);
        Validator.Validate("Type code validation", postedDeviceType.getInstructionManual(), deviceTypePut.getInstructionManual());

        Long oldTotal, newTotal;
        Results<DeviceType> deviceTypeResults = Identity.DeviceTypeGetAll(null);
        assert deviceTypeResults.getTotal() > 0;
        oldTotal = deviceTypeResults.getTotal();
        assert deviceTypeResults.getItems().size() != 0;

        deviceTypeResults = Identity.DeviceTypeGetAll(1);
        assert deviceTypeResults.getTotal() > 0;
        newTotal = deviceTypeResults.getTotal();
        assert oldTotal.equals(newTotal);
        assert deviceTypeResults.getItems().size() == 1;

        deviceTypeResults = Identity.DeviceTypeGetAll(0);
        assert deviceTypeResults.getTotal() > 0;
        newTotal = deviceTypeResults.getTotal();
        assert oldTotal.equals(newTotal);
        assert deviceTypeResults.getItems().size() == 0;

        Identity.DeviceTypeDelete(deviceType.getTypeCode());
    }
}
