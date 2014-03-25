/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.id.UserId;
import com.junbo.identity.data.identifiable.UserPasswordStrength;
import com.junbo.identity.data.repository.*;
import com.junbo.identity.spec.model.users.*;
import com.junbo.identity.spec.options.list.*;
import org.glassfish.jersey.internal.util.Base64;
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
    // This is the fake value to meet current requirement.
    private final long userId = 1493188608L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserPinRepository userPinRepository;

    @Autowired
    private UserAuthenticatorRepository userAuthenticatorRepository;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private UserEmailRepository userEmailRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserLoginAttemptRepository userLoginAttemptRepository;

    @Autowired
    private UserOptinRepository userOptinRepository;

    @Autowired
    private UserPhoneNumberRepository userPhoneNumberRepository;

    @Autowired
    private UserPasswordRepository userPasswordRepository;

    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository;

    @Autowired
    private UserTosRepository userTosRepository;

    @Autowired
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository;

    @Test(enabled = true)
    public void testUserRepository() {
        User user = new User();
        user.setActive(true);
        user.setBirthday(new Date());
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

        Date newValue = new Date();
        newUser.setBirthday(newValue);
        newUser = userRepository.update(newUser);
        Assert.assertEquals(newUser.getBirthday(), newValue);

        UserListOption getOption = new UserListOption();
        getOption.setUserName(newUser.getUserName());
        List<User> userList = userRepository.search(getOption);
        Assert.assertNotEquals(userList.size(), 0);
    }

    @Test(enabled = true)
    public void testGroupRepository() {
        Group group = new Group();
        group.setName("test " + UUID.randomUUID().toString());
        group.setActive(true);
        group.setCreatedTime(new Date());
        group = groupRepository.save(group);

        Group newGroup = groupRepository.get(group.getId());
        Assert.assertEquals(group.getName(), newGroup.getName());

        String newValue = "test2 " + UUID.randomUUID().toString();
        newGroup.setName(newValue);
        groupRepository.update(newGroup);
        newGroup = groupRepository.get(group.getId());
        Assert.assertEquals(newValue, newGroup.getName());

        List<Group> groupList = groupRepository.searchByName(newValue);

        Assert.assertEquals(groupList.size(), 1);
    }

    @Test(enabled = true)
    public void testUserPasswordRepository() {
        UserPassword userPassword = new UserPassword();
        userPassword.setUserId(new UserId(userId));
        userPassword.setPasswordSalt(UUID.randomUUID().toString());
        userPassword.setPasswordHash(UUID.randomUUID().toString());
        userPassword.setStrength(UserPasswordStrength.WEAK.toString());
        userPassword.setActive(true);
        userPassword.setChangeAtNextLogin(false);
        userPassword.setExpiresBy(new Date());
        userPassword.setCreatedTime(new Date());
        userPassword.setCreatedBy("lixia");
        userPassword.setUpdatedTime(new Date());
        userPassword.setUpdatedBy("lixia");
        userPassword = userPasswordRepository.save(userPassword);

        UserPassword newUserPassword = userPasswordRepository.get(userPassword.getId());
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive());

        Boolean newValue = !userPassword.getActive();
        newUserPassword.setActive(newValue);
        userPasswordRepository.update(newUserPassword);
        newUserPassword = userPasswordRepository.get(newUserPassword.getId());
        Assert.assertEquals(newValue, newUserPassword.getActive());

        UserPasswordListOption getOption = new UserPasswordListOption();
        getOption.setUserId(new UserId(userId));
        List<UserPassword> userPasswordList = userPasswordRepository.search(getOption);
        Assert.assertNotEquals(userPasswordList.size(), 0);
    }

    @Test(enabled = true)
    public void testUserPinRepository() {
        UserPin userPIN = new UserPin();
        userPIN.setUserId(new UserId(userId));
        userPIN.setPinHash(UUID.randomUUID().toString());
        userPIN.setPinSalt(UUID.randomUUID().toString());
        userPIN.setActive(true);
        userPIN.setChangeAtNextLogin(false);
        userPIN.setExpiresBy(new Date());
        userPIN.setCreatedTime(new Date());
        userPIN.setCreatedBy("lixia");
        userPIN.setUpdatedTime(new Date());
        userPIN.setUpdatedBy("lixia");
        userPIN = userPinRepository.save(userPIN);

        UserPin newUserPin = userPinRepository.get(userPIN.getId());
        Assert.assertEquals(userPIN.getActive(), newUserPin.getActive());

        Boolean newValue = !userPIN.getActive();
        newUserPin.setActive(newValue);
        userPinRepository.update(newUserPin);
        newUserPin = userPinRepository.get(newUserPin.getId());
        Assert.assertEquals(newValue, newUserPin.getActive());

        UserPinListOption getOption = new UserPinListOption();
        getOption.setUserId(new UserId(userId));
        List<UserPin> userPins = userPinRepository.search(getOption);
        Assert.assertNotEquals(userPins.size(), 0);
    }

    @Test(enabled = true)
    public void testUserAuthenticatorRepository() {
        UserAuthenticator authenticator = new UserAuthenticator();
        authenticator.setUserId(new UserId(userId));
        authenticator.setType("Google_account");
        authenticator.setValue(UUID.randomUUID().toString());
        authenticator.setCreatedTime(new Date());
        authenticator.setCreatedBy("lixia");
        authenticator = userAuthenticatorRepository.save(authenticator);

        UserAuthenticator newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId());
        Assert.assertEquals(authenticator.getValue(), newUserAuthenticator.getValue());

        String newValue = UUID.randomUUID().toString();
        newUserAuthenticator.setValue(newValue);
        userAuthenticatorRepository.update(newUserAuthenticator);
        newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId());

        Assert.assertEquals(newValue, newUserAuthenticator.getValue());

        UserAuthenticatorListOption getOption = new UserAuthenticatorListOption();
        getOption.setValue(newValue);
        List<UserAuthenticator> userAuthenticators = userAuthenticatorRepository.search(getOption);
        Assert.assertNotEquals(userAuthenticators.size(), 0);
    }

    @Test(enabled = true)
    public void testUserDeviceRepository() {
        UserDevice userDevice = new UserDevice();
        userDevice.setType("Oculus");
        userDevice.setDeviceId(UUID.randomUUID().toString());
        userDevice.setName(UUID.randomUUID().toString());
        userDevice.setOs(UUID.randomUUID().toString());
        userDevice.setUserId(new UserId(userId));
        userDevice.setCreatedBy("lixia");
        userDevice.setCreatedTime(new Date());

        userDevice = userDeviceRepository.save(userDevice);

        UserDevice newUserDevice = userDeviceRepository.get(userDevice.getId());
        Assert.assertEquals(userDevice.getName(), newUserDevice.getName());

        String newName = UUID.randomUUID().toString();
        newUserDevice.setName(newName);
        userDeviceRepository.update(newUserDevice);

        newUserDevice = userDeviceRepository.get(userDevice.getId());
        Assert.assertEquals(newName, newUserDevice.getName());

        UserDeviceListOption getOption = new UserDeviceListOption();
        getOption.setUserId(new UserId(userId));
        List<UserDevice> userDevices = userDeviceRepository.search(getOption);
        Assert.assertNotEquals(userDevices.size(), 0);
    }

    @Test(enabled = true)
    public void testUserEmailRepository() {
        UserEmail userEmail = new UserEmail();
        userEmail.setUserId(new UserId(userId));
        userEmail.setType("Google");
        userEmail.setValue(UUID.randomUUID().toString());
        userEmail.setPrimary(true);
        userEmail.setVerified(true);
        userEmail.setCreatedBy("lixia");
        userEmail.setCreatedTime(new Date());
        userEmail = userEmailRepository.save(userEmail);

        UserEmail newUserEmail = userEmailRepository.get(userEmail.getId());
        Assert.assertEquals(userEmail.getValue(), newUserEmail.getValue());

        String value = UUID.randomUUID().toString();
        newUserEmail.setValue(value);
        userEmailRepository.update(newUserEmail);

        newUserEmail = userEmailRepository.get(userEmail.getId());
        Assert.assertEquals(newUserEmail.getValue(), value);

        UserEmailListOption getOption = new UserEmailListOption();
        getOption.setValue(value);
        List<UserEmail> userEmails = userEmailRepository.search(getOption);
        Assert.assertEquals(userEmails.size(), 1);
    }

    @Test(enabled = true)
    public void testUserGroupRepository() {
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(new UserId(userId));
        userGroup.setGroupId(new GroupId(1493188608L));
        userGroup.setCreatedBy("lixia");
        userGroup.setCreatedTime(new Date());
        userGroup = userGroupRepository.save(userGroup);

        UserGroup newUserGroup = userGroupRepository.get(userGroup.getId());
        Assert.assertEquals(userGroup.getGroupId().getValue(), newUserGroup.getGroupId().getValue());

        UserGroupListOption getOption = new UserGroupListOption();
        getOption.setUserId(new UserId(userId));
        getOption.setGroupId(new GroupId(1493188608L));
        List<UserGroup> userGroups = userGroupRepository.search(getOption);

        Assert.assertNotEquals(userGroups.size(), 0);
    }

    @Test(enabled = true)
    public void testUserLoginAttemptRepository() {
        UserLoginAttempt userLoginAttempt = new UserLoginAttempt();
        userLoginAttempt.setUserId(new UserId(userId));
        userLoginAttempt.setType("pin");
        userLoginAttempt.setValue(UUID.randomUUID().toString());
        userLoginAttempt.setClientId(UUID.randomUUID().toString());
        userLoginAttempt.setIpAddress(UUID.randomUUID().toString());
        userLoginAttempt.setUserAgent(UUID.randomUUID().toString());
        userLoginAttempt.setSucceeded(true);
        userLoginAttempt.setCreatedBy("lixia");
        userLoginAttempt.setCreatedTime(new Date());

        userLoginAttempt = userLoginAttemptRepository.save(userLoginAttempt);

        UserLoginAttempt newUserLoginAttempt = userLoginAttemptRepository.get(userLoginAttempt.getId());
        Assert.assertEquals(userLoginAttempt.getIpAddress(), newUserLoginAttempt.getIpAddress());

        String value = UUID.randomUUID().toString();
        newUserLoginAttempt.setIpAddress(value);
        userLoginAttemptRepository.update(newUserLoginAttempt);

        newUserLoginAttempt = userLoginAttemptRepository.get(userLoginAttempt.getId());
        Assert.assertEquals(newUserLoginAttempt.getIpAddress(), value);

        LoginAttemptListOption getOption = new LoginAttemptListOption();
        getOption.setUserId(new UserId(userId));
        getOption.setIpAddress(value);
        List<UserLoginAttempt> userLoginAttempts = userLoginAttemptRepository.search(getOption);
        Assert.assertEquals(userLoginAttempts.size(), 1);
    }

    @Test(enabled = true)
    public void testUserOptinRepository() {
        UserOptin userOptin = new UserOptin();
        userOptin.setUserId(new UserId(userId));
        userOptin.setType(UUID.randomUUID().toString());
        userOptin.setCreatedBy("lixia");
        userOptin.setCreatedTime(new Date());
        userOptin = userOptinRepository.save(userOptin);

        UserOptin newUserOptin = userOptinRepository.get(userOptin.getId());
        Assert.assertEquals(userOptin.getType(), newUserOptin.getType());

        String value = UUID.randomUUID().toString();
        userOptin.setType(value);
        userOptinRepository.update(userOptin);

        newUserOptin = userOptinRepository.get(userOptin.getId());
        Assert.assertEquals(value, newUserOptin.getType());

        UserOptinListOption getOption = new UserOptinListOption();
        getOption.setType(value);
        getOption.setUserId(new UserId(userId));
        List<UserOptin> userOptins = userOptinRepository.search(getOption);
        Assert.assertEquals(userOptins.size(), 1);
    }

    @Test(enabled = true)
    public void testUserPhoneNumberRepository() {
        UserPhoneNumber userPhoneNumber = new UserPhoneNumber();
        userPhoneNumber.setUserId(new UserId(userId));
        userPhoneNumber.setValue(UUID.randomUUID().toString());
        userPhoneNumber.setType("Google");
        userPhoneNumber.setPrimary(true);
        userPhoneNumber.setVerified(true);
        userPhoneNumber.setCreatedTime(new Date());
        userPhoneNumber.setCreatedBy("lixia");
        userPhoneNumber = userPhoneNumberRepository.save(userPhoneNumber);

        UserPhoneNumber newUserPhoneNumber = userPhoneNumberRepository.get(userPhoneNumber.getId());
        Assert.assertEquals(userPhoneNumber.getValue(), newUserPhoneNumber.getValue());

        String value = UUID.randomUUID().toString();
        newUserPhoneNumber.setValue(value);
        userPhoneNumberRepository.update(newUserPhoneNumber);

        newUserPhoneNumber = userPhoneNumberRepository.get(userPhoneNumber.getId());
        Assert.assertEquals(value, newUserPhoneNumber.getValue());

        UserPhoneNumberListOption getOption = new UserPhoneNumberListOption();
        getOption.setUserId(new UserId(userId));
        getOption.setValue(value);
        List<UserPhoneNumber> userPhoneNumbers = userPhoneNumberRepository.search(getOption);
        Assert.assertEquals(userPhoneNumbers.size(), 1);
    }

    @Test(enabled = true)
    public void testUserSecurityQuestionRepository() {
        UserSecurityQuestion userSecurityQuestion = new UserSecurityQuestion();
        userSecurityQuestion.setUserId(new UserId(userId));
        userSecurityQuestion.setSecurityQuestionId(new SecurityQuestionId(123L));
        userSecurityQuestion.setAnswerHash(UUID.randomUUID().toString());
        userSecurityQuestion.setAnswerSalt(UUID.randomUUID().toString());
        userSecurityQuestion.setCreatedBy("lixia");
        userSecurityQuestion.setCreatedTime(new Date());

        userSecurityQuestion = userSecurityQuestionRepository.save(userSecurityQuestion);

        UserSecurityQuestion newUserSecurityQuestion = userSecurityQuestionRepository.get(userSecurityQuestion.getId());
        Assert.assertEquals(userSecurityQuestion.getAnswerHash(), newUserSecurityQuestion.getAnswerHash());

        String value = UUID.randomUUID().toString();
        newUserSecurityQuestion.setAnswerSalt(value);
        userSecurityQuestionRepository.update(newUserSecurityQuestion);

        newUserSecurityQuestion = userSecurityQuestionRepository.get(userSecurityQuestion.getId());
        Assert.assertEquals(newUserSecurityQuestion.getAnswerSalt(), value);

        UserSecurityQuestionListOption getOption = new UserSecurityQuestionListOption();
        getOption.setUserId(new UserId(userId));
        getOption.setSecurityQuestionId(new SecurityQuestionId(123L));
        List<UserSecurityQuestion> securityQuestions = userSecurityQuestionRepository.search(getOption);
        Assert.assertNotEquals(securityQuestions.size(), 0);
    }

    @Test(enabled = true)
    public void testUserTosRepository() {
        UserTos userTos = new UserTos();
        userTos.setUserId(new UserId(userId));
        userTos.setTosUri(UUID.randomUUID().toString());
        userTos.setCreatedBy("lixia");
        userTos.setCreatedTime(new Date());
        userTos = userTosRepository.save(userTos);

        UserTos newUserTos = userTosRepository.get(userTos.getId());
        Assert.assertEquals(userTos.getTosUri(), newUserTos.getTosUri());

        String value = UUID.randomUUID().toString();
        newUserTos.setTosUri(value);
        userTosRepository.update(newUserTos);

        newUserTos = userTosRepository.get(userTos.getId());
        Assert.assertEquals(value, newUserTos.getTosUri());

        UserTosListOption userTosGetOption = new UserTosListOption();
        userTosGetOption.setUserId(new UserId(userId));
        userTosGetOption.setTosUri(value);
        List<UserTos> userToses = userTosRepository.search(userTosGetOption);
        Assert.assertEquals(userToses.size(), 1);
    }

    @Test(enabled = true)
    public void testUserSecurityQuestionAttempt() {
        UserSecurityQuestionAttempt attempt = new UserSecurityQuestionAttempt();
        attempt.setUserId(new UserId(userId));
        attempt.setSucceeded(true);
        attempt.setValue(UUID.randomUUID().toString());
        attempt.setClientId(UUID.randomUUID().toString());
        attempt.setIpAddress(UUID.randomUUID().toString());
        attempt.setSecurityQuestionId(new SecurityQuestionId(123L));
        attempt.setUserAgent(UUID.randomUUID().toString());

        attempt = userSecurityQuestionAttemptRepository.save(attempt);

        UserSecurityQuestionAttempt newAttempt = userSecurityQuestionAttemptRepository.get(attempt.getId());
        Assert.assertEquals(attempt.getIpAddress(), newAttempt.getIpAddress());

        String value = UUID.randomUUID().toString();
        newAttempt.setIpAddress(value);
        userSecurityQuestionAttemptRepository.update(newAttempt);

        newAttempt = userSecurityQuestionAttemptRepository.get(attempt.getId());
        Assert.assertEquals(newAttempt.getIpAddress(), value);

        UserSecurityQuestionAttemptListOption option = new UserSecurityQuestionAttemptListOption();
        option.setUserId(new UserId(userId));
        option.setSecurityQuestionId(new SecurityQuestionId(123L));
        List<UserSecurityQuestionAttempt> attempts = userSecurityQuestionAttemptRepository.search(option);
        Assert.assertNotEquals(attempts.size(), 0);
    }

    @Test
    public void test() {
        String userName = "liangfuxia";
        String password = "#Bugsfor$";

        String original = userName + ":" + password;
        String encode = Base64.encodeAsString(original);
        String decode = Base64.decodeAsString(encode);

        String[] split = decode.split(":");
        Assert.assertEquals(userName, split[0]);
        Assert.assertEquals(password, split[1]);
    }
}
