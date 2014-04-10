/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data

import com.junbo.common.id.DeviceId
import com.junbo.common.id.GroupId
import com.junbo.common.id.SecurityQuestionId
import com.junbo.common.id.UserDeviceId
import com.junbo.common.id.UserId
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.*
import com.junbo.identity.spec.options.list.*
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional
import org.testng.Assert
import org.testng.annotations.Test
/**
 * Unittest.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional('transactionManager')
@CompileStatic
public class RepositoryTest extends AbstractTestNGSpringContextTests {
    // This is the fake value to meet current requirement.
    private final long userId = 1493188608L

    @Autowired
    private UserRepository userRepository

    @Autowired
    private GroupRepository groupRepository

    @Autowired
    private UserPinRepository userPinRepository

    @Autowired
    private UserAuthenticatorRepository userAuthenticatorRepository

    @Autowired
    private UserDeviceRepository userDeviceRepository

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private UserCredentialVerifyAttemptRepository userLoginAttemptRepository

    @Autowired
    private UserOptinRepository userOptinRepository

    @Autowired
    private UserPasswordRepository userPasswordRepository

    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    @Autowired
    private UserTosRepository userTosRepository

    @Autowired
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository

    @Autowired
    private DeviceRepository deviceRepository

    @Test(enabled = true)
    public void testUserRepository() throws Exception {
        User user = new User()
        user.setActive(true)
        user.setCurrency('USD')
        user.setLocale('en_US')
        user.setNickName(UUID.randomUUID().toString())
        user.setPreferredLanguage(UUID.randomUUID().toString())
        user.setTimezone(UUID.randomUUID().toString())
        user.setType(UUID.randomUUID().toString())
        user.setUsername(UUID.randomUUID().toString())
        user.setCreatedTime(new Date())
        user.setCreatedBy('lixia')
        user = userRepository.create(user).wrapped().get()

        User newUser = userRepository.get(user.getId()).wrapped().get()
        Assert.assertEquals(user.getType(), newUser.getType())

        String newType = UUID.randomUUID().toString()
        newUser.setType(newType)
        newUser = userRepository.update(newUser).wrapped().get()
        Assert.assertEquals(newUser.getType(), newType)

        User findUser = userRepository.getUserByCanonicalUsername(newUser.getUsername()).wrapped().get()
        Assert.assertNotNull(findUser)
    }

    @Test(enabled = true)
    public void testGroupRepository() {
        Group group = new Group()
        group.setName(UUID.randomUUID().toString())
        group.setActive(true)
        group.setCreatedTime(new Date())
        group = groupRepository.create(group).wrapped().get()

        Group newGroup = groupRepository.get(group.getId()).wrapped().get()
        Assert.assertEquals(group.getName(), newGroup.getName())

        String newValue = 'test2 ' + UUID.randomUUID().toString()
        newGroup.setName(newValue)
        groupRepository.update(newGroup)
        newGroup = groupRepository.get(group.getId()).wrapped().get()
        Assert.assertEquals(newValue, newGroup.getName())

        Group groupSearched = groupRepository.searchByName(newValue).wrapped().get()

        Assert.assertNotNull(groupSearched)
    }

    @Test(enabled = true)
    public void testUserPasswordRepository() {
        UserPassword userPassword = new UserPassword()
        userPassword.setUserId(new UserId(userId))
        userPassword.setPasswordSalt(UUID.randomUUID().toString())
        userPassword.setPasswordHash(UUID.randomUUID().toString())
        userPassword.setStrength(UserPasswordStrength.WEAK.toString())
        userPassword.setActive(true)
        userPassword.setChangeAtNextLogin(false)
        userPassword.setExpiresBy(new Date())
        userPassword.setCreatedTime(new Date())
        userPassword.setCreatedBy('lixia')
        userPassword.setUpdatedTime(new Date())
        userPassword.setUpdatedBy('lixia')
        userPassword = userPasswordRepository.create(userPassword).wrapped().get()

        UserPassword newUserPassword = userPasswordRepository.get(userPassword.getId()).wrapped().get()
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive())

        Boolean newValue = !userPassword.getActive()
        newUserPassword.setActive(newValue)
        userPasswordRepository.update(newUserPassword)
        newUserPassword = userPasswordRepository.get(newUserPassword.getId()).wrapped().get()
        Assert.assertEquals(newValue, newUserPassword.getActive())

        UserPasswordListOptions getOption = new UserPasswordListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserPassword> userPasswordList = userPasswordRepository.search(getOption).wrapped().get()
        assert userPasswordList.size() != 0
    }

    @Test(enabled = true)
    public void testUserPinRepository() {
        UserPin userPIN = new UserPin()
        userPIN.setUserId(new UserId(userId))
        userPIN.setPinHash(UUID.randomUUID().toString())
        userPIN.setPinSalt(UUID.randomUUID().toString())
        userPIN.setActive(true)
        userPIN.setChangeAtNextLogin(false)
        userPIN.setExpiresBy(new Date())
        userPIN.setCreatedTime(new Date())
        userPIN.setCreatedBy('lixia')
        userPIN.setUpdatedTime(new Date())
        userPIN.setUpdatedBy('lixia')
        userPIN = userPinRepository.create(userPIN).wrapped().get()

        UserPin newUserPin = userPinRepository.get(userPIN.getId()).wrapped().get()
        Assert.assertEquals(userPIN.getActive(), newUserPin.getActive())

        Boolean newValue = !userPIN.getActive()
        newUserPin.setActive(newValue)
        userPinRepository.update(newUserPin)
        newUserPin = userPinRepository.get(newUserPin.getId()).wrapped().get()
        Assert.assertEquals(newValue, newUserPin.getActive())

        UserPinListOptions getOption = new UserPinListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserPin> userPins = userPinRepository.search(getOption).wrapped().get()
        assert userPins.size() != 0
    }

    @Test(enabled = true)
    public void testUserAuthenticatorRepository() {
        UserAuthenticator authenticator = new UserAuthenticator()
        authenticator.setUserId(new UserId(userId))
        authenticator.setType('Google_account')
        authenticator.setValue(UUID.randomUUID().toString())
        authenticator.setCreatedTime(new Date())
        authenticator.setCreatedBy('lixia')
        authenticator = userAuthenticatorRepository.create(authenticator).wrapped().get()

        UserAuthenticator newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).wrapped().get()
        Assert.assertEquals(authenticator.getValue(), newUserAuthenticator.getValue())

        String newValue = UUID.randomUUID().toString()
        newUserAuthenticator.setValue(newValue)
        userAuthenticatorRepository.update(newUserAuthenticator)
        newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).wrapped().get()

        Assert.assertEquals(newValue, newUserAuthenticator.getValue())

        AuthenticatorListOptions getOption = new AuthenticatorListOptions()
        getOption.setValue(newValue)
        List<UserAuthenticator> userAuthenticators = userAuthenticatorRepository.search(getOption).wrapped().get()
        assert userAuthenticators.size() != 0
    }

    @Test(enabled = true)
    public void testUserDeviceRepository() {
        UserDevice userDevice = new UserDevice()
        userDevice.setDeviceId(new DeviceId(123L))
        userDevice.setUserId(new UserId(userId))
        userDevice.setCreatedBy('lixia')
        userDevice.setCreatedTime(new Date())

        userDevice = userDeviceRepository.create(userDevice).wrapped().get()

        UserDevice newUserDevice = userDeviceRepository.get((UserDeviceId)userDevice.id).wrapped().get()
        Assert.assertEquals(userDevice.deviceId, newUserDevice.deviceId)

        DeviceId newDeviceId = new DeviceId(345L)
        newUserDevice.setDeviceId(newDeviceId)
        userDeviceRepository.update(newUserDevice)

        newUserDevice = userDeviceRepository.get(userDevice.getId()).wrapped().get()
        Assert.assertEquals(newDeviceId, newUserDevice.deviceId)

        UserDeviceListOptions getOption = new UserDeviceListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserDevice> userDevices = userDeviceRepository.search(getOption).wrapped().get()
        assert userDevices.size() != 0
    }

    @Test(enabled = true)
    public void testUserGroupRepository() {
        UserGroup userGroup = new UserGroup()
        userGroup.setUserId(new UserId(userId))
        userGroup.setGroupId(new GroupId(1493188608L))
        userGroup.setCreatedBy('lixia')
        userGroup.setCreatedTime(new Date())
        userGroup = userGroupRepository.create(userGroup).wrapped().get()

        UserGroup newUserGroup = userGroupRepository.get(userGroup.getId()).wrapped().get()
        Assert.assertEquals(userGroup.getGroupId().getValue(), newUserGroup.getGroupId().getValue())

        UserGroupListOptions getOption = new UserGroupListOptions()
        getOption.setUserId(new UserId(userId))
        getOption.setGroupId(new GroupId(1493188608L))
        List<UserGroup> userGroups = userGroupRepository.search(getOption).wrapped().get()

        assert userGroups.size() != 0
    }

    @Test(enabled = true)
    public void testUserLoginAttemptRepository() {
        UserCredentialVerifyAttempt userLoginAttempt = new UserCredentialVerifyAttempt()
        userLoginAttempt.setUserId(new UserId(userId))
        userLoginAttempt.setType('pin')
        userLoginAttempt.setValue(UUID.randomUUID().toString())
        userLoginAttempt.setClientId(UUID.randomUUID().toString())
        userLoginAttempt.setIpAddress(UUID.randomUUID().toString())
        userLoginAttempt.setUserAgent(UUID.randomUUID().toString())
        userLoginAttempt.setSucceeded(true)
        userLoginAttempt.setCreatedBy('lixia')
        userLoginAttempt.setCreatedTime(new Date())

        userLoginAttempt = userLoginAttemptRepository.create(userLoginAttempt).wrapped().get()

        UserCredentialVerifyAttempt newUserLoginAttempt =
                userLoginAttemptRepository.get(userLoginAttempt.getId()).wrapped().get()
        Assert.assertEquals(userLoginAttempt.getIpAddress(), newUserLoginAttempt.getIpAddress())

        String value = UUID.randomUUID().toString()
        newUserLoginAttempt.setIpAddress(value)
        userLoginAttemptRepository.update(newUserLoginAttempt)

        newUserLoginAttempt = userLoginAttemptRepository.get(userLoginAttempt.getId()).wrapped().get()
        Assert.assertEquals(newUserLoginAttempt.getIpAddress(), value)

        UserCredentialAttemptListOptions getOption = new UserCredentialAttemptListOptions()
        getOption.setUserId(new UserId(userId))
        getOption.setType('pin')
        List<UserCredentialVerifyAttempt> userLoginAttempts =
                userLoginAttemptRepository.search(getOption).wrapped().get()
        Assert.assertEquals(userLoginAttempts.size(), 1)
    }

    @Test(enabled = true)
    public void testUserOptinRepository() {
        UserOptin userOptin = new UserOptin()
        userOptin.setUserId(new UserId(userId))
        userOptin.setType(UUID.randomUUID().toString())
        userOptin.setCreatedBy('lixia')
        userOptin.setCreatedTime(new Date())
        userOptin = userOptinRepository.create(userOptin).wrapped().get()

        UserOptin newUserOptin = userOptinRepository.get(userOptin.getId()).wrapped().get()
        Assert.assertEquals(userOptin.getType(), newUserOptin.getType())

        String value = UUID.randomUUID().toString()
        userOptin.setType(value)
        userOptinRepository.update(userOptin)

        newUserOptin = userOptinRepository.get(userOptin.getId()).wrapped().get()
        Assert.assertEquals(value, newUserOptin.getType())

        UserOptinListOptions getOption = new UserOptinListOptions()
        getOption.setType(value)
        getOption.setUserId(new UserId(userId))
        List<UserOptin> userOptins = userOptinRepository.search(getOption).wrapped().get()
        Assert.assertEquals(userOptins.size(), 1)
    }

    @Test(enabled = true)
    public void testUserSecurityQuestionRepository() {
        UserSecurityQuestion userSecurityQuestion = new UserSecurityQuestion()
        userSecurityQuestion.setUserId(new UserId(userId))
        userSecurityQuestion.setSecurityQuestionId(new SecurityQuestionId(123L))
        userSecurityQuestion.setAnswerHash(UUID.randomUUID().toString())
        userSecurityQuestion.setAnswerSalt(UUID.randomUUID().toString())
        userSecurityQuestion.setCreatedBy('lixia')
        userSecurityQuestion.setCreatedTime(new Date())

        userSecurityQuestion = userSecurityQuestionRepository.create(userSecurityQuestion).wrapped().get()

        UserSecurityQuestion newUserSecurityQuestion =
                userSecurityQuestionRepository.get(userSecurityQuestion.getId()).wrapped().get()
        Assert.assertEquals(userSecurityQuestion.getAnswerHash(), newUserSecurityQuestion.getAnswerHash())

        String value = UUID.randomUUID().toString()
        newUserSecurityQuestion.setAnswerSalt(value)
        userSecurityQuestionRepository.update(newUserSecurityQuestion)

        newUserSecurityQuestion = userSecurityQuestionRepository.get(userSecurityQuestion.getId()).wrapped().get()
        Assert.assertEquals(newUserSecurityQuestion.getAnswerSalt(), value)

        UserSecurityQuestionListOptions getOption = new UserSecurityQuestionListOptions()
        getOption.setUserId(new UserId(userId))
        getOption.setSecurityQuestionId(new SecurityQuestionId(123L))
        List<UserSecurityQuestion> securityQuestions = userSecurityQuestionRepository.search(getOption).wrapped().get()
        assert securityQuestions.size() != 0
    }

    @Test(enabled = true)
    public void testUserTosRepository() {
        UserTos userTos = new UserTos()
        userTos.setUserId(new UserId(userId))
        userTos.setTosUri(UUID.randomUUID().toString())
        userTos.setCreatedBy('lixia')
        userTos.setCreatedTime(new Date())
        userTos = userTosRepository.create(userTos).wrapped().get()

        UserTos newUserTos = userTosRepository.get(userTos.getId()).wrapped().get()
        Assert.assertEquals(userTos.getTosUri(), newUserTos.getTosUri())

        String value = UUID.randomUUID().toString()
        newUserTos.setTosUri(value)
        userTosRepository.update(newUserTos)

        newUserTos = userTosRepository.get(userTos.getId()).wrapped().get()
        Assert.assertEquals(value, newUserTos.getTosUri())

        UserTosListOptions userTosGetOption = new UserTosListOptions()
        userTosGetOption.setUserId(new UserId(userId))
        userTosGetOption.setTosUri(value)
        List<UserTos> userToses = userTosRepository.search(userTosGetOption).wrapped().get()
        Assert.assertEquals(userToses.size(), 1)
    }

    @Test(enabled = true)
    public void testUserSecurityQuestionAttempt() {
        UserSecurityQuestionAttempt attempt = new UserSecurityQuestionAttempt()
        attempt.setUserId(new UserId(userId))
        attempt.setSucceeded(true)
        attempt.setValue(UUID.randomUUID().toString())
        attempt.setClientId(UUID.randomUUID().toString())
        attempt.setIpAddress(UUID.randomUUID().toString())
        attempt.setSecurityQuestionId(new SecurityQuestionId(123L))
        attempt.setUserAgent(UUID.randomUUID().toString())

        attempt = userSecurityQuestionAttemptRepository.create(attempt).wrapped().get()

        UserSecurityQuestionAttempt newAttempt =
                userSecurityQuestionAttemptRepository.get(attempt.getId()).wrapped().get()
        Assert.assertEquals(attempt.getIpAddress(), newAttempt.getIpAddress())

        String value = UUID.randomUUID().toString()
        newAttempt.setIpAddress(value)
        userSecurityQuestionAttemptRepository.update(newAttempt)

        newAttempt = userSecurityQuestionAttemptRepository.get(attempt.getId()).wrapped().get()
        Assert.assertEquals(newAttempt.getIpAddress(), value)

        UserSecurityQuestionAttemptListOptions option = new UserSecurityQuestionAttemptListOptions()
        option.setUserId(new UserId(userId))
        option.setSecurityQuestionId(new SecurityQuestionId(123L))
        List<UserSecurityQuestionAttempt> attempts =
                userSecurityQuestionAttemptRepository.search(option).wrapped().get()
        assert attempts.size() != 0
    }

    @Test
    public void test() {
        String userName = 'liangfuxia'
        String password = '#Bugsfor$4'

        String original = userName + ':' + password
        String encode = Base64.encodeAsString(original)
        String decode = Base64.decodeAsString(encode)

        String[] split = decode.split(':')
        Assert.assertEquals(userName, split[0])
        Assert.assertEquals(password, split[1])
    }

    @Test
    public void testDeviceRepository() {
        Device device = new Device()
        device.setExternalRef(UUID.randomUUID().toString())
        device.setDescription(UUID.randomUUID().toString())

        Device newDevice = deviceRepository.create(device).wrapped().get()
        newDevice = deviceRepository.get((DeviceId)newDevice.id).wrapped().get()

        assert  device.externalRef == newDevice.externalRef

        String newDescription = UUID.randomUUID().toString()
        newDevice.setDescription(newDescription)
        deviceRepository.update(newDevice)

        device = deviceRepository.get((DeviceId)newDevice.id).wrapped().get()
        assert device.description == newDescription

        device = deviceRepository.searchByExternalRef(device.externalRef).wrapped().get()
        assert device.description == newDescription
    }
}
