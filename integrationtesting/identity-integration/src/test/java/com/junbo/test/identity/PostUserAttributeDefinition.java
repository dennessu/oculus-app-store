/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.model.UserAttributeDefinition;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by xiali_000 on 2014/12/19.
 */
public class PostUserAttributeDefinition {
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
    public void postUserAttributeDefinition() throws Exception {
        Results<UserAttributeDefinition> userAttributeDefinitionResults = Identity.UserAttributeDefinitionSearch(null, null, null, null);
        for(UserAttributeDefinition userAttributeDefinition : userAttributeDefinitionResults.getItems()) {
            Identity.UserAttributeDefinitionDelete(userAttributeDefinition.getId());
        }

        UserAttributeDefinition userAttributeDefinition = IdentityModel.DefaultUserAttributeDefinition();
        Organization organization = IdentityModel.DefaultOrganization();
        Organization posted = Identity.OrganizationPostDefault(organization);
        userAttributeDefinition.setOrganizationId(posted.getId());
        UserAttributeDefinition created = Identity.UserAttributeDefinitionPost(userAttributeDefinition);
        assert userAttributeDefinition.getDescription().equals(created.getDescription());
        assert userAttributeDefinition.getType().equals(created.getType());

        String description = RandomHelper.randomAlphabetic(15);
        created.setDescription(description);
        UserAttributeDefinition updated = Identity.UserAttributeDefinitionPut(created);
        assert userAttributeDefinition.getType().equals(updated.getType());
        assert description.equals(updated.getDescription());

        UserAttributeDefinition getResource = Identity.UserAttributeDefinitionGet(updated.getId());
        assert getResource.getId().equals(updated.getId());
        assert getResource.getType().equals(updated.getType());
        assert getResource.getDescription().equals(updated.getDescription());

        Results<UserAttributeDefinition> results = Identity.UserAttributeDefinitionSearch(null, null, null, null);
        assert results.getItems().size() == 1;

        results = Identity.UserAttributeDefinitionSearch(posted.getId(), null, null, null);
        assert results.getItems().size() == 1;

        results = Identity.UserAttributeDefinitionSearch(posted.getId(), userAttributeDefinition.getType(), null, null);
        assert results.getItems().size() == 1;

        Identity.UserAttributeDefinitionDelete(updated.getId());
        results = Identity.UserAttributeDefinitionSearch(null, null, null, null);
        assert results.getItems().size() == 0;
    }
}
