/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data

import com.junbo.common.id.DeviceId
import com.junbo.common.id.UserId
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
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
    private UserGroupRepository userGroupRepository
    @Qualifier('cloudantUserPinRepository')
    private UserPinRepository userPinRepository

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
}
