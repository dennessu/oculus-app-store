/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data;

import com.junbo.identity.data.entity.user.UserPasswordStrength;
import com.junbo.identity.data.repository.*;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.options.UserGetOption;
import com.junbo.identity.spec.model.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Unittest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class RepositoryTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserPasswordRepository userPasswordRepository;

    @Autowired
    private UserPinRepository userPINRepository;

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @Test(enabled = false)
    public void testUserRepository() {
        User user = new User();
        user.setActive(true);
        user.setBirthday("1998-08-01");
        user.setDisplayName(UUID.randomUUID().toString());
        user.setGender("male");
        user.setLocale("en_US");
        UserName userName = new UserName();
        userName.setFirstName(UUID.randomUUID().toString());
        userName.setHonorificPrefix(UUID.randomUUID().toString());
        userName.setHonorificSuffix(UUID.randomUUID().toString());
        userName.setLastName(UUID.randomUUID().toString());
        userName.setMiddleName(UUID.randomUUID().toString());
        user.setName(userName);
        user.setNickName(UUID.randomUUID().toString());
        user.setPreferredLanguage(UUID.randomUUID().toString());
        user.setTimezone(UUID.randomUUID().toString());
        user.setType(UUID.randomUUID().toString());
        user.setUserName(UUID.randomUUID().toString());
        user.setCreatedTime(new Date());
        user.setCreatedBy("lixia");
        user = userRepository.save(user);

        User newUser = userRepository.get(user.getId());
        Assert.assertEquals(user.getBirthday(), newUser.getBirthday());

        String oldValue = user.getBirthday();
        String newValue = UUID.randomUUID().toString();
        newUser.setBirthday(newValue);
        newUser = userRepository.update(newUser);
        Assert.assertEquals(newUser.getBirthday(), newValue);

        UserGetOption getOption = new UserGetOption();
        getOption.setUserName(newUser.getUserName());
        List<User> userList = userRepository.search(getOption);
        Assert.assertNotEquals(userList.size(), 0);
    }

    @Test(enabled = false)
    public void testGroupEntity() {
        Group group = new Group();
        group.setValue("test " + UUID.randomUUID().toString());
        group.setActive(true);
        group.setCreatedTime(new Date());
        group = groupRepository.save(group);

        Group newGroup = groupRepository.get(group.getId());
        Assert.assertEquals(group.getValue(), newGroup.getValue());

        String newValue = "test2 " + UUID.randomUUID().toString();
        newGroup.setValue(newValue);
        groupRepository.update(newGroup);
        newGroup = groupRepository.get(group.getId());
        Assert.assertEquals(newValue, newGroup.getValue());

        // Todo:    Need to enable this.
        //GroupGetOption option = new GroupGetOption();
        //option.setValue("test");
        //List<Group> groupList = groupRepository.search(option);

        //Assert.assertNotEquals(groupList.size(), 0);
    }

    @Test(enabled = false)
    public void testUserPasswordDAO() {
        UserPassword userPassword = new UserPassword();
        // userPassword.setUserId(new UserId(rand.nextLong()));
        userPassword.setPasswordSalt(UUID.randomUUID().toString());
        userPassword.setPasswordHash(UUID.randomUUID().toString());
        userPassword.setPasswordStrength(UserPasswordStrength.WEAK.toString());
        userPassword.setActive(true);
        userPassword.setChangeAtNextLogin(false);
        userPassword.setExpiresBy(new Date());
        userPassword.setCreatedTime(new Date());
        userPassword.setUpdatedTime(new Date());
        userPasswordRepository.save(userPassword);

        UserPassword newUserPassword = userPasswordRepository.get(userPassword.getId());
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive());

        Boolean newValue = !userPassword.getActive();
        newUserPassword.setActive(newValue);
        userPasswordRepository.update(newUserPassword);
        newUserPassword = userPasswordRepository.get(newUserPassword.getId());
        Assert.assertEquals(newValue, newUserPassword.getActive());
    }

    @Test(enabled = false)
    public void testUserPinDAO() {
        UserPin userPIN = new UserPin();
        // userPIN.setUserId(new UserId(rand.nextLong()));
        userPIN.setPinHash(UUID.randomUUID().toString());
        userPIN.setPinSalt(UUID.randomUUID().toString());
        userPIN.setActive(true);
        userPIN.setChangeAtNextLogin(false);
        userPIN.setExpiresBy(new Date());
        userPIN.setCreatedTime(new Date());
        userPIN.setUpdatedTime(new Date());
        userPINRepository.save(userPIN);

        UserPin newUserPIN = userPINRepository.get(userPIN.getId());
        Assert.assertEquals(userPIN.getActive(), newUserPIN.getActive());

        Boolean newValue = !userPIN.getActive();
        newUserPIN.setActive(newValue);
        userPINRepository.update(newUserPIN);
        newUserPIN = userPINRepository.get(newUserPIN.getId());
        Assert.assertEquals(newValue, newUserPIN.getActive());
    }

    @Test(enabled = false)
    public void testSecurityQuestionDAO() {
        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setValue("test " + UUID.randomUUID().toString());
        securityQuestion.setCreatedTime(new Date());
        securityQuestion.setUpdatedTime(new Date());
        securityQuestionRepository.save(securityQuestion);

        SecurityQuestion newSecurityQuestion = securityQuestionRepository.get(securityQuestion.getId());
        Assert.assertEquals(securityQuestion.getValue(), newSecurityQuestion.getValue());

        String newValue = "Test2 " + UUID.randomUUID().toString();
        newSecurityQuestion.setValue(newValue);
        securityQuestionRepository.update(newSecurityQuestion);
        newSecurityQuestion = securityQuestionRepository.get(newSecurityQuestion.getId());
        Assert.assertEquals(newValue, newSecurityQuestion.getValue());
    }
}
