/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserAttribute;
import com.junbo.identity.spec.v1.model.UserAttributeDefinition;
import com.junbo.test.common.HttpclientHelper;
import org.apache.commons.lang.time.DateUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by xiali_000 on 2014/12/19.
 */
public class PostUserAttribute {

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
    public void postUserAttribute() throws Exception {
        Results<UserAttributeDefinition> results = Identity.UserAttributeDefinitionSearch(null, null);
        for(UserAttributeDefinition userAttributeDefinition : results.getItems()) {
            Identity.UserAttributeDefinitionDelete(userAttributeDefinition.getId());

            Results<UserAttribute> userAttributeResults = Identity.UserAttributeSearch(null, userAttributeDefinition.getId(), null, null);
            for(UserAttribute userAttribute : userAttributeResults.getItems()) {
                Identity.UserAttributeDelete(userAttribute.getId());
            }
        }
        User user = Identity.UserPostDefault();
        UserAttributeDefinition userAttributeDefinition = IdentityModel.DefaultUserAttributeDefinition();
        userAttributeDefinition = Identity.UserAttributeDefinitionPost(userAttributeDefinition);

        UserAttribute userAttribute = IdentityModel.DefaultUserAttribute(user.getId(), userAttributeDefinition.getId());
        UserAttribute created = Identity.UserAttributePost(userAttribute);
        assert created.getUseCount().equals(userAttribute.getUseCount());
        assert created.getIsActive();

        created.setUseCount(created.getUseCount()/2 + 1);
        UserAttribute updated = Identity.UserAttributePut(created);
        assert updated.getUseCount().equals(created.getUseCount());
        assert updated.getIsActive();

        UserAttribute getResource = Identity.UserAttributeGet(created.getId());
        assert getResource.getUseCount().equals(created.getUseCount());
        assert getResource.getIsActive();

        Results<UserAttribute> userAttributeResults = Identity.UserAttributeSearch(user.getId(), null, null, null);
        for (UserAttribute userAttribute1 : userAttributeResults.getItems()) {
            assert userAttribute1.getIsActive();
        }

        Identity.UserAttributeDefinitionDelete(userAttributeDefinition.getId());
    }

    @Test(groups = "dailies")
    public void testUserAttributeIsActive() throws Exception {
        Results<UserAttributeDefinition> results = Identity.UserAttributeDefinitionSearch(null, null);
        for(UserAttributeDefinition userAttributeDefinition : results.getItems()) {
            Identity.UserAttributeDefinitionDelete(userAttributeDefinition.getId());

            Results<UserAttribute> userAttributeResults = Identity.UserAttributeSearch(null, userAttributeDefinition.getId(), null, null);
            for(UserAttribute userAttribute : userAttributeResults.getItems()) {
                Identity.UserAttributeDelete(userAttribute.getId());
            }
        }
        User user = Identity.UserPostDefault();
        UserAttributeDefinition userAttributeDefinition = IdentityModel.DefaultUserAttributeDefinition();
        userAttributeDefinition = Identity.UserAttributeDefinitionPost(userAttributeDefinition);

        UserAttribute userAttribute = IdentityModel.DefaultUserAttribute(user.getId(), userAttributeDefinition.getId());
        UserAttribute created = Identity.UserAttributePost(userAttribute);
        assert created.getUseCount().equals(userAttribute.getUseCount());
        assert created.getIsActive();

        Results<UserAttribute> userAttributeResults = Identity.UserAttributeSearch(user.getId(), null, null, null);
        for(UserAttribute userAttribute1 : userAttributeResults.getItems()) {
            assert userAttribute1.getIsActive();
        }

        created.setUseCount(0);
        UserAttribute updated = Identity.UserAttributePut(created);
        assert !updated.getIsActive();

        userAttributeResults = Identity.UserAttributeSearch(user.getId(), null, null, null);
        for (UserAttribute userAttribute1 : userAttributeResults.getItems()) {
            assert !userAttribute1.getIsActive();
        }

        updated.setUseCount(100);
        updated = Identity.UserAttributePut(updated);
        assert updated.getIsActive();

        userAttributeResults = Identity.UserAttributeSearch(user.getId(), null, null, null);
        for (UserAttribute userAttribute1 : userAttributeResults.getItems()) {
            assert userAttribute1.getIsActive();
        }

        updated.setExpirationTime(DateUtils.addDays(new Date(), -10));
        updated = Identity.UserAttributePut(updated);
        assert !updated.getIsActive();

        userAttributeResults = Identity.UserAttributeSearch(user.getId(), null, null, null);
        for (UserAttribute userAttribute1 : userAttributeResults.getItems()) {
            assert !userAttribute1.getIsActive();
        }

        updated.setExpirationTime(DateUtils.addDays(new Date(), 100));
        updated = Identity.UserAttributePut(updated);
        assert updated.getIsActive();

        userAttributeResults = Identity.UserAttributeSearch(user.getId(), null, null, null);
        for (UserAttribute userAttribute1 : userAttributeResults.getItems()) {
            assert userAttribute1.getIsActive();
        }
    }
}
