/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.*
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.*
import com.junbo.sharding.IdGenerator
import groovy.time.TimeCategory
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
public class RepositoryTest extends AbstractTestNGSpringContextTests {
    // This is the fake value to meet current requirement.
    private final long userId = 1493188608L

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Autowired
    @Qualifier('tosRepository')
    private TosRepository tosRepository

    @Autowired
    @Qualifier('userRepository')
    private UserRepository userRepository

    @Autowired
    @Qualifier('groupRepository')
    private GroupRepository groupRepository

    @Autowired
    @Qualifier('userPinRepository')
    private UserPinRepository userPinRepository

    @Autowired
    @Qualifier('userAuthenticatorRepository')
    private UserAuthenticatorRepository userAuthenticatorRepository

    @Autowired
    @Qualifier('userGroupRepository')
    private UserGroupRepository userGroupRepository

    @Autowired
    @Qualifier('userCredentialVerifyAttemptRepository')
    private UserCredentialVerifyAttemptRepository userCredentialVerifyAttemptRepository

    @Autowired
    @Qualifier('userCommunicationRepository')
    private UserCommunicationRepository userCommunicationRepository

    @Autowired
    @Qualifier('userPasswordRepository')
    private UserPasswordRepository userPasswordRepository

    @Autowired
    @Qualifier('userSecurityQuestionRepository')
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    @Autowired
    @Qualifier('userTosRepository')
    private UserTosRepository userTosRepository

    @Autowired
    @Qualifier('userSecurityQuestionAttemptRepository')
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository

    @Autowired
    @Qualifier('deviceRepository')
    private DeviceRepository deviceRepository

    @Autowired
    @Qualifier('userTeleRepository')
    private UserTeleRepository userTeleRepository

    @Autowired
    @Qualifier('userTeleAttemptRepository')
    private UserTeleAttemptRepository userTeleAttemptRepository

    @Autowired
    @Qualifier('userTeleBackupCodeRepository')
    private UserTeleBackupCodeRepository userTeleBackupCodeRepository

    @Autowired
    @Qualifier('userTeleBackupCodeAttemptRepository')
    private UserTeleBackupCodeAttemptRepository userTeleBackupCodeAttemptRepository

    @Autowired
    @Qualifier('userPersonalInfoRepository')
    private UserPersonalInfoRepository userPersonalInfoRepository

    @Autowired
    @Qualifier('countryRepository')
    private CountryRepository countryRepository

    @Autowired
    @Qualifier('currencyRepository')
    private CurrencyRepository currencyRepository

    @Autowired
    @Qualifier('localeRepository')
    private LocaleRepository localeRepository

    @Autowired
    @Qualifier('piTypeRepository')
    private PITypeRepository piTypeRepository


    @Test
    public void testCountryRepository() {
        countryRepository.delete(new CountryId('US')).wrapped().get()

        Country country = new Country()
        country.setId(new CountryId('US'))
        country.setCountryCode('US')
        country.setDefaultLocale(new LocaleId('en_US'))
        country.setDefaultCurrency(new CurrencyId('USD'))
        Country newCountry = countryRepository.create(country).wrapped().get()
        assert  country.countryCode == newCountry.countryCode
    }

    @Test
    public void testCurrencyRepository() {
        currencyRepository.delete(new CurrencyId('USD')).wrapped().get()

        Currency currency = new Currency()
        currency.setId(new CurrencyId('USD'))
        currency.setCurrencyCode('USD')

        Currency newCurrency = currencyRepository.create(currency).wrapped().get()
        assert  currency.currencyCode == newCurrency.currencyCode
    }

    @Test
    public void testLocaleRepository() {
        localeRepository.delete(new LocaleId('en_US')).wrapped().get()

        com.junbo.identity.spec.v1.model.Locale locale = new com.junbo.identity.spec.v1.model.Locale()
        locale.setId(new LocaleId('en_US'))
        locale.setLocaleCode('en_US')

        com.junbo.identity.spec.v1.model.Locale newLocale = localeRepository.create(locale).wrapped().get()
        assert  locale.localeCode == newLocale.localeCode
    }

    @Test
    public void testUserRepository() throws Exception {
        User user = new User()
        user.setIsAnonymous(true)
        def random = UUID.randomUUID().toString()
        user.setCanonicalUsername(random)
        user.setUsername(random)
        user.setPreferredLocale(new LocaleId(UUID.randomUUID().toString()))
        user.setPreferredTimezone(UUID.randomUUID().toString())
        user.setCreatedTime(new Date())
        user.setCreatedBy(123L)
        UserPersonalInfoLink userPersonalInfoLink = new UserPersonalInfoLink()
        userPersonalInfoLink.setUserId(new UserId(idGenerator.nextId()))
        userPersonalInfoLink.setValue(new UserPersonalInfoId(idGenerator.nextId()))
        List<UserPersonalInfoLink> personalInfoLinkList = new ArrayList<>()
        personalInfoLinkList.add(userPersonalInfoLink)
        user.setAddresses(personalInfoLinkList)
        user.setEmails(personalInfoLinkList)

        user = userRepository.create(user).wrapped().get()

        User newUser = userRepository.get(user.getId()).wrapped().get()
        Assert.assertEquals(user.getPreferredLocale(), newUser.getPreferredLocale())
        assert newUser.addresses != null
        assert newUser.addresses.size() == 1
        assert newUser.emails != null
        assert newUser.emails.size() == 1

        LocaleId newPreferredLocale = new LocaleId(UUID.randomUUID().toString())
        newUser.setPreferredLocale(newPreferredLocale)
        newUser = userRepository.update(newUser).wrapped().get()
        Assert.assertEquals(newUser.getPreferredLocale(), newPreferredLocale)

        User findUser = userRepository.getUserByCanonicalUsername(newUser.getUsername()).wrapped().get()
        Assert.assertNotNull(findUser)
    }

    @Test
    public void testTosRepository() {
        String content = UUID.randomUUID().toString()
        Tos tos = new Tos()
        tos.setTitle('title')
        tos.setContent(content)
        tos = tosRepository.create(tos).wrapped().get()

        Tos newTos = tosRepository.get(tos.getId()).wrapped().get()
        Assert.assertEquals(tos.getContent(), newTos.getContent())
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
        newGroup = groupRepository.update(newGroup).wrapped().get()
        Assert.assertEquals(newValue, newGroup.getName())
        Group groupSearched = groupRepository.searchByName(newValue).wrapped().get()
        Assert.assertNotNull(groupSearched)
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
        userPassword = userPasswordRepository.create(userPassword).wrapped().get()

        UserPassword newUserPassword = userPasswordRepository.get(userPassword.getId()).wrapped().get()
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive())

        Boolean newValue = !userPassword.getActive()
        newUserPassword.setActive(newValue)
        newUserPassword = userPasswordRepository.update(newUserPassword).wrapped().get()
        Assert.assertEquals(newValue, newUserPassword.getActive())

        UserPasswordListOptions getOption = new UserPasswordListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserPassword> userPasswordList = userPasswordRepository.search(getOption).wrapped().get()
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
        userPIN.setCreatedBy(123L)
        userPIN.setUpdatedTime(new Date())
        userPIN.setUpdatedBy(123L)
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
        authenticator.setCreatedBy(123L)
        authenticator = userAuthenticatorRepository.create(authenticator).wrapped().get()

        UserAuthenticator newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).wrapped().get()
        Assert.assertEquals(authenticator.getExternalId(), newUserAuthenticator.getExternalId())

        String newValue = UUID.randomUUID().toString()
        newUserAuthenticator.setExternalId(newValue)
        newUserAuthenticator = userAuthenticatorRepository.update(newUserAuthenticator).wrapped().get()

        Assert.assertEquals(newValue, newUserAuthenticator.getExternalId())

        AuthenticatorListOptions getOption = new AuthenticatorListOptions()
        getOption.setExternalId(newValue)
        List<UserAuthenticator> userAuthenticators = userAuthenticatorRepository.search(getOption).wrapped().get()
        assert userAuthenticators.size() != 0
    }

    @Test
    public void testUserGroupRepository() {
        UserGroup userGroup = new UserGroup()
        userGroup.setUserId(new UserId(userId))
        userGroup.setGroupId(new GroupId(1493188608L))
        userGroup.setCreatedBy(123L)
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

    @Test
    public void testUserLoginAttemptRepository() {
        UserCredentialVerifyAttempt userLoginAttempt = new UserCredentialVerifyAttempt()
        userLoginAttempt.setUserId(new UserId(userId))
        userLoginAttempt.setType('pin')
        userLoginAttempt.setValue(UUID.randomUUID().toString())
        userLoginAttempt.setClientId(new ClientId(idGenerator.nextId()))
        userLoginAttempt.setIpAddress(UUID.randomUUID().toString())
        userLoginAttempt.setUserAgent(UUID.randomUUID().toString())
        userLoginAttempt.setSucceeded(true)
        userLoginAttempt.setCreatedBy(123L)
        userLoginAttempt.setCreatedTime(new Date())

        userLoginAttempt = userCredentialVerifyAttemptRepository.create(userLoginAttempt).wrapped().get()

        UserCredentialVerifyAttempt newUserLoginAttempt =
                userCredentialVerifyAttemptRepository.get(userLoginAttempt.getId()).wrapped().get()
        Assert.assertEquals(userLoginAttempt.getIpAddress(), newUserLoginAttempt.getIpAddress())

        String value = UUID.randomUUID().toString()
        newUserLoginAttempt.setIpAddress(value)
        newUserLoginAttempt = userCredentialVerifyAttemptRepository.update(newUserLoginAttempt).wrapped().get()
        Assert.assertEquals(newUserLoginAttempt.getIpAddress(), value)

        UserCredentialAttemptListOptions getOption = new UserCredentialAttemptListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserCredentialVerifyAttempt> userLoginAttempts =
                userCredentialVerifyAttemptRepository.search(getOption).wrapped().get()
        assert userLoginAttempts.size() != 0
    }

    @Test
    public void testUserOptinRepository() {
        UserCommunication userOptin = new UserCommunication()
        userOptin.setUserId(new UserId(userId))
        userOptin.setCommunicationId(new CommunicationId(idGenerator.nextId()))
        userOptin.setCreatedBy(123L)
        userOptin.setCreatedTime(new Date())
        userOptin = userCommunicationRepository.create(userOptin).wrapped().get()

        UserCommunication newUserOptin = userCommunicationRepository.get(userOptin.getId()).wrapped().get()
        Assert.assertEquals(userOptin.getCommunicationId(), newUserOptin.getCommunicationId())

        CommunicationId value = new CommunicationId(idGenerator.nextId())
        userOptin.setCommunicationId(value)
        newUserOptin = userCommunicationRepository.update(userOptin).wrapped().get()
        Assert.assertEquals(value, newUserOptin.getCommunicationId())

        UserOptinListOptions getOption = new UserOptinListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserCommunication> userOptins = userCommunicationRepository.search(getOption).wrapped().get()
        assert userOptins.size() != 0
    }

    @Test
    public void testUserSecurityQuestionRepository() {
        UserSecurityQuestion userSecurityQuestion = new UserSecurityQuestion()
        userSecurityQuestion.setUserId(new UserId(userId))
        userSecurityQuestion.setSecurityQuestion('whosyourdaddy')
        userSecurityQuestion.setAnswerHash(UUID.randomUUID().toString())
        userSecurityQuestion.setCreatedBy(123L)
        userSecurityQuestion.setCreatedTime(new Date())

        userSecurityQuestion = userSecurityQuestionRepository.create(userSecurityQuestion).wrapped().get()

        UserSecurityQuestion newUserSecurityQuestion =
                userSecurityQuestionRepository.get(userSecurityQuestion.getId()).wrapped().get()
        Assert.assertEquals(userSecurityQuestion.getAnswerHash(), newUserSecurityQuestion.getAnswerHash())

        String value = UUID.randomUUID().toString()
        newUserSecurityQuestion.setAnswerHash(value)
        newUserSecurityQuestion = userSecurityQuestionRepository.update(newUserSecurityQuestion).wrapped().get()
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
        userTos.setCreatedBy(123L)
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
    public void testDeviceRepository() {
        Device device = new Device()
        device.setSerialNumber(UUID.randomUUID().toString())
        device.setType(new DeviceTypeId("DK2"))
        device.setFirmwareVersion(UUID.randomUUID().toString())

        Device newDevice = deviceRepository.create(device).wrapped().get()
        newDevice = deviceRepository.get((DeviceId)newDevice.id).wrapped().get()

        assert  device.serialNumber == newDevice.serialNumber

        String newSerialNumber = UUID.randomUUID().toString()
        newDevice.setSerialNumber(newSerialNumber)
        device = deviceRepository.update(newDevice).wrapped().get()
        assert device.serialNumber == newSerialNumber

        device = deviceRepository.searchBySerialNumber(device.serialNumber).wrapped().get()
        assert device.serialNumber == newSerialNumber
    }

    @Test
    public void testUserTeleRepository() {
        def after30Mins = new Date()
        use( TimeCategory ) {
            after30Mins = (new Date()) + 30.minutes
        }
        def id = idGenerator.nextId()
        def phoneNumber = new UserPersonalInfoId(idGenerator.nextId())
        UserTeleCode userTeleCode = new UserTeleCode()
        userTeleCode.setUserId(new UserId(id))
        userTeleCode.setActive(true)
        userTeleCode.setExpiresBy(after30Mins)
        userTeleCode.setPhoneNumber(phoneNumber)
        userTeleCode.setSentLanguage('en_US')
        userTeleCode.setTemplate('xxxxx')
        userTeleCode.setVerifyCode(UUID.randomUUID().toString())
        userTeleCode.setVerifyType('CALL')

        UserTeleCode newUserTeleCode = userTeleRepository.create(userTeleCode).wrapped().get()
        newUserTeleCode = userTeleRepository.get((UserTeleId)newUserTeleCode.id).wrapped().get()

        assert userTeleCode.phoneNumber == newUserTeleCode.phoneNumber

        UserPersonalInfoId newPhoneNumber = new UserPersonalInfoId(idGenerator.nextId())
        newUserTeleCode.setPhoneNumber(newPhoneNumber)
        userTeleCode = userTeleRepository.update(newUserTeleCode).wrapped().get()
        assert userTeleCode.phoneNumber == newPhoneNumber
    }

    @Test
    public void testUserTeleAttemptRepository() {
        def userTeleId = new UserTeleId(idGenerator.nextId())
        def userId = new UserId(idGenerator.nextId())
        UserTeleAttempt userTeleAttempt = new UserTeleAttempt()
        userTeleAttempt.setVerifyCode(UUID.randomUUID().toString())
        userTeleAttempt.setClientId(UUID.randomUUID().toString())
        userTeleAttempt.setUserId(userId)
        userTeleAttempt.setIpAddress(UUID.randomUUID().toString())
        userTeleAttempt.setSucceeded(true)
        userTeleAttempt.setUserAgent(UUID.randomUUID().toString())
        userTeleAttempt.setUserTeleId(userTeleId)

        UserTeleAttempt newUserTeleAttempt = userTeleAttemptRepository.create(userTeleAttempt).wrapped().get()
        newUserTeleAttempt = userTeleAttemptRepository.get((UserTeleAttemptId)newUserTeleAttempt.id).wrapped().get()

        assert userTeleAttempt.ipAddress == newUserTeleAttempt.ipAddress

        String newIpAddress = UUID.randomUUID().toString()
        newUserTeleAttempt.setIpAddress(newIpAddress)
        userTeleAttempt = userTeleAttemptRepository.update(newUserTeleAttempt).wrapped().get()
        assert userTeleAttempt.ipAddress == newIpAddress

        List<UserTeleAttempt> results = userTeleAttemptRepository.search(new UserTeleAttemptListOptions(
                userId: userId,
                userTeleId: userTeleId,
                offset: 0,
                limit: 100
        )).wrapped().get()
        assert results.size() != 0
    }

    @Test
    public void testUserTeleBackupCodeRepository() {
        def after30000Mins = new Date()
        use( TimeCategory ) {
            after30000Mins = (new Date()) + 30000.minutes
        }
        def userId = new UserId(idGenerator.nextId())
        UserTeleBackupCode userTeleBackupCode = new UserTeleBackupCode()
        userTeleBackupCode.setUserId(userId)
        userTeleBackupCode.setVerifyCode(UUID.randomUUID().toString())
        userTeleBackupCode.setActive(true)
        userTeleBackupCode.setExpiresBy(after30000Mins)
        UserTeleBackupCode newUserTeleBackupCode =
                userTeleBackupCodeRepository.create(userTeleBackupCode).wrapped().get()
        newUserTeleBackupCode =
                userTeleBackupCodeRepository.get((UserTeleBackupCodeId)newUserTeleBackupCode.id).wrapped().get()

        assert userTeleBackupCode.verifyCode == newUserTeleBackupCode.verifyCode

        String newVerifyCode = UUID.randomUUID().toString()
        newUserTeleBackupCode.setVerifyCode(newVerifyCode)
        userTeleBackupCode = userTeleBackupCodeRepository.update(newUserTeleBackupCode).wrapped().get()

        assert userTeleBackupCode.verifyCode == newVerifyCode

        List<UserTeleBackupCode> results = userTeleBackupCodeRepository.search(new UserTeleBackupCodeListOptions(
                userId: userId,
                offset: 0,
                limit: 100
        )).wrapped().get()
        assert results.size() != 0
    }

    @Test
    public void testUserTeleBackupCodeAttemptRepository() {
        def userId = new UserId(idGenerator.nextId())
        UserTeleBackupCodeAttempt attempt = new UserTeleBackupCodeAttempt()
        attempt.setUserId(userId)
        attempt.setVerifyCode(UUID.randomUUID().toString())
        attempt.setUserAgent(UUID.randomUUID().toString())
        attempt.setClientId(UUID.randomUUID().toString())
        attempt.setIpAddress(UUID.randomUUID().toString())
        attempt.setSucceeded(true)

        UserTeleBackupCodeAttempt newAttempt = userTeleBackupCodeAttemptRepository.create(attempt).wrapped().get()
        newAttempt = userTeleBackupCodeAttemptRepository.get(newAttempt.id).wrapped().get()

        assert newAttempt.verifyCode == attempt.verifyCode

        String newVerifyCode = UUID.randomUUID().toString()
        newAttempt.setVerifyCode(newVerifyCode)
        attempt = userTeleBackupCodeAttemptRepository.update(newAttempt).wrapped().get()

        assert attempt.verifyCode == newVerifyCode

        List<UserTeleBackupCodeAttempt> results = userTeleBackupCodeAttemptRepository.search(
                new UserTeleBackupCodeAttemptListOptions(
                        userId: userId,
                        offset: 0,
                        limit: 100
                )).wrapped().get()
        assert results.size() != 0
    }

    @Test(enabled = false)
    public void testUserPersonalInfoRepository() {
        UserId userId = new UserId(idGenerator.nextId())
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo()
        userPersonalInfo.setType('EMAIL')
        userPersonalInfo.setIsNormalized(true)
        userPersonalInfo.setLastValidateTime(new Date())
        userPersonalInfo.setUserId(userId)
        Email email = new Email()
        email.value = UUID.randomUUID().toString()
        email.value = false
        ObjectMapper objectMapper = new ObjectMapper()
        userPersonalInfo.setValue(objectMapper.valueToTree(email))

        UserPersonalInfo newUserPersonalInfo = userPersonalInfoRepository.create(userPersonalInfo).wrapped().get()
        newUserPersonalInfo = userPersonalInfoRepository.get(newUserPersonalInfo.id).wrapped().get()

        assert newUserPersonalInfo.type == userPersonalInfo.type
    }
}
