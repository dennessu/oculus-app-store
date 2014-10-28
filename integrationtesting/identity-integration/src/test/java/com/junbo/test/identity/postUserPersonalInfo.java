/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserDOB;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.Validator;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.*;

import java.util.Date;

/**
 * @author dw
 */
public class postUserPersonalInfo {

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
    public void postUserPersonalInfo() throws Exception {
        User user = Identity.UserPostDefault();
        UserPersonalInfo upi = IdentityModel.DefaultUserPersonalInfoAddress();
        UserPersonalInfo posted = Identity.UserPersonalInfoPost(user.getId(), upi);
        UserPersonalInfo stored = Identity.UserPersonalInfoGetByUserPersonalInfoId(posted.getId());
        Validator.Validate("validate user personal info type", upi.getType(), stored.getType());
        Validator.Validate("validate user personal info value", upi.getValue(), stored.getValue());
    }

    @Test(groups = "bvt")
    public void postUserPersonalInfoUpdate() throws Exception {
        User user = Identity.UserPostDefault();
        UserPersonalInfo upi = IdentityModel.DefaultUserPersonalInfoDob();
        UserPersonalInfo posted = Identity.UserPersonalInfoPost(user.getId(), upi);
        UserDOB originalDOB = (UserDOB)JsonHelper.JsonNodeToObject(upi.getValue(), UserDOB.class);
        UserDOB userDOBPosted = (UserDOB)JsonHelper.JsonNodeToObject(posted.getValue(), UserDOB.class);
        Validator.Validate("validate DOB data", originalDOB.getInfo(), userDOBPosted.getInfo());
        Validator.Validate("validate lastvalidationTime", posted.getLastValidateTime(), null);

        Date date = new Date();
        posted.setLastValidateTime(date);
        posted = Identity.UserPersonalInfoPut(user.getId(), posted);
        Validator.Validate("validate lastvalidationTime", posted.getLastValidateTime(), date);

        posted.setLastValidateTime(null);
        posted = Identity.UserPersonalInfoPut(user.getId(), posted);
        Validator.Validate("validate lastvalidationTime", posted.getLastValidateTime(), null);

        UserPersonalInfo gotten = Identity.UserPersonalInfoGetByUserPersonalInfoId(posted.getId());
        Validator.Validate("validate lastvalidationTime", gotten.getLastValidateTime(), null);
    }
}
