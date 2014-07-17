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
    @Qualifier('userTFARepository')
    private UserTFARepository userTFARepository

    @Autowired
    @Qualifier('userTFAAttemptRepository')
    private UserTFAAttemptRepository userTFAAttemptRepository

    @Autowired
    @Qualifier('userTFABackupCodeRepository')
    private UserTFABackupCodeRepository userTFABackupCodeRepository

    @Autowired
    @Qualifier('userTFABackupCodeAttemptRepository')
    private UserTFABackupCodeAttemptRepository userTFABackupCodeAttemptRepository

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

    @Autowired
    @Qualifier('organizationRepository')
    private OrganizationRepository organizationRepository

    @Test
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

    @Test
    public void testCurrencyRepository() {
        currencyRepository.delete(new CurrencyId('USD')).get()

        Currency currency = new Currency()
        currency.setId(new CurrencyId('USD'))
        currency.setCurrencyCode('USD')

        Currency newCurrency = currencyRepository.create(currency).get()
        assert  currency.currencyCode == newCurrency.currencyCode
    }

    @Test
    public void testLocaleRepository() {
        localeRepository.delete(new LocaleId('en_US')).get()

        com.junbo.identity.spec.v1.model.Locale locale = new com.junbo.identity.spec.v1.model.Locale()
        locale.setId(new LocaleId('en_US'))
        locale.setLocaleCode('en_US')

        com.junbo.identity.spec.v1.model.Locale newLocale = localeRepository.create(locale).get()
        assert  locale.localeCode == newLocale.localeCode
    }

    @Test
    public void testUserRepository() throws Exception {
        User user = new User()
        user.setIsAnonymous(true)
        def random = UUID.randomUUID().toString()
        user.setCanonicalUsername(random)
        user.setUsername(random)
        user.setPreferredLocale(new LocaleId("en_US"))
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

        user = userRepository.create(user).get()

        User newUser = userRepository.get(user.getId()).get()
        Assert.assertEquals(user.getPreferredLocale(), newUser.getPreferredLocale())
        assert newUser.addresses != null
        assert newUser.addresses.size() == 1
        assert newUser.emails != null
        assert newUser.emails.size() == 1

        LocaleId newPreferredLocale = new LocaleId(UUID.randomUUID().toString())
        newUser.setPreferredLocale(newPreferredLocale)
        newUser = userRepository.update(newUser, user).get()
        Assert.assertEquals(newUser.getPreferredLocale(), newPreferredLocale)

        User findUser = userRepository.searchUserByCanonicalUsername(newUser.getUsername()).get()
        Assert.assertNotNull(findUser)
    }

    @Test
    public void testTosRepository() {
        String content = UUID.randomUUID().toString()
        Tos tos = new Tos()
        tos.setTitle('title')
        tos.setContent(content)
        tos = tosRepository.create(tos).get()

        Tos newTos = tosRepository.get(tos.getId()).get()
        Assert.assertEquals(tos.getContent(), newTos.getContent())
    }

    @Test
    public void testGroupRepository() {
        Group group = new Group()
        group.setOrganizationId(new OrganizationId(789L))
        group.setName(UUID.randomUUID().toString())
        group.setActive(true)
        group.setCreatedTime(new Date())
        group = groupRepository.create(group).get()

        Group newGroup = groupRepository.get(group.getId()).get()
        Assert.assertEquals(group.getName(), newGroup.getName())

        String newValue = 'test2 ' + UUID.randomUUID().toString()
        newGroup.setName(newValue)
        newGroup = groupRepository.update(newGroup, group).get()
        Assert.assertEquals(newValue, newGroup.getName())
        Group groupSearched = groupRepository.searchByOrganizationIdAndName(new OrganizationId(789L), newValue, Integer.MAX_VALUE, 0).get()
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
        userPassword = userPasswordRepository.create(userPassword).get()

        UserPassword newUserPassword = userPasswordRepository.get(userPassword.getId()).get()
        Assert.assertEquals(userPassword.getActive(), newUserPassword.getActive())

        Boolean newValue = !userPassword.getActive()
        newUserPassword.setActive(newValue)
        newUserPassword = userPasswordRepository.update(newUserPassword, userPassword).get()
        Assert.assertEquals(newValue, newUserPassword.getActive())

        UserPasswordListOptions getOption = new UserPasswordListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserPassword> userPasswordList = userPasswordRepository.searchByUserId(new UserId(userId),
                Integer.MAX_VALUE, 0).get()
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
        List<UserPin> userPins = userPinRepository.searchByUserId(new UserId(userId), Integer.MAX_VALUE, 0).get()
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
        authenticator = userAuthenticatorRepository.create(authenticator).get()

        UserAuthenticator newUserAuthenticator = userAuthenticatorRepository.get(authenticator.getId()).get()
        Assert.assertEquals(authenticator.getExternalId(), newUserAuthenticator.getExternalId())

        String newValue = UUID.randomUUID().toString()
        newUserAuthenticator.setExternalId(newValue)
        newUserAuthenticator = userAuthenticatorRepository.update(newUserAuthenticator, authenticator).get()
        Assert.assertEquals(newValue, newUserAuthenticator.getExternalId())

        AuthenticatorListOptions getOption = new AuthenticatorListOptions()
        getOption.setExternalId(newValue)
        List<UserAuthenticator> userAuthenticators = userAuthenticatorRepository.searchByExternalId(newValue, null,
                null).get()
        assert userAuthenticators.size() != 0
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
        List<UserGroup> userGroups = userGroupRepository.searchByUserIdAndGroupId(new UserId(userId),
                new GroupId("1493188608L"), Integer.MAX_VALUE, 0).get()

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

        userLoginAttempt = userCredentialVerifyAttemptRepository.create(userLoginAttempt).get()

        UserCredentialVerifyAttempt newUserLoginAttempt =
                userCredentialVerifyAttemptRepository.get(userLoginAttempt.getId()).get()
        Assert.assertEquals(userLoginAttempt.getIpAddress(), newUserLoginAttempt.getIpAddress())

        String value = UUID.randomUUID().toString()
        newUserLoginAttempt.setIpAddress(value)
        newUserLoginAttempt = userCredentialVerifyAttemptRepository.update(newUserLoginAttempt, userLoginAttempt).get()
        Assert.assertEquals(newUserLoginAttempt.getIpAddress(), value)

        UserCredentialAttemptListOptions getOption = new UserCredentialAttemptListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserCredentialVerifyAttempt> userLoginAttempts =
                userCredentialVerifyAttemptRepository.searchByUserId(new UserId(userId), Integer.MAX_VALUE, 0).get()
        assert userLoginAttempts.size() != 0
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
        Assert.assertEquals(userOptin.getCommunicationId(), newUserOptin.getCommunicationId())

        CommunicationId value = new CommunicationId(idGenerator.nextId().toString())
        userOptin.setCommunicationId(value)
        newUserOptin = userCommunicationRepository.update(userOptin, userOptin).get()
        Assert.assertEquals(value, newUserOptin.getCommunicationId())

        UserOptinListOptions getOption = new UserOptinListOptions()
        getOption.setUserId(new UserId(userId))
        List<UserCommunication> userOptins = userCommunicationRepository.searchByUserId(new UserId(userId), null,
                null).get()
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

        userSecurityQuestion = userSecurityQuestionRepository.create(userSecurityQuestion).get()

        UserSecurityQuestion newUserSecurityQuestion =
                userSecurityQuestionRepository.get(userSecurityQuestion.getId()).get()
        Assert.assertEquals(userSecurityQuestion.getAnswerHash(), newUserSecurityQuestion.getAnswerHash())

        String value = UUID.randomUUID().toString()
        newUserSecurityQuestion.setAnswerHash(value)
        newUserSecurityQuestion = userSecurityQuestionRepository.update(newUserSecurityQuestion, userSecurityQuestion).get()
        Assert.assertEquals(newUserSecurityQuestion.getAnswerHash(), value)

        List<UserSecurityQuestion> securityQuestions = userSecurityQuestionRepository.
                searchByUserId(new UserId(userId), Integer.MAX_VALUE, 0).get()
        assert securityQuestions.size() != 0
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
        List<UserTosAgreement> userToses = userTosRepository.searchByUserIdAndTosId(new UserId(userId), new TosId("456L"),
                Integer.MAX_VALUE, 0).get()
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
        attempt.setUserSecurityQuestionId(new UserSecurityQuestionId("123L"))
        attempt.setUserAgent(UUID.randomUUID().toString())

        attempt = userSecurityQuestionAttemptRepository.create(attempt).get()

        UserSecurityQuestionVerifyAttempt newAttempt =
                userSecurityQuestionAttemptRepository.get(attempt.getId()).get()
        Assert.assertEquals(attempt.getIpAddress(), newAttempt.getIpAddress())

        UserSecurityQuestionAttemptListOptions option = new UserSecurityQuestionAttemptListOptions()
        option.setUserId(new UserId(userId))
        option.setUserSecurityQuestionId(new UserSecurityQuestionId("123L"))
        List<UserSecurityQuestionVerifyAttempt> attempts =
                userSecurityQuestionAttemptRepository.searchByUserIdAndSecurityQuestionId(new UserId(userId),
                        new UserSecurityQuestionId("123L"), Integer.MAX_VALUE, 0).get()
        assert attempts.size() != 0
    }

    @Test
    public void testDeviceRepository() {
        Device device = new Device()
        device.setSerialNumber(UUID.randomUUID().toString())
        device.setType(new DeviceTypeId("DK2"))
        device.setFirmwareVersion(UUID.randomUUID().toString())

        Device newDevice = deviceRepository.create(device).get()
        newDevice = deviceRepository.get((DeviceId)newDevice.id).get()

        assert  device.serialNumber == newDevice.serialNumber

        String newSerialNumber = UUID.randomUUID().toString()
        newDevice.setSerialNumber(newSerialNumber)
        device = deviceRepository.update(newDevice, newDevice).get()
        assert device.serialNumber == newSerialNumber

        device = deviceRepository.searchBySerialNumber(device.serialNumber).get()
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
        UserTFA userTeleCode = new UserTFA()
        userTeleCode.setUserId(new UserId(id))
        userTeleCode.setActive(true)
        userTeleCode.setExpiresBy(after30Mins)
        userTeleCode.setPersonalInfo(phoneNumber)
        userTeleCode.setSentLocale(new LocaleId('en_US'))
        userTeleCode.setTemplate('xxxxx')
        userTeleCode.setVerifyCode(UUID.randomUUID().toString())
        userTeleCode.setVerifyType('CALL')

        UserTFA newUserTeleCode = userTFARepository.create(userTeleCode).get()
        newUserTeleCode = userTFARepository.get((UserTFAId)newUserTeleCode.id).get()

        assert userTeleCode.personalInfo == newUserTeleCode.personalInfo

        UserPersonalInfoId newPhoneNumber = new UserPersonalInfoId(idGenerator.nextId())
        newUserTeleCode.setPersonalInfo(newPhoneNumber)
        userTeleCode = userTFARepository.update(newUserTeleCode, newUserTeleCode).get()
        assert userTeleCode.personalInfo == newPhoneNumber
    }

    @Test
    public void testUserTeleAttemptRepository() {
        def userTeleId = new UserTFAId(idGenerator.nextId().toString())
        def userId = new UserId(idGenerator.nextId())
        UserTFAAttempt userTeleAttempt = new UserTFAAttempt()
        userTeleAttempt.setVerifyCode(UUID.randomUUID().toString())
        userTeleAttempt.setClientId(new ClientId(123L))
        userTeleAttempt.setUserId(userId)
        userTeleAttempt.setIpAddress(UUID.randomUUID().toString())
        userTeleAttempt.setSucceeded(true)
        userTeleAttempt.setUserAgent(UUID.randomUUID().toString())
        userTeleAttempt.setUserTFAId(userTeleId)

        UserTFAAttempt newUserTeleAttempt = userTFAAttemptRepository.create(userTeleAttempt).get()
        newUserTeleAttempt = userTFAAttemptRepository.get((UserTFAAttemptId)newUserTeleAttempt.id).get()

        assert userTeleAttempt.ipAddress == newUserTeleAttempt.ipAddress

        String newIpAddress = UUID.randomUUID().toString()
        newUserTeleAttempt.setIpAddress(newIpAddress)
        userTeleAttempt = userTFAAttemptRepository.update(newUserTeleAttempt, newUserTeleAttempt).get()
        assert userTeleAttempt.ipAddress == newIpAddress

        List<UserTFAAttempt> results = userTFAAttemptRepository.searchByUserIdAndUserTFAId(userId, userTeleId, 100,
                0).get()
        assert results.size() != 0
    }

    @Test
    public void testUserTeleBackupCodeRepository() {
        def after30000Mins = new Date()
        use( TimeCategory ) {
            after30000Mins = (new Date()) + 30000.minutes
        }
        def userId = new UserId(idGenerator.nextId())
        UserTFABackupCode userTeleBackupCode = new UserTFABackupCode()
        userTeleBackupCode.setUserId(userId)
        userTeleBackupCode.setVerifyCode(UUID.randomUUID().toString())
        userTeleBackupCode.setActive(true)
        userTeleBackupCode.setExpiresBy(after30000Mins)
        UserTFABackupCode newUserTeleBackupCode =
                userTFABackupCodeRepository.create(userTeleBackupCode).get()
        newUserTeleBackupCode =
                userTFABackupCodeRepository.get((UserTFABackupCodeId)newUserTeleBackupCode.id).get()

        assert userTeleBackupCode.verifyCode == newUserTeleBackupCode.verifyCode

        String newVerifyCode = UUID.randomUUID().toString()
        newUserTeleBackupCode.setVerifyCode(newVerifyCode)
        userTeleBackupCode = userTFABackupCodeRepository.update(newUserTeleBackupCode, newUserTeleBackupCode).get()

        assert userTeleBackupCode.verifyCode == newVerifyCode

        List<UserTFABackupCode> results = userTFABackupCodeRepository.searchByUserId(userId, 100, 0).get()
        assert results.size() != 0
    }

    @Test
    public void testUserTeleBackupCodeAttemptRepository() {
        def userId = new UserId(idGenerator.nextId())
        UserTFABackupCodeAttempt attempt = new UserTFABackupCodeAttempt()
        attempt.setUserId(userId)
        attempt.setVerifyCode(UUID.randomUUID().toString())
        attempt.setUserAgent(UUID.randomUUID().toString())
        attempt.setClientId(new ClientId(123L))
        attempt.setIpAddress(UUID.randomUUID().toString())
        attempt.setSucceeded(true)

        UserTFABackupCodeAttempt newAttempt = userTFABackupCodeAttemptRepository.create(attempt).get()
        newAttempt = userTFABackupCodeAttemptRepository.get(newAttempt.id).get()

        assert newAttempt.verifyCode == attempt.verifyCode

        String newVerifyCode = UUID.randomUUID().toString()
        newAttempt.setVerifyCode(newVerifyCode)
        attempt = userTFABackupCodeAttemptRepository.update(newAttempt, newAttempt).get()

        assert attempt.verifyCode == newVerifyCode

        List<UserTFABackupCodeAttempt> results = userTFABackupCodeAttemptRepository.searchByUserId(userId, 100, 0).get()
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
        email.info = UUID.randomUUID().toString()
        ObjectMapper objectMapper = new ObjectMapper()
        userPersonalInfo.setValue(objectMapper.valueToTree(email))

        UserPersonalInfo newUserPersonalInfo = userPersonalInfoRepository.create(userPersonalInfo).get()
        newUserPersonalInfo = userPersonalInfoRepository.get(newUserPersonalInfo.id).get()

        assert newUserPersonalInfo.type == userPersonalInfo.type
    }

    @Test
    public void testOrganizationRepository() {
        UserId ownerId = new UserId(idGenerator.nextId())
        Organization organization = new Organization()
        organization.ownerId = ownerId
        Organization newOrganization = organizationRepository.create(organization).get()

        newOrganization = organizationRepository.get(newOrganization.id).get()

        assert organization.id == newOrganization.id

        List<Organization> organizationList = organizationRepository.searchByOwner(ownerId, Integer.MAX_VALUE, 0).get()

        assert organizationList.size() != 0
    }
}
