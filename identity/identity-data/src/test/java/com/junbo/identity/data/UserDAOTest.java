/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.dao.UserProfileDAO;
import com.junbo.identity.data.entity.user.UserProfileType;
import com.junbo.identity.data.entity.user.UserStatus;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.spec.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * Unittest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public class UserDAOTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    @Resource(name = "userDAO")
    private UserDAO userDAO;

    @Autowired
    @Resource(name = "userProfileDAO")
    private UserProfileDAO userProfileDAO;

    private Long userId;

    @BeforeMethod
    public void preparedUserKey() {
        User user = new User();
        user.setCreatedTime(new Date());
        user.setStatus(UserStatus.ACTIVE.toString());
        user.setUpdatedTime(new Date());
        user.setUserName(UUID.randomUUID().toString() + "xxxxx@xxx.com");
        user.setPassword("password");
        user = userDAO.saveUser(user);

        userId = user.getId().getValue();
    }

    @Test
    public void testUserDAO() {
        User user = new User();
        user.setCreatedTime(new Date());
        user.setStatus(UserStatus.ACTIVE.toString());
        user.setUpdatedTime(new Date());
        user.setUserName(UUID.randomUUID().toString() + "xxxxx@xxx.com");
        user.setPassword("password");
        user = userDAO.saveUser(user);

        User newUser = userDAO.getUser(user.getId().getValue());

        Assert.assertEquals(user.getUserName(), newUser.getUserName());
    }


    @Test
    public void testUserProfileDAO() {
        UserProfile entity = new UserProfile();
        entity.setType(UserProfileType.PAYIN.toString());
        entity.setDateOfBirth(new Date());
        entity.setFirstName("liangfu");
        entity.setLastName("xia");
        entity.setMiddleName("");
        entity.setLocale("en_US");
        entity.setRegion("US");
        entity.setCreatedTime(new Date());
        entity.setUpdatedTime(new Date());
        entity.setUserId(new UserId(userId));
        entity = userProfileDAO.save(entity);

        UserProfile newEntity = userProfileDAO.get(entity.getId().getValue());
        Assert.assertEquals(entity.getFirstName(), newEntity.getFirstName());
    }
}
