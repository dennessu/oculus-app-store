/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserGroup;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import scala.reflect.internal.Trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 10/9/14.
 */
public class postUserGroup {

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
    //https://oculus.atlassian.net/browse/SER-657
    public void postUserGroup() throws Exception {
        User user = Identity.UserPostDefault();
        Organization org = IdentityModel.DefaultOrganization();
        Organization posted = Identity.OrganizationPostDefault(org);
        Group group1 = Identity.GroupPostDefault(IdentityModel.DefaultGroup(posted.getId()));
        Group group2 = Identity.GroupPostDefault(IdentityModel.DefaultGroup(posted.getId()));

        UserGroup userGroup = Identity.UserGroupPost(user.getId(), group1.getId());
        assert userGroup.getGroupId().getValue().equalsIgnoreCase(group1.getId().getValue());
        userGroup.setGroupId(group2.getId());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));

        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserGroupMemberURI + "/" + userGroup.getId().getValue(), JsonHelper.JsonSerializer(userGroup),
                HttpclientHelper.HttpRequestType.put, nvps);

        String errorMessage = "Field is not writable.";
        Validator.Validate("validate response error code", 409, response.getStatusLine().getStatusCode());
        EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage);
        response.close();

        userGroup = Identity.UserGroupPost(user.getId(), group2.getId());
        assert userGroup.getGroupId().getValue().equalsIgnoreCase(group2.getId().getValue());
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-686
    public void getDeleteGroup() throws Exception {
        User user = Identity.UserPostDefault();
        Organization org = IdentityModel.DefaultOrganization();
        Organization posted = Identity.OrganizationPostDefault(org);
        Group group1 = Identity.GroupPostDefault(IdentityModel.DefaultGroup(posted.getId()));
        Group group2 = Identity.GroupPostDefault(IdentityModel.DefaultGroup(posted.getId()));

        Identity.UserGroupPost(user.getId(), group1.getId());
        Identity.UserGroupPost(user.getId(), group2.getId());

        Results<UserGroup> userGroups = Identity.UserGroupSearch(null, user.getId());
        assert userGroups != null;
        assert userGroups.getTotal() == 2;
        assert userGroups.getItems().size() == 2;

        userGroups = Identity.UserGroupSearch(group1.getId(), null);
        assert userGroups != null;
        assert userGroups.getTotal() == 1;
        assert userGroups.getItems().size() == 1;

        userGroups = Identity.UserGroupSearch(group2.getId(), null);
        assert userGroups != null;
        assert userGroups.getTotal() == 1;
        assert userGroups.getItems().size() == 1;

        userGroups = Identity.UserGroupSearch(group1.getId(), user.getId());
        assert userGroups != null;
        assert userGroups.getTotal() == 1;
        assert userGroups.getItems().size() == 1;

        Identity.GroupDelete(group1);
        userGroups = Identity.UserGroupSearch(group1.getId(), null);
        assert userGroups != null;
        assert userGroups.getTotal() == 0;
        assert userGroups.getItems().size() == 0;

        userGroups = Identity.UserGroupSearch(null, user.getId());
        assert userGroups != null;
        assert userGroups.getTotal() == 1;
        assert userGroups.getItems().size() == 1;

        Identity.GroupDelete(group2);
        userGroups = Identity.UserGroupSearch(group2.getId(), null);
        assert userGroups != null;
        assert userGroups.getTotal() == 0;
        assert userGroups.getItems().size() == 0;

        userGroups = Identity.UserGroupSearch(null, user.getId());
        assert userGroups != null;
        assert userGroups.getTotal() == 0;
        assert userGroups.getItems().size() == 0;
    }

    @Test(groups = "dailies")
    public void testUserGroupSearch() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        Organization posted = Identity.OrganizationPostDefault(org);
        Group group1 = Identity.GroupPostDefault(IdentityModel.DefaultGroup(posted.getId()));
        Group group2 = Identity.GroupPostDefault(IdentityModel.DefaultGroup(posted.getId()));

        List<UserId> userIdList = new ArrayList<>();

        for (int i =0; i<10; i++) {
            User user = Identity.UserPostDefault();
            userIdList.add(user.getId());
            Identity.UserGroupPost(user.getId(), group1.getId());
            Identity.UserGroupPost(user.getId(), group2.getId());
        }

        User defaultUser = Identity.UserPostDefault();

        // validate groupId
        Results<UserGroup> userGroupResults = Identity.UserGroupSearch(group1.getId(), null, null);
        assert userGroupResults != null;
        assert userGroupResults.getTotal() == 10;
        assert userGroupResults.getItems().size() == 10;

        userGroupResults = Identity.UserGroupSearch(group1.getId(), null, 5);
        assert userGroupResults != null;
        assert userGroupResults.getTotal() == 10;
        assert userGroupResults.getItems().size() == 5;

        userGroupResults = Identity.UserGroupSearch(group1.getId(), null, 0);
        assert userGroupResults != null;
        assert userGroupResults.getTotal() == 10;
        assert userGroupResults.getItems().size() == 0;

        // validate userId
        for (UserId userId : userIdList) {
            userGroupResults = Identity.UserGroupSearch(null, userId, null);
            assert userGroupResults != null;
            assert userGroupResults.getTotal() == 2;
            assert userGroupResults.getItems().size() == 2;

            userGroupResults = Identity.UserGroupSearch(null, userId, 10);
            assert userGroupResults != null;
            assert userGroupResults.getTotal() == 2;
            assert userGroupResults.getItems().size() == 2;

            userGroupResults = Identity.UserGroupSearch(null, userId, 0);
            assert userGroupResults != null;
            assert userGroupResults.getTotal() == 2;
            assert userGroupResults.getItems().size() == 0;
        }

        // validate userId and groupId
        for (UserId userId : userIdList) {
            userGroupResults = Identity.UserGroupSearch(group1.getId(), userId, null);
            assert userGroupResults != null;
            assert userGroupResults.getTotal() == 1;
            assert userGroupResults.getItems().size() == 1;

            userGroupResults = Identity.UserGroupSearch(group1.getId(), userId, 10);
            assert userGroupResults != null;
            assert userGroupResults.getTotal() == 1;
            assert userGroupResults.getItems().size() == 1;

            userGroupResults = Identity.UserGroupSearch(group1.getId(), userId, 0);
            assert userGroupResults != null;
            assert userGroupResults.getTotal() == 1;
            assert userGroupResults.getItems().size() == 0;
        }

        // validate not exist value
        userGroupResults = Identity.UserGroupSearch(null, defaultUser.getId(), null);
        assert userGroupResults != null;
        assert userGroupResults.getTotal() == 0;
        assert userGroupResults.getItems().size() == 0;
    }
}
