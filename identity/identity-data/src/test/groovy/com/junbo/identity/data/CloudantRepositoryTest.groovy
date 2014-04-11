/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data

import com.junbo.common.id.DeviceId
import com.junbo.common.id.GroupId
import com.junbo.common.id.UserDeviceId
import com.junbo.common.id.UserId
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.model.UserEmail
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
public class CloudantRepositoryTest extends AbstractTestNGSpringContextTests {
    // This is the fake value to meet current requirement.
    private final long userId = 1493188608L

    @Autowired
    @Qualifier('cloudantDeviceRepository')
    private DeviceRepository deviceRepository

    @Autowired
    @Qualifier('cloudantUserAuthenticatorRepository')
    private UserAuthenticatorRepository userAuthenticatorRepository

    @Autowired
    @Qualifier('cloudantGroupRepository')
    private GroupRepository groupRepository

    @Autowired
    @Qualifier('cloudantTosRepository')
    private TosRepository tosRepository

    @Autowired
    @Qualifier('cloudantUserPasswordRepository')
    private UserPasswordRepository userPasswordRepository

    @Autowired
    @Qualifier('cloudantUserPinRepository')
    private UserPinRepository userPinRepository

    @Autowired
    @Qualifier('cloudantUserCredentialVerifyAttemptRepository')
    private UserCredentialVerifyAttemptRepository userCredentialVerifyAttemptRepository

    @Autowired
    @Qualifier('cloudantUserDeviceRepository')
    private UserDeviceRepository userDeviceRepository

    @Autowired
    @Qualifier('cloudantUserGroupRepository')
    private UserGroupRepository userGroupRepository

    @Autowired
    @Qualifier('cloudantUserOptinRepository')
    private UserOptinRepository userOptinRepository

    @Autowired
    @Qualifier('cloudantUserPiiRepository')
    private UserPiiRepository userPiiRepository

    @Autowired
    @Qualifier('cloudantUserRepository')
    private UserRepository userRepository

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

    @Test
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

    @Test
    public void testTosRepository() {
        Tos tos = new Tos()
        tos.title = 'title'
        tos.locale = 'en_us'
        tos.content = 'content'

        tos = tosRepository.create(tos).wrapped().get()
        Tos getTos = tosRepository.get(tos.getId()).wrapped().get()
        Assert.assertEquals(tos.title, getTos.title)

        tosRepository.delete(tos.getId())

        Tos delTos = tosRepository.get(tos.getId()).wrapped().get()
        Assert.assertNull(delTos)
    }

    @Test
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

        getOption.active = newUserPassword.active
        userPasswordList = userPasswordRepository.search(getOption).wrapped().get()
        assert userPasswordList.size() != 0
    }

    @Test
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

    @Test
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

    @Test
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

        userCredentialVerifyAttemptRepository.create(userLoginAttempt).wrapped().get()

        UserCredentialAttemptListOptions getOption = new UserCredentialAttemptListOptions()
        getOption.setUserId(new UserId(userId))
        getOption.setType('pin')
        List<UserCredentialVerifyAttempt> userLoginAttempts =
                userCredentialVerifyAttemptRepository.search(getOption).wrapped().get()
        assert  userLoginAttempts.size() != 0
    }

    @Test
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

        getOption.setDeviceId(newUserDevice.deviceId)
        userDevices = userDeviceRepository.search(getOption).wrapped().get()
        assert userDevices.size() != 0

        getOption.setUserId(null)
        userDevices = userDeviceRepository.search(getOption).wrapped().get()
        assert userDevices.size() != 0
    }

    @Test
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

        getOption.setGroupId(newUserGroup.groupId)
        userGroups = userGroupRepository.search(getOption).wrapped().get()
        assert userGroups.size() != 0

        getOption.setUserId(null)
        userGroups = userGroupRepository.search(getOption).wrapped().get()
        assert userGroups.size() != 0
    }

    @Test
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
        getOption.setUserId(new UserId(userId))
        List<UserOptin> userOptins = userOptinRepository.search(getOption).wrapped().get()
        assert userOptins.size() != 0
    }

    @Test
    public void testUserPiiRepository() {
        UserPii userPii = new UserPii()
        userPii.setUserId(new UserId(userId))
        userPii.setBirthday(new Date())
        userPii.setDisplayName('hao')
        userPii.setDisplayNameType(0)
        userPii.setGender('male')
        userPii.setName(new UserName(firstName: 'first_name', lastName: 'last_name'))
        def search = UUID.randomUUID().toString()
        userPii.setEmails(['primary': new UserEmail(value: 'primary@silkcloud.com', verified: false),
                           'secondary': new UserEmail(value: search, verified: false)])
        userPii.setCreatedBy('lixia')
        userPii.setCreatedTime(new Date())
        userPii = userPiiRepository.create(userPii).wrapped().get()

        UserPii newUserPii = userPiiRepository.get(userPii.getId()).wrapped().get()
        Assert.assertEquals(userPii.getBirthday(), newUserPii.getBirthday())

        userPii.setGender('female')
        userPiiRepository.update(userPii)

        newUserPii = userPiiRepository.get(userPii.getId()).wrapped().get()
        Assert.assertEquals(userPii.getGender(), newUserPii.getGender())

        UserPiiListOptions options = new UserPiiListOptions()
        options.setUserId(new UserId(userId))
        List<UserPii> userPiiList = userPiiRepository.search(options).wrapped().get()
        assert userPiiList.size() != 0

        options.setUserId(null)
        options.setEmail(search)
        userPiiList = userPiiRepository.search(options).wrapped().get()
        assert userPiiList.size() != 0
    }

    @Test
    public void testUserRepository() throws Exception {
        User user = new User()
        user.setActive(true)
        user.setCurrency('USD')
        user.setLocale('en_US')
        def random = UUID.randomUUID().toString()
        user.setNickName(random)
        user.setPreferredLanguage(random)
        user.setTimezone(random)
        user.setType(random)
        user.setUsername(random)
        user.setCanonicalUsername(random)
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
}
