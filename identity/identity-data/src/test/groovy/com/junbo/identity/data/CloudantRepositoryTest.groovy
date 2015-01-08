/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.*
import com.junbo.common.model.Results
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.*
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
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
    private UserCommunicationRepository userCommunicationRepository

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

    @Autowired
    @Qualifier('cloudantCountryRepository')
    private CountryRepository countryRepository

    @Autowired
    @Qualifier('cloudantCurrencyRepository')
    private CurrencyRepository currencyRepository

    @Autowired
    @Qualifier('cloudantLocaleRepository')
    private LocaleRepository localeRepository

    @Autowired
    @Qualifier('cloudantPITypeRepository')
    private PITypeRepository piTypeRepository


    @Test(enabled = false)
    public void testCountryRepository() {
        countryRepository.delete(new CountryId('US')).get()

        Country country = new Country()
        country.setId(new CountryId('US'))
        country.setCountryCode('US')
        country.setDefaultLocale(new LocaleId('en_US'))
        country.setDefaultCurrency(new CurrencyId('USD'))
        Country newCountry = countryRepository.create(country).get()
        assert  country.countryCode == newCountry.countryCode
    }

    @Test(enabled = false)
    public void testCurrencyRepository() {
        currencyRepository.delete(new CurrencyId('USD')).get()

        Currency currency = new Currency()
        currency.setId(new CurrencyId('USD'))
        currency.setCurrencyCode('USD')

        Currency newCurrency = currencyRepository.create(currency).get()
        assert  currency.currencyCode == newCurrency.currencyCode
    }

    @Test(enabled = false)
    public void testLocaleRepository() {
        localeRepository.delete(new LocaleId('en_US')).get()

        com.junbo.identity.spec.v1.model.Locale locale = new com.junbo.identity.spec.v1.model.Locale()
        locale.setId(new LocaleId('en_US'))
        locale.setLocaleCode('en_US')

        com.junbo.identity.spec.v1.model.Locale newLocale = localeRepository.create(locale).get()
        assert  locale.localeCode == newLocale.localeCode
    }

    @Test
    public void testDeviceRepository() {
        Device device = new Device()
        device.setFirmwareVersion(UUID.randomUUID().toString())
        device.setSerialNumber(UUID.randomUUID().toString())
        device.setType(new DeviceTypeId("DK2"))

        Device newDevice = deviceRepository.create(device).get()
        newDevice = deviceRepository.get((DeviceId)newDevice.id).get()

        assert  device.serialNumber == newDevice.serialNumber

        String newSerialNumber = UUID.randomUUID().toString()
        newDevice.setSerialNumber(newSerialNumber)
        deviceRepository.update(newDevice, newDevice).get()

        device = deviceRepository.get((DeviceId)newDevice.id).get()
        assert device.serialNumber == newSerialNumber

        device = deviceRepository.searchBySerialNumber(device.serialNumber).get()
        assert device.serialNumber == newSerialNumber
    }

    @Test
    public void testGroupRepository() {
        Group group = new Group()
        group.setName(UUID.randomUUID().toString())
        group.setOrganizationId(new OrganizationId(456L))
        group.setActive(true)
        group.setCreatedTime(new Date())
        group = groupRepository.create(group).get()

        Group newGroup = groupRepository.get(group.getId()).get()
        Assert.assertEquals(group.getName(), newGroup.getName())

        String newValue = 'test2 ' + UUID.randomUUID().toString()
        newGroup.setName(newValue)
        groupRepository.update(newGroup, group).get()
        newGroup = groupRepository.get(group.getId()).get()
        Assert.assertEquals(newValue, newGroup.getName())

        Results<Group> groupSearched = groupRepository.searchByOrganizationIdAndName(new OrganizationId(456L), newValue, Integer.MAX_VALUE, null).get()

        Assert.assertNotNull(groupSearched)
        assert groupSearched.total == 1
    }

    @Test
    public void testTosRepository() {
        Tos tos = new Tos()
        tos.content = 'content'

        tos = tosRepository.create(tos).get()
        Tos getTos = tosRepository.get(tos.getId()).get()

        tosRepository.delete(tos.getId()).get()

        Tos delTos = tosRepository.get(tos.getId()).get()
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

        userPassword.setCreatedBy(123L)
        userPassword.setUpdatedTime(new Date())
        userPassword.setUpdatedBy(123L)
        userPassword = userPasswordRepository.create(userPassword).get()

        UserPassword newUserPassword = userPasswordRepository.get(userPassword.getId()).get()
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive())

        Boolean newValue = !userPassword.getActive()
        newUserPassword.setActive(newValue)
        userPasswordRepository.update(newUserPassword, userPassword).get()
        newUserPassword = userPasswordRepository.get(newUserPassword.getId()).get()
        Assert.assertEquals(newValue, newUserPassword.getActive())

        UserPasswordListOptions getOption = new UserPasswordListOptions()
        getOption.setUserId(new UserId(userId))
        Results<UserPassword> userPasswordList = userPasswordRepository.searchByUserId(new UserId(userId),
                Integer.MAX_VALUE, 0).get()
        assert userPasswordList.items.size() != 0

        getOption.active = newUserPassword.active
        userPasswordList = userPasswordRepository.searchByUserIdAndActiveStatus(new UserId(userId),
                newUserPassword.active, Integer.MAX_VALUE, 0).get()
        assert userPasswordList.items.size() != 0
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
        userPIN.setCreatedBy(123L)
        userPIN.setUpdatedTime(new Date())
        userPIN.setUpdatedBy(123L)
        userPIN = userPinRepository.create(userPIN).get()

        UserPin newUserPin = userPinRepository.get(userPIN.getId()).get()
        Assert.assertEquals(userPIN.getActive(), newUserPin.getActive())

        Boolean newValue = !userPIN.getActive()
        newUserPin.setActive(newValue)
        userPinRepository.update(newUserPin, userPIN).get()
        newUserPin = userPinRepository.get(newUserPin.getId()).get()
        Assert.assertEquals(newValue, newUserPin.getActive())

        UserPinListOptions getOption = new UserPinListOptions()
        getOption.setUserId(new UserId(userId))
        Results<UserPin> userPins = userPinRepository.searchByUserId(new UserId(userId), Integer.MAX_VALUE, 0).get()
        assert userPins.getItems().size() != 0
    }

    @Test
    public void testUserAuthenticatorRepository() {
        UserAuthenticator authenticator = new UserAuthenticator()
        authenticator.setUserId(new UserId(userId))
        authenticator.setType('Google_account')
        authenticator.setExternalId(UUID.randomUUID().toString())
        authenticator.setCreatedTime(new Date())
        authenticator.setCreatedBy(123L)
        authenticator = userAuthenticatorRepository.create(authenticator).get()

        UserAuthenticator newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).get()
        Assert.assertEquals(authenticator.externalId, newUserAuthenticator.externalId)

        String newValue = UUID.randomUUID().toString()
        newUserAuthenticator.setExternalId(newValue)
        userAuthenticatorRepository.update(newUserAuthenticator, authenticator).get()
        newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).get()

        Assert.assertEquals(newValue, newUserAuthenticator.externalId)

        AuthenticatorListOptions getOption = new AuthenticatorListOptions()
        getOption.setExternalId(newValue)
        Results<UserAuthenticator> userAuthenticators = userAuthenticatorRepository.searchByExternalId(newValue, null,
                null).get()
        assert userAuthenticators != null
        assert userAuthenticators.items.size() != 0
    }

    @Test
    public void testUserLoginAttemptRepository() {
        UserCredentialVerifyAttempt userLoginAttempt = new UserCredentialVerifyAttempt()
        userLoginAttempt.setUserId(new UserId(userId))
        userLoginAttempt.setType('pin')
        userLoginAttempt.setValue(UUID.randomUUID().toString())
        userLoginAttempt.setClientId(new ClientId(UUID.randomUUID().toString()))
        userLoginAttempt.setIpAddress(UUID.randomUUID().toString())
        userLoginAttempt.setUserAgent(UUID.randomUUID().toString())
        userLoginAttempt.setSucceeded(true)
        userLoginAttempt.setCreatedBy(123L)
        userLoginAttempt.setCreatedTime(new Date())

        userCredentialVerifyAttemptRepository.create(userLoginAttempt).get()

        UserCredentialAttemptListOptions getOption = new UserCredentialAttemptListOptions()
        getOption.setUserId(new UserId(userId))
        getOption.setType('pin')
        List<UserCredentialVerifyAttempt> userLoginAttempts =
                userCredentialVerifyAttemptRepository.searchByUserIdAndCredentialTypeAndInterval(new UserId(userId), 'pin', 0L, Integer.MAX_VALUE, 0).get()
        assert  userLoginAttempts.size() != 0
    }

    @Test
    public void testUserGroupRepository() {
        UserGroup userGroup = new UserGroup()
        userGroup.setUserId(new UserId(userId))
        userGroup.setGroupId(new GroupId("1493188608L"))
        userGroup.setCreatedBy(123L)
        userGroup.setCreatedTime(new Date())
        userGroup = userGroupRepository.create(userGroup).get()

        UserGroup newUserGroup = userGroupRepository.get(userGroup.getId()).get()
        Assert.assertEquals(userGroup.getGroupId().getValue(), newUserGroup.getGroupId().getValue())

        UserGroupListOptions getOption = new UserGroupListOptions()
        getOption.setUserId(new UserId(userId))
        getOption.setGroupId(new GroupId("1493188608L"))
        Results<UserGroup> userGroups = userGroupRepository.searchByUserIdAndGroupId(new UserId(userId),
                new GroupId("1493188608L"), Integer.MAX_VALUE, 0).get()
        assert userGroups != null
        assert userGroups.items.size() != 0

        getOption.setGroupId(newUserGroup.groupId)
        userGroups = userGroupRepository.searchByUserIdAndGroupId(new UserId(userId), newUserGroup.groupId,
                Integer.MAX_VALUE, 0).get()
        assert userGroups != null
        assert userGroups.items.size() != 0

        getOption.setUserId(null)
        userGroups = userGroupRepository.searchByGroupId(newUserGroup.groupId, Integer.MAX_VALUE, 0).get()
        assert userGroups != null
        assert userGroups.items.size() != 0
    }

    @Test
    public void testUserOptinRepository() {
        UserCommunication userOptin = new UserCommunication()
        userOptin.setUserId(new UserId(userId))
        userOptin.setCommunicationId(new CommunicationId(idGenerator.nextId().toString()))
        userOptin.setCreatedBy(123L)
        userOptin.setCreatedTime(new Date())
        userOptin = userCommunicationRepository.create(userOptin).get()

        UserCommunication newUserOptin = userCommunicationRepository.get(userOptin.getId()).get()
        Assert.assertEquals(userOptin.communicationId, newUserOptin.communicationId)

        CommunicationId newCommunicationId = new CommunicationId(idGenerator.nextId().toString())
        userOptin.setCommunicationId(newCommunicationId)
        userCommunicationRepository.update(userOptin, userOptin).get()

        newUserOptin = userCommunicationRepository.get(userOptin.getId()).get()
        Assert.assertEquals(newCommunicationId, newUserOptin.getCommunicationId())

        UserOptinListOptions getOption = new UserOptinListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserCommunication> userOptins = userCommunicationRepository.searchByUserId(new UserId(userId), null,
                null).get().getItems()
        assert userOptins.size() != 0
    }

    @Test
    public void testUserRepository() throws Exception {
        User user = new User()
        user.setStatus('ACTIVE')
        user.setIsAnonymous(false)
        def random = UUID.randomUUID().toString()
        user.setPreferredLocale(new LocaleId("en_US"))
        user.setPreferredTimezone(UUID.randomUUID().toString())
        user.setCreatedTime(new Date())
        user.setCreatedBy(123L)
        user = userRepository.create(user).get()

        User newUser = userRepository.get(user.getId()).get()
        Assert.assertEquals(user.preferredLocale, newUser.preferredLocale)

        String newPreferredTimeZone = UUID.randomUUID().toString()
        newUser.setPreferredTimezone(newPreferredTimeZone)
        newUser = userRepository.update(newUser, user).get()
        Assert.assertEquals(newUser.getPreferredTimezone(), newPreferredTimeZone)
    }

    @Test
    public void testUserSecurityQuestionAttempt() {
        UserSecurityQuestionVerifyAttempt attempt = new UserSecurityQuestionVerifyAttempt()
        attempt.setUserId(new UserId(userId))
        attempt.setSucceeded(true)
        attempt.setValue(UUID.randomUUID().toString())
        attempt.setClientId(new ClientId(UUID.randomUUID().toString()))
        attempt.setIpAddress(UUID.randomUUID().toString())
        attempt.setUserSecurityQuestionId(new UserSecurityQuestionId("123L"))
        attempt.setUserAgent(UUID.randomUUID().toString())

        attempt = userSecurityQuestionAttemptRepository.create(attempt).get()

        UserSecurityQuestionVerifyAttempt newAttempt =
                userSecurityQuestionAttemptRepository.get(attempt.getId()).get()
        Assert.assertEquals(attempt.getIpAddress(), newAttempt.getIpAddress())

        UserSecurityQuestionAttemptListOptions option = new UserSecurityQuestionAttemptListOptions()
        option.setUserId(new UserId(userId))
        option.setUserSecurityQuestionId(new UserSecurityQuestionId("123L"))
        Results<UserSecurityQuestionVerifyAttempt> attempts =
                userSecurityQuestionAttemptRepository.searchByUserIdAndSecurityQuestionId(new UserId(userId),
                        new UserSecurityQuestionId("123L"), Integer.MAX_VALUE, 0).get()
        assert attempts != null
        assert attempts.items.size() != 0
    }

    @Test
    public void testUserSecurityQuestionRepository() {
        UserSecurityQuestion userSecurityQuestion = new UserSecurityQuestion()
        userSecurityQuestion.setUserId(new UserId(userId))
        userSecurityQuestion.setSecurityQuestion('whosyourdaddy')
        userSecurityQuestion.setAnswerHash(UUID.randomUUID().toString())
        userSecurityQuestion.setCreatedBy(123L)
        userSecurityQuestion.setCreatedTime(new Date())

        userSecurityQuestion = userSecurityQuestionRepository.create(userSecurityQuestion).get()

        UserSecurityQuestion newUserSecurityQuestion =
                userSecurityQuestionRepository.get(userSecurityQuestion.getId()).get()
        Assert.assertEquals(userSecurityQuestion.getAnswerHash(), newUserSecurityQuestion.getAnswerHash())

        String value = UUID.randomUUID().toString()
        newUserSecurityQuestion.setAnswerHash(value)
        userSecurityQuestionRepository.update(newUserSecurityQuestion, userSecurityQuestion).get()

        newUserSecurityQuestion = userSecurityQuestionRepository.get(userSecurityQuestion.getId()).get()
        Assert.assertEquals(newUserSecurityQuestion.getAnswerHash(), value)

        Results<UserSecurityQuestion> securityQuestions = userSecurityQuestionRepository.
                searchByUserId(new UserId(userId), Integer.MAX_VALUE, 0).get()
        assert securityQuestions.items.size() != 0
    }

    @Test
    public void testUserTosRepository() {
        UserTosAgreement userTos = new UserTosAgreement()
        userTos.setUserId(new UserId(userId))
        userTos.setTosId(new TosId("123L"))
        userTos.setCreatedBy(123L)
        userTos.setCreatedTime(new Date())
        userTos = userTosRepository.create(userTos).get()

        UserTosAgreement newUserTos = userTosRepository.get(userTos.getId()).get()
        Assert.assertEquals(userTos.getTosId(), newUserTos.getTosId())

        newUserTos.setTosId(new TosId("456L"))
        userTosRepository.update(newUserTos, userTos).get()

        newUserTos = userTosRepository.get(userTos.getId()).get()
        Assert.assertEquals(new TosId("456L"), newUserTos.getTosId())

        UserTosAgreementListOptions userTosGetOption = new UserTosAgreementListOptions()
        userTosGetOption.setUserId(new UserId(userId))
        userTosGetOption.setTosId(new TosId("456L"))
        Results<UserTosAgreement> userToses = userTosRepository.searchByUserIdAndTosId(new UserId(userId), new TosId("456L"), Integer.MAX_VALUE, 0).get()
        assert userToses != null;
        assert userToses.items.size() != 0
    }
}
