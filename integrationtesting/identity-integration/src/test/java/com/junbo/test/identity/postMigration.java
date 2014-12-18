/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 10/10/14.
 */
public class postMigration {
    @BeforeClass(alwaysRun = true)
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeaderForMigration();
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
    // The test case will be run as follows
    // Import one username/email; check the username & email cannot be used;
    // Import same username/email again; check the username & email cannot be used
    // Import same username2/email2; check both username & email2 cannot be used
    public void postMigration() throws Exception {
        UsernameMailBlocker blocker1 = IdentityModel.DefaultUsernameMailBlocker();
        Identity.ImportUsernameMailBlocker(blocker1);
        Boolean result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), blocker1.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), RandomHelper.randomEmail());
        assert result == true;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), blocker1.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert result == false;

        Identity.ImportUsernameMailBlocker(blocker1);
        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), blocker1.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), RandomHelper.randomEmail());
        assert result == true;

        UsernameMailBlocker blocker2 = IdentityModel.DefaultUsernameMailBlocker();
        Identity.ImportUsernameMailBlocker(blocker2);

        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), blocker1.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), RandomHelper.randomEmail());
        assert result == true;

        result = Identity.GetUsernameEmailBlocker(blocker2.getUsername(), blocker2.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker2.getUsername(), RandomHelper.randomEmail());
        assert result == true;
    }

    @Test(groups = "dailies")
    // Import username1/email1; check the username1 & email1 can be used;
    // Import username1/email2; check exception is throw;
    // Import username2/email1; check exception is throw;
    // import username2/email2; check the username2 & email2 can be used;
    public void postMigrationNegativeCases() throws Exception {
        UsernameMailBlocker blocker1 = IdentityModel.DefaultUsernameMailBlocker();
        Identity.ImportUsernameMailBlocker(blocker1);
        Boolean result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), blocker1.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), RandomHelper.randomEmail());
        assert result == true;

        String errorMessage = "username and email do not match";
        String field = "username";
        com.junbo.common.error.Error error = Identity.ImportUsernameMailBlockerError(blocker1.getUsername(), RandomHelper.randomEmail());
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        error = Identity.ImportUsernameMailBlockerError(blocker1.getUsername().toUpperCase(), RandomHelper.randomEmail());
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        error = Identity.ImportUsernameMailBlockerError(blocker1.getUsername().toLowerCase(), RandomHelper.randomEmail());
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        error = Identity.ImportUsernameMailBlockerError(RandomHelper.randomAlphabetic(15), blocker1.getEmail());
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        error = Identity.ImportUsernameMailBlockerError(RandomHelper.randomAlphabetic(15), blocker1.getEmail().toLowerCase());
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        error = Identity.ImportUsernameMailBlockerError(RandomHelper.randomAlphabetic(15), blocker1.getEmail().toUpperCase());
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        UsernameMailBlocker blocker2 = IdentityModel.DefaultUsernameMailBlocker();
        Identity.ImportUsernameMailBlocker(blocker2);

        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), blocker1.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker1.getUsername(), RandomHelper.randomEmail());
        assert result == true;

        result = Identity.GetUsernameEmailBlocker(blocker2.getUsername(), blocker2.getEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert result == false;
        result = Identity.GetUsernameEmailBlocker(blocker2.getUsername(), RandomHelper.randomEmail());
        assert result == true;
    }
}
