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
import com.junbo.test.common.libs.IdConverter;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Test(groups = "dailies")
    public void postInvalidUserPersonalInfo() throws Exception {
        User user = Identity.UserPostDefault();
        UserPersonalInfo upi = IdentityModel.DefaultUserPersonalInfoAddress();
        upi.setType("FAKE");
        upi.setUserId(user.getId());

        // https://oculus.atlassian.net/browse/SER-752
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1UserPersonalInfoURI,
                JsonHelper.JsonSerializer(upi), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Field value is invalid";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        // https://oculus.atlassian.net/browse/SER-753
        upi = IdentityModel.DefaultUserPersonalInfoAddress();
        UserPersonalInfo posted = Identity.UserPersonalInfoPost(user.getId(), upi);
        response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1UserPersonalInfoURI + "/" + IdConverter.idToHexString(posted.getId()),
                null, HttpclientHelper.HttpRequestType.delete, nvps);
        Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
        errorMessage = "pii operation is not supported";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    @Test(groups = "dailies", enabled = false)
    public void patchUserPersonalInfo() throws Exception {
        User user = Identity.UserPostDefault();
        UserPersonalInfo upi = IdentityModel.DefaultUserPersonalInfoAddress();
        upi = Identity.UserPersonalInfoPost(user.getId(), upi);

        UserPersonalInfo newUPI = new UserPersonalInfo();
        newUPI.setLastValidateTime(new Date());
        upi = Identity.UserPersonalInfoPatch(upi.getId(), newUPI);
        assert upi.getLastValidateTime() == newUPI.getLastValidateTime();
    }
}
