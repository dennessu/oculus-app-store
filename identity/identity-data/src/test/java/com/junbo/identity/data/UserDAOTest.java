/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data;

import com.junbo.common.id.*;
import com.junbo.identity.data.dao.GroupDAO;
import com.junbo.identity.data.dao.SecurityQuestionDAO;
import com.junbo.identity.data.dao.UserPINDAO;
import com.junbo.identity.data.dao.UserPasswordDAO;
import com.junbo.identity.data.entity.user.UserPasswordStrength;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.options.GroupGetOption;
import com.junbo.identity.spec.model.users.Group;
import com.junbo.identity.spec.model.users.UserPIN;
import com.junbo.identity.spec.model.users.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Unittest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class UserDAOTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private UserPasswordDAO userPasswordDAO;

    @Autowired
    private UserPINDAO userPINDAO;

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    private Random rand = new Random();

    @Test(enabled = false)
    public void testGroupEntity() {
        Group group = new Group();
        group.setId(new GroupId(rand.nextLong()));
        group.setValue("test " + UUID.randomUUID().toString());
        group.setActive(true);
        group.setCreatedTime(new Date());
        groupDAO.save(group);

        Group newGroup = groupDAO.get(group.getId());
        Assert.assertEquals(group.getValue(), newGroup.getValue());

        String newValue = "test2 " + UUID.randomUUID().toString();
        newGroup.setValue(newValue);
        groupDAO.update(newGroup);
        newGroup = groupDAO.get(group.getId());
        Assert.assertEquals(newValue, newGroup.getValue());

        GroupGetOption option = new GroupGetOption();
        option.setValue("test");
        List<Group> groupList = groupDAO.search(option);

        Assert.assertNotEquals(groupList.size(), 0);
    }

    @Test(enabled = false)
    public void testUserPasswordDAO() {
        UserPassword userPassword = new UserPassword();
        userPassword.setId(new UserPasswordId(rand.nextLong()));
        userPassword.setUserId(new UserId(rand.nextLong()));
        userPassword.setPasswordSalt(UUID.randomUUID().toString());
        userPassword.setPasswordHash(UUID.randomUUID().toString());
        userPassword.setPasswordStrength(UserPasswordStrength.WEAK.toString());
        userPassword.setActive(true);
        userPassword.setChangeAtNextLogin(false);
        userPassword.setExpiresBy(new Date());
        userPassword.setCreatedTime(new Date());
        userPassword.setUpdatedTime(new Date());
        userPasswordDAO.save(userPassword);

        UserPassword newUserPassword = userPasswordDAO.get(userPassword.getId());
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive());

        Boolean newValue = !userPassword.getActive();
        newUserPassword.setActive(newValue);
        userPasswordDAO.update(newUserPassword);
        newUserPassword = userPasswordDAO.get(newUserPassword.getId());
        Assert.assertEquals(newValue, newUserPassword.getActive());
    }

    @Test(enabled = false)
    public void testUserPinDAO() {
        UserPIN userPIN = new UserPIN();
        userPIN.setId(new UserPINId(rand.nextLong()));
        userPIN.setUserId(new UserId(rand.nextLong()));
        userPIN.setPinHash(UUID.randomUUID().toString());
        userPIN.setPinSalt(UUID.randomUUID().toString());
        userPIN.setActive(true);
        userPIN.setChangeAtNextLogin(false);
        userPIN.setExpiresBy(new Date());
        userPIN.setCreatedTime(new Date());
        userPIN.setUpdatedTime(new Date());
        userPINDAO.save(userPIN);

        UserPIN newUserPIN = userPINDAO.get(userPIN.getId());
        Assert.assertEquals(userPIN.getActive(), newUserPIN.getActive());

        Boolean newValue = !userPIN.getActive();
        newUserPIN.setActive(newValue);
        userPINDAO.update(newUserPIN);
        newUserPIN = userPINDAO.get(newUserPIN.getId());
        Assert.assertEquals(newValue, newUserPIN.getActive());
    }

    @Test(enabled = false)
    public void testSecurityQuestionDAO() {
        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setId(new SecurityQuestionId(rand.nextLong()));
        securityQuestion.setValue("test " + UUID.randomUUID().toString());
        securityQuestion.setCreatedTime(new Date());
        securityQuestion.setUpdatedTime(new Date());
        securityQuestionDAO.save(securityQuestion);

        SecurityQuestion newSecurityQuestion = securityQuestionDAO.get(securityQuestion.getId());
        Assert.assertEquals(securityQuestion.getValue(), newSecurityQuestion.getValue());

        String newValue = "Test2 " + UUID.randomUUID().toString();
        newSecurityQuestion.setValue(newValue);
        securityQuestionDAO.update(newSecurityQuestion);
        newSecurityQuestion = securityQuestionDAO.get(newSecurityQuestion.getId());
        Assert.assertEquals(newValue, newSecurityQuestion.getValue());
    }
}
