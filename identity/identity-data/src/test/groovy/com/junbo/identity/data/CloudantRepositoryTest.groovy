/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data

import com.junbo.common.id.ClientId
import com.junbo.common.id.CommunicationId
import com.junbo.common.id.DeviceId
import com.junbo.common.id.DeviceTypeId
import com.junbo.common.id.GroupId
import com.junbo.common.id.TosId
import com.junbo.common.id.UserDeviceId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.sharding.IdGenerator
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
@ContextConfiguration(locations = ['classpath:test/spring/context-test.xml'])
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional('transactionManager')
@CompileStatic
public class CloudantRepositoryTest extends AbstractTestNGSpringContextTests {
    // This is the fake value to meet current requirement.
    private final long userId = 1493188608L

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Autowired
    @Qualifier('cloudantAddressRepository')
    private AddressRepository addressRepository

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
    @Qualifier('cloudantUserGroupRepository')
    private UserGroupRepository userGroupRepository

    @Autowired
    @Qualifier('cloudantUserOptinRepository')
    private UserCommunicationRepository userOptinRepository

    @Autowired
    @Qualifier('cloudantUserRepository')
    private UserRepository userRepository

    @Autowired
    @Qualifier('cloudantUserSecurityQuestionRepository')
    private UserSecurityQuestionRepository userSecurityQuestionRepository


    @Autowired
    @Qualifier('cloudantUserSecurityQuestionAttemptRepository')
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository

    @Autowired
    @Qualifier('cloudantUserTosRepository')
    private UserTosRepository userTosRepository

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
        device.setFirmwareVersion(UUID.randomUUID().toString())
        device.setSerialNumber(UUID.randomUUID().toString())
        device.setType(new DeviceTypeId(123L))

        Device newDevice = deviceRepository.create(device).wrapped().get()
        newDevice = deviceRepository.get((DeviceId)newDevice.id).wrapped().get()

        assert  device.serialNumber == newDevice.serialNumber

        String newSerialNumber = UUID.randomUUID().toString()
        newDevice.setSerialNumber(newSerialNumber)
        deviceRepository.update(newDevice)

        device = deviceRepository.get((DeviceId)newDevice.id).wrapped().get()
        assert device.serialNumber == newSerialNumber

        device = deviceRepository.searchBySerialNumber(device.serialNumber).wrapped().get()
        assert device.serialNumber == newSerialNumber
    }

    @Test(enabled = true)
    public void testAddressRepository() {
        Address address = new Address()
        address.city = 'shanghai'
        address.street1 = UUID.randomUUID().toString()
        address.postalCode = '201102'
        address.userId = new UserId(userId)
        address = addressRepository.create(address).wrapped().get()

        Address newAddress = addressRepository.get(address.id).wrapped().get()
        Assert.assertEquals(address.city, newAddress.city)
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
        authenticator.setExternalId(UUID.randomUUID().toString())
        authenticator.setCreatedTime(new Date())
        authenticator.setCreatedBy('lixia')
        authenticator = userAuthenticatorRepository.create(authenticator).wrapped().get()

        UserAuthenticator newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).wrapped().get()
        Assert.assertEquals(authenticator.externalId, newUserAuthenticator.externalId)

        String newValue = UUID.randomUUID().toString()
        newUserAuthenticator.setExternalId(newValue)
        userAuthenticatorRepository.update(newUserAuthenticator)
        newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).wrapped().get()

        Assert.assertEquals(newValue, newUserAuthenticator.externalId)

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
        userLoginAttempt.setClientId(new ClientId(234L))
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
        UserCommunication userOptin = new UserCommunication()
        userOptin.setUserId(new UserId(userId))
        userOptin.setCommunicationId(new CommunicationId(idGenerator.nextId()))
        userOptin.setCreatedBy('lixia')
        userOptin.setCreatedTime(new Date())
        userOptin = userOptinRepository.create(userOptin).wrapped().get()

        UserCommunication newUserOptin = userOptinRepository.get(userOptin.getId()).wrapped().get()
        Assert.assertEquals(userOptin.communicationId, newUserOptin.communicationId)

        CommunicationId newCommunicationId = new CommunicationId(idGenerator.nextId())
        userOptin.setCommunicationId(newCommunicationId)
        userOptinRepository.update(userOptin)

        newUserOptin = userOptinRepository.get(userOptin.getId()).wrapped().get()
        Assert.assertEquals(newCommunicationId, newUserOptin.getCommunicationId())

        UserOptinListOptions getOption = new UserOptinListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserCommunication> userOptins = userOptinRepository.search(getOption).wrapped().get()
        assert userOptins.size() != 0
    }

    @Test
    public void testUserRepository() throws Exception {
        User user = new User()
        user.setStatus('ACTIVE')
        user.setIsAnonymous(false)
        def random = UUID.randomUUID().toString()
        user.setUsername(random)
        user.setPreferredLocale(UUID.randomUUID().toString())
        user.setPreferredTimezone(UUID.randomUUID().toString())
        user.setCanonicalUsername(random)
        user.setCreatedTime(new Date())
        user.setCreatedBy('lixia')
        user = userRepository.create(user).wrapped().get()

        User newUser = userRepository.get(user.getId()).wrapped().get()
        Assert.assertEquals(user.preferredLocale, newUser.preferredLocale)

        String newPreferredTimeZone = UUID.randomUUID().toString()
        newUser.setPreferredTimezone(newPreferredTimeZone)
        newUser = userRepository.update(newUser).wrapped().get()
        Assert.assertEquals(newUser.getPreferredTimezone(), newPreferredTimeZone)

        User findUser = userRepository.getUserByCanonicalUsername(newUser.getUsername()).wrapped().get()
        Assert.assertNotNull(findUser)
    }

    @Test
    public void testUserSecurityQuestionAttempt() {
        UserSecurityQuestionVerifyAttempt attempt = new UserSecurityQuestionVerifyAttempt()
        attempt.setUserId(new UserId(userId))
        attempt.setSucceeded(true)
        attempt.setValue(UUID.randomUUID().toString())
        attempt.setClientId(new ClientId(idGenerator.nextId()))
        attempt.setIpAddress(UUID.randomUUID().toString())
        attempt.setUserSecurityQuestionId(new UserSecurityQuestionId(123L))
        attempt.setUserAgent(UUID.randomUUID().toString())

        attempt = userSecurityQuestionAttemptRepository.create(attempt).wrapped().get()

        UserSecurityQuestionVerifyAttempt newAttempt =
                userSecurityQuestionAttemptRepository.get(attempt.getId()).wrapped().get()
        Assert.assertEquals(attempt.getIpAddress(), newAttempt.getIpAddress())

        UserSecurityQuestionAttemptListOptions option = new UserSecurityQuestionAttemptListOptions()
        option.setUserId(new UserId(userId))
        option.setUserSecurityQuestionId(new UserSecurityQuestionId(123L))
        List<UserSecurityQuestionVerifyAttempt> attempts =
                userSecurityQuestionAttemptRepository.search(option).wrapped().get()
        assert attempts.size() != 0
    }

    @Test
    public void testUserSecurityQuestionRepository() {
        UserSecurityQuestion userSecurityQuestion = new UserSecurityQuestion()
        userSecurityQuestion.setUserId(new UserId(userId))
        userSecurityQuestion.setSecurityQuestion('whosyourdaddy')
        userSecurityQuestion.setAnswerHash(UUID.randomUUID().toString())
        userSecurityQuestion.setCreatedBy('lixia')
        userSecurityQuestion.setCreatedTime(new Date())

        userSecurityQuestion = userSecurityQuestionRepository.create(userSecurityQuestion).wrapped().get()

        UserSecurityQuestion newUserSecurityQuestion =
                userSecurityQuestionRepository.get(userSecurityQuestion.getId()).wrapped().get()
        Assert.assertEquals(userSecurityQuestion.getAnswerHash(), newUserSecurityQuestion.getAnswerHash())

        String value = UUID.randomUUID().toString()
        newUserSecurityQuestion.setAnswerHash(value)
        userSecurityQuestionRepository.update(newUserSecurityQuestion)

        newUserSecurityQuestion = userSecurityQuestionRepository.get(userSecurityQuestion.getId()).wrapped().get()
        Assert.assertEquals(newUserSecurityQuestion.getAnswerHash(), value)

        List<UserSecurityQuestion> securityQuestions = userSecurityQuestionRepository.
                search(new UserSecurityQuestionListOptions(userId: new UserId(userId))).wrapped().get()
        assert securityQuestions.size() != 0
    }

    @Test
    public void testUserTosRepository() {
        UserTosAgreement userTos = new UserTosAgreement()
        userTos.setUserId(new UserId(userId))
        userTos.setTosId(new TosId(123L))
        userTos.setCreatedBy('lixia')
        userTos.setCreatedTime(new Date())
        userTos = userTosRepository.create(userTos).wrapped().get()

        UserTosAgreement newUserTos = userTosRepository.get(userTos.getId()).wrapped().get()
        Assert.assertEquals(userTos.getTosId(), newUserTos.getTosId())

        newUserTos.setTosId(new TosId(456L))
        userTosRepository.update(newUserTos)

        newUserTos = userTosRepository.get(userTos.getId()).wrapped().get()
        Assert.assertEquals(new TosId(456L), newUserTos.getTosId())

        UserTosAgreementListOptions userTosGetOption = new UserTosAgreementListOptions()
        userTosGetOption.setUserId(new UserId(userId))
        userTosGetOption.setTosId(new TosId(456L))
        List<UserTosAgreement> userToses = userTosRepository.search(userTosGetOption).wrapped().get()
        assert userToses.size() != 0
    }
}
