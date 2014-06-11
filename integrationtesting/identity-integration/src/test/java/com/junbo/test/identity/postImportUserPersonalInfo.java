/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author dw
 */
public class postImportUserPersonalInfo {
    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "dailies")
    public void importMigrationData() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);

        User user = Identity.UserGetByUserId(oculusOutput.getUserId());
        Validator.Validate("validate user name", oculusInput.getUsername(), user.getUsername());
        Validator.Validate("validate user status", oculusInput.getStatus(), user.getStatus());

        List<UserPersonalInfo> userPersonalInfos = Identity.UserPersonalInfosGetByUserId(user.getId());
        for (UserPersonalInfo upi : userPersonalInfos) {
            if (upi.getType().equals("NAME")) {
                UserName userName = (UserName) JsonHelper.JsonNodeToObject(upi.getValue(), UserName.class);
                Validator.Validate("validate given name", oculusInput.getFirstName(), userName.getGivenName());
                Validator.Validate("validate family name", oculusInput.getLastName(), userName.getFamilyName());
                Validator.Validate("validate nick name", oculusInput.getNickname(), userName.getNickName());
            } else if (upi.getType().equals("EMAIL")) {
                Email email = (Email) JsonHelper.JsonNodeToObject(upi.getValue(), Email.class);
                Validator.Validate("validate email", oculusInput.getEmail(), email.getInfo());
            } else if (upi.getType().equals("GENDER")) {
                UserGender userGender = (UserGender) JsonHelper.JsonNodeToObject(upi.getValue(), UserGender.class);
                Validator.Validate("validate gender", oculusInput.getGender(), userGender.getInfo());
            } else if (upi.getType().equals("DOB")) {
                UserDOB userDob = (UserDOB) JsonHelper.JsonNodeToObject(upi.getValue(), UserDOB.class);
                Validator.Validate("validate dob", oculusInput.getDob(), userDob.getInfo());
            }
        }

        Organization organization = Identity.OrganizationGetByOrganizationId(oculusOutput.getOrganizationId());
        Validator.Validate("validate organization name", oculusInput.getDevCenterCompany(), organization.getName());
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateEmailDifferentUserName() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        Identity.ImportMigrationData(oculusInput);
        oculusInput.setUsername(RandomHelper.randomAlphabetic(10));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.DefaultIdentityV1ImportsURI,
                JsonHelper.JsonSerializer(oculusInput), 2);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = String.format("User %s Email %s already used.",
                oculusInput.getCurrentId(), oculusInput.getEmail());
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateUserName() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);
        User user1 = Identity.UserGetByUserId(oculusOutput.getUserId());
        UserPersonalInfoLink upil1 = user1.getEmails().get(0);
        UserPersonalInfo upi1 = Identity.UserPersonalInfoGetByUserPersonalInfoId(upil1.getValue());
        Validator.Validate("validate original email", true, upi1.getValue().toString().contains(oculusInput.getEmail()));

        oculusInput.setEmail(RandomHelper.randomAlphabetic(10) + "@163.com");
        Identity.ImportMigrationData(oculusInput);
        User user2 = Identity.UserGetByUserId(oculusOutput.getUserId());
        UserPersonalInfoLink upil2 = user2.getEmails().get(0);
        UserPersonalInfo upi2 = Identity.UserPersonalInfoGetByUserPersonalInfoId(upil2.getValue());
        Validator.Validate("validate original email", true, upi2.getValue().toString().contains(oculusInput.getEmail()));
    }
}