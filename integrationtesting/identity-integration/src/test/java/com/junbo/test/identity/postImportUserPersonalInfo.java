/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

        User importedUser = Identity.UserGetByUserId(oculusOutput.getUserId());
        Validator.Validate("validate user name", oculusInput.getUsername(), importedUser.getUsername());
        Validator.Validate("validate user status", oculusInput.getStatus(), importedUser.getStatus());

        //UserPersonalInfo importedPI = Identity.UserPersonalInfoGetByUserPersonalInfoId(importedUser.)
    }
}
