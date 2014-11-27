/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr;

import com.junbo.csr.spec.def.SearchType;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by weiyu_000 on 11/25/14.
 */
public class CsrSearchTesting extends CsrBaseTestClass {

    @Property(
            priority = Priority.BVT,
            features = "CSR Search",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test search user by user name",
            steps = {
                    "1. Post a new user",
                    "2. Search user by user name"

            }
    )
    @Test
    public void testSearchUserByUserName() throws Exception {
        UserInfo userInfo = UserInfo.getRandomUserInfo();
        String userName = userInfo.getUserName();
        String uid = testDataProvider.createUser(userInfo);
        testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        List<String> uids = testDataProvider.searchUsers(SearchType.USERNAME, userName);

        assert uids.size() == 1;
        ValidationHelper.verifyEqual(uids.get(0), uid, "verify csr search result by user name");

    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Search",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test search user by user id",
            steps = {
                    "1. Post a new user",
                    "2. Search user by user id"

            }
    )
    @Test
    public void testSearchUserNameByUid() throws Exception {
        UserInfo userInfo = UserInfo.getRandomUserInfo();
        String userName = userInfo.getUserName();
        String uid = testDataProvider.createUser(userInfo);
        testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        List<String> uids = testDataProvider.searchUsers(SearchType.USERID, uid);

        assert uids.size() == 1;
        ValidationHelper.verifyEqual(Master.getInstance().getUser(uid).getNickName(), userInfo.getUserName(), "verify search result by uid");
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Search",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test search user by email",
            steps = {
                    "1. Post a new user",
                    "2. Search user by user id"

            }
    )
    @Test
    public void testSearchUserNameByEmail() throws Exception {
        UserInfo userInfo = UserInfo.getRandomUserInfo();
        String userName = userInfo.getUserName();
        String uid = testDataProvider.createUser(userInfo);
        testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        List<String> uids = testDataProvider.searchUsers(SearchType.EMAIL, userInfo.getEmails().get(0));

        assert uids.size() == 1;
        ValidationHelper.verifyEqual(Master.getInstance().getUser(uid).getNickName(), userInfo.getUserName(), "verify search result by uid");
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Search",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test search user by fullname",
            steps = {
                    "1. Post a new user",
                    "2. Search user by user id"

            }
    )
    @Test
    public void testSearchUserNameByFullName() throws Exception {
        UserInfo userInfo = UserInfo.getRandomUserInfo();
        String userName = userInfo.getUserName();
        String uid = testDataProvider.createUser(userInfo);
        testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        List<String> uids = testDataProvider.searchUsers(SearchType.FULLNAME, userInfo.getFirstName() + " " + userInfo.getLastName());

        assert uids.size() == 1;
        ValidationHelper.verifyEqual(Master.getInstance().getUser(uid).getNickName(), userInfo.getUserName(), "verify search result by uid");
    }

}
