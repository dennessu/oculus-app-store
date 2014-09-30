// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.*;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.identity.spec.v1.model.migration.Company;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.ShareProfile;
import com.junbo.identity.spec.v1.model.migration.ShareProfileAvatar;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author dw
 */
public class IdentityModel {

    private IdentityModel() {

    }

    public static final String DefaultCountry = "US";
    public static final String DefaultCurrency = "USD";
    public static final String DefaultLocale = "en_US";

    public static Address DefaultAddress() throws Exception {
        Address address = new Address();
        address.setCity("Irvine");
        CountryId countryId = new CountryId();
        countryId.setValue(DefaultCountry);
        address.setCountryId(countryId);
        address.setPostalCode("92612");
        address.setStreet1("19800 MacArthur Blvd");
        return address;
    }

    public static UserDOB DefaultUserDob() throws Exception {
        Calendar ca = Calendar.getInstance();
        UserDOB userDOB = new UserDOB();
        userDOB.setInfo(new Date(ca.getTime().getYear() - 30, ca.getTime().getMonth(), ca.getTime().getDay()));
        return userDOB;
    }

    public static Country DefaultCountry() throws Exception {
        Country country = new Country();
        country.setCountryCode(DefaultCountry);
        CurrencyId currencyId = new CurrencyId();
        currencyId.setValue(DefaultCurrency);
        country.setDefaultCurrency(currencyId);
        LocaleId localeId = new LocaleId();
        localeId.setValue(DefaultLocale);
        country.setDefaultLocale(localeId);
        Map<String, CountryLocaleKey> locales = new HashMap<>();
        locales.put("en_US", new CountryLocaleKey() {{
            setShortName("USD_SHORT");
            setLongName("USD_LONG");
        }});
        country.setLocales(locales);
        List<RatingBoardId> ratingBoards = new ArrayList<>();
        country.setRatingBoards(ratingBoards);
        Map<String, SubCountryLocaleKeys> subCountryMap = new HashMap<>();
        SubCountryLocaleKeys subCountry1 = new SubCountryLocaleKeys();
        subCountry1.setLocales(new HashMap<String, SubCountryLocaleKey>() {{
            put("en_US", new SubCountryLocaleKey() {{
                setShortName("US_NY_SHORT");
                setLongName("US_NY_LONG");
            }});
        }});
        SubCountryLocaleKeys subCountry2 = new SubCountryLocaleKeys();
        subCountry2.setLocales(new HashMap<String, SubCountryLocaleKey>() {{
            put("en_US", new SubCountryLocaleKey() {{
                setShortName("US_CA_SHORT");
                setLongName("US_CA_LONG");
            }});
        }});
        subCountryMap.put("NY", subCountry1);
        subCountryMap.put("CA", subCountry2);
        country.setSubCountries(subCountryMap);
        List<LocaleId> localeIds = new ArrayList<>();
        country.setSupportedLocales(localeIds);
        Map<String, JsonNode> fe = new HashMap<>();
        country.setFutureExpansion(fe);
        return country;
    }

    public static Currency DefaultCurrency() throws Exception {
        Currency currency = new Currency();
        currency.setCurrencyCode(DefaultCurrency);
        Map<String, CurrencyLocaleKey> localeKeys = new HashMap<>();
        localeKeys.put("en_US", new CurrencyLocaleKey() {{
            setShortName("USD_SHORT");
            setLongName("USD_LONG");
        }});
        currency.setLocales(localeKeys);
        currency.setMinAuthAmount(new BigDecimal(1));
        currency.setNumberAfterDecimal(2);
        currency.setSymbol("$");
        currency.setSymbolPosition("BEFORE");
        return currency;
    }

    public static Email DefaultEmail() throws Exception {
        Email email = new Email();
        email.setInfo(RandomHelper.randomAlphabetic(10) + "@163.com");
        return email;
    }

    public static PhoneNumber DefaultPhoneNumber() throws Exception {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setInfo("8613061982418");
        return phoneNumber;
    }

    public static String DefaultPassword() throws Exception {
        String password = RandomHelper.randomAlphabetic(4).toLowerCase() +
                RandomHelper.randomNumeric(6) +
                RandomHelper.randomAlphabetic(4).toUpperCase();

        return password;
    }

    public static String DefaultPin() throws Exception {
        String pin = RandomHelper.randomNumeric(4);
        return pin;
    }

    public static Locale DefaultLocale() throws Exception {
        Locale locale = new Locale();
        locale.setLocaleCode(DefaultLocale);
        locale.setLocaleName("English(US)");
        locale.setLongName("English (United States)");
        locale.setShortName("English(US)");
        return locale;
    }

    public static User DefaultUser() throws Exception {
        User user = new User();
        user.setIsAnonymous(true);
        return user;
    }

    public static OculusInput DefaultOculusInput() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OculusInput input = new OculusInput();
        input.setCurrentId(RandomHelper.randomLong());
        input.setFirstName(RandomHelper.randomAlphabetic(10));
        input.setLastName(RandomHelper.randomAlphabetic(10));
        input.setEmail(RandomHelper.randomAlphabetic(10) + "@163.com");
        input.setUsername(RandomHelper.randomAlphabetic(15));
        input.setPassword("1:" + RandomHelper.randomAlphabetic(20) + ":" + RandomHelper.randomAlphabetic(30)
                + ":" + RandomHelper.randomAlphabetic(40));
        input.setGender(RandomGender());
        input.setDob(sdf.parse("1980-01-01 00:00:00"));
        input.setNickname(RandomHelper.randomAlphabetic(10));
        input.setTimezone(-8);
        input.setLanguage("en");
        input.setCreatedDate(sdf.parse("2013-02-07 05:30:32"));
        input.setUpdateDate(sdf.parse("2013-11-26 17:35:03"));
        input.setCompany(DefaultCompany());
        input.setProfile(DefaultShareProfile());
        input.setStatus(RandomMigrteUserStatus());
        input.setForceResetPassword(RandomHelper.randomBoolean());
        return input;
    }

    public static Company DefaultCompany() throws Exception {
        Company company = new Company();
        company.setCompanyId(RandomHelper.randomLong());
        company.setName(RandomHelper.randomAlphabetic(20));
        company.setAddress(RandomHelper.randomAlphabetic(40));
        company.setCity(RandomHelper.randomAlphabetic(30));
        company.setState(RandomHelper.randomAlphabetic(10));
        company.setPostalCode(RandomHelper.randomAlphabetic(10));
        company.setCountry("US");
        company.setPhone(RandomHelper.randomAlphabetic(30));
        company.setType(RandomMigrateCompanyType());
        company.setIsAdmin(RandomHelper.randomBoolean());
        return company;
    }

    public static ShareProfile DefaultShareProfile() throws Exception {
        ShareProfile shareProfile = new ShareProfile();
        shareProfile.setHeadline(RandomHelper.randomAlphabetic(10));
        shareProfile.setSummary(RandomHelper.randomAlphabetic(10));
        shareProfile.setUrl(RandomHelper.randomAlphabetic(10));
        ShareProfileAvatar avatar = new ShareProfileAvatar();
        avatar.setHref(RandomHelper.randomAlphabetic(100));
        shareProfile.setAvatar(avatar);
        return shareProfile;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoAddress() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.ADDRESS.name());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(DefaultAddress()));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoDob() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.DOB.name());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(DefaultUserDob()));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoEmail(String email) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.EMAIL.toString());
        Email tmpEmail = new Email();
        tmpEmail.setInfo(email);
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(tmpEmail));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoEmail() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.EMAIL.name());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(DefaultEmail()));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoPhone() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.PHONE.name());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(DefaultPhoneNumber()));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUnicodeUserPersonalInfoName() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        UserName userName = new UserName();
        userName.setFamilyName("赵");
        userName.setGivenName("云龙");
        userPersonalInfo.setType(UserPersonalInfoType.NAME.toString());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(userName));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoUsername() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        UserLoginName loginName = new UserLoginName();
        loginName.setUserName(RandomHelper.randomAlphabetic(15));
        userPersonalInfo.setType(UserPersonalInfoType.USERNAME.toString());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(loginName));
        return userPersonalInfo;
    }

    public static Group DefaultGroup(OrganizationId organizationId) throws Exception {
        Group group = new Group();
        group.setName(RandomHelper.randomAlphabetic(15));
        group.setActive(true);
        group.setOrganizationId(organizationId);
        return group;
    }

    public static Organization DefaultOrganization() throws Exception {
        Organization org = new Organization();
        org.setName(RandomHelper.randomAlphabetic(20));
        org.setIsValidated(false);
        return org;
    }

    public static UserCredential DefaultUserCredential(UserId userId, String password) throws Exception {
        return DefaultUserCredential(userId, null, password);
    }

    public static UserCredential DefaultUserCredential(UserId userId, String oldPassword, String password) throws Exception {
        UserCredential userCredential = new UserCredential();
        userCredential.setUserId(userId);
        userCredential.setCurrentPassword(oldPassword);
        userCredential.setValue(password);
        userCredential.setType("PASSWORD");
        userCredential.setChangeAtNextLogin(false);
        return userCredential;
    }

    public static UserCredential DefaultUserPin(UserId userId, String oldPassword, String pin) throws Exception {
        UserCredential userCredential = new UserCredential();
        userCredential.setUserId(userId);
        userCredential.setCurrentPassword(oldPassword);
        userCredential.setValue(pin);
        userCredential.setType("PIN");
        userCredential.setChangeAtNextLogin(false);
        return userCredential;
    }

    public static UserCredentialVerifyAttempt DefaultUserCredentialAttempts(String userName, String password)
            throws Exception {
        UserCredentialVerifyAttempt ucva = new UserCredentialVerifyAttempt();
        ucva.setUsername(userName);
        ucva.setValue(password);
        ucva.setType("PASSWORD");
        ucva.setIpAddress(RandomHelper.randomIP());
        return ucva;
    }

    public static UserCredentialVerifyAttempt DefaultUserPinAttempts(String username, String pin) throws Exception {
        UserCredentialVerifyAttempt ucva = new UserCredentialVerifyAttempt();
        ucva.setUsername(username);
        ucva.setValue(pin);
        ucva.setType("PIN");
        ucva.setIpAddress(RandomHelper.randomIP());
        return ucva;
    }

    public static UserSecurityQuestion DefaultUserSecurityQuestion() throws Exception {
        UserSecurityQuestion usq = new UserSecurityQuestion();
        usq.setSecurityQuestion(RandomHelper.randomAlphabetic(100));
        usq.setAnswer(RandomHelper.randomAlphabetic(100));
        return usq;
    }

    public static UserSecurityQuestionVerifyAttempt DefaultUserSecurityQuestionVerifyAttempt(UserId userId,
                                                                                             UserSecurityQuestionId securityQuestionId, String value) throws Exception {
        UserSecurityQuestionVerifyAttempt attempt = new UserSecurityQuestionVerifyAttempt();
        attempt.setUserId(userId);
        attempt.setUserSecurityQuestionId(securityQuestionId);
        attempt.setValue(value);
        return attempt;
    }

    public static UserTFA DefaultUserTFA() throws Exception {
        UserTFA userTFA = new UserTFA();
        userTFA.setVerifyType(RandomTFAVerifyType());
        userTFA.setTemplate(RandomHelper.randomAlphabetic(100));
        return userTFA;
    }

    public static UserVAT DefaultUserVat() throws Exception {
        UserVAT userVAT = new UserVAT();
        userVAT.setVatNumber("IE6388047V");
        return userVAT;
    }

    public static DeviceType DefaultDeviceType(List<DeviceTypeId> deviceTypeIds) throws Exception {
        DeviceType deviceType = new DeviceType();
        deviceType.setInstructionManual(RandomHelper.randomAlphabetic(100));
        deviceType.setTypeCode(RandomDeviceTypeCode());
        deviceType.setComponentTypes(deviceTypeIds);
        Map<String, DeviceSoftware> availableSoftwareMap = new HashMap<>();
        DeviceSoftware deviceSoftware = new DeviceSoftware();
        SoftwareObject dev = new SoftwareObject();
        dev.setHref(RandomHelper.randomAlphabetic(15));
        dev.setVersion(RandomHelper.randomAlphabetic(15));
        deviceSoftware.setDev(dev);
        SoftwareObject stable = new SoftwareObject();
        stable.setHref(RandomHelper.randomAlphabetic(15));
        stable.setVersion(RandomHelper.randomAlphabetic(15));
        deviceSoftware.setStable(stable);
        availableSoftwareMap.put(RandomHelper.randomAlphabetic(15), deviceSoftware);
        deviceType.setAvailableSoftware(availableSoftwareMap);
        return deviceType;
    }

    public static ErrorInfo DefaultErrorInfo() throws Exception {
        ErrorInfo errorInfo = new ErrorInfo();
        String errorCode = RandomHelper.randomNumeric(3) + "." + RandomHelper.randomNumeric(2);
        errorInfo.setErrorIdentifier(errorCode);
        Map<String, JsonNode> locales = new HashMap<>();

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorInformation(RandomHelper.randomAlphabetic(10));
        errorDetail.setErrorSummary(RandomHelper.randomAlphabetic(10));
        errorDetail.setErrorTitle(RandomHelper.randomAlphabetic(10));
        errorDetail.setSupportLink(RandomHelper.randomAlphabetic(15));
        locales.put("en_US", JsonHelper.ObjectToJsonNode(errorDetail));
        errorInfo.setLocales(locales);

        return errorInfo;
    }


    public static Communication DefaultCommunication() throws Exception {
        Communication communication = new Communication();
        List<CountryId> regions = new ArrayList<>();
        regions.add(new CountryId("US"));
        regions.add(new CountryId("CN"));
        communication.setRegions(regions);

        List<LocaleId> translations = new ArrayList<>();
        translations.add(new LocaleId("en_US"));
        translations.add(new LocaleId("zh_CN"));
        communication.setTranslations(translations);

        CommunicationLocale communicationLocale = new CommunicationLocale();
        communicationLocale.setDescription(RandomHelper.randomAlphabetic(100));
        communicationLocale.setName(RandomHelper.randomAlphabetic(100));

        Map<String, JsonNode> locales = new HashMap<>();
        locales.put("en_US", JsonHelper.ObjectToJsonNode(communicationLocale));
        locales.put("zh_CN", JsonHelper.ObjectToJsonNode(communicationLocale));
        communication.setLocales(locales);

        return communication;
    }


    public static String RandomGender() {
        List<Object> array = new ArrayList<>();
        array.add("MALE");
        array.add("FEMALE");
        return RandomHelper.randomValueFromList(array).toString();
    }

    public static String RandomDeviceTypeCode() {
        List<Object> array = new ArrayList<>();
        array.add(DeviceTypeCode.CV1.name());
        array.add(DeviceTypeCode.DK1.name());
        array.add(DeviceTypeCode.DK2.name());
        array.add(DeviceTypeCode.DK2_CAMERA.name());
        array.add(DeviceTypeCode.DKHD.name());
        array.add(DeviceTypeCode.HMD.name());
        array.add(DeviceTypeCode.NOTE4.name());
        return RandomHelper.randomValueFromList(array).toString();
    }

    public static String RandomMigrteUserStatus() {
        List<Object> array = new ArrayList<>();
        array.add(MigrateUserStatus.ACTIVE.name());
        array.add(MigrateUserStatus.ARCHIVE.name());
        array.add(MigrateUserStatus.PENDING.name());
        array.add(MigrateUserStatus.PENDING_EMAIL_VERIFICATION.name());
        array.add(MigrateUserStatus.VERIFIED.name());
        return RandomHelper.randomValueFromList(array).toString();
    }

    public static String RandomMigrateCompanyType() {
        List<Object> array = new ArrayList<>();
        array.add(MigrateCompanyType.CORPORATE.name());
        array.add(MigrateCompanyType.INDIVIDUAL.name());
        return RandomHelper.randomValueFromList(array).toString();
    }

    public static String RandomTFAVerifyType() {
        List<Object> array = new ArrayList<>();
        array.add(TFAVerifyType.CALL.name());
        array.add(TFAVerifyType.EMAIL.name());
        array.add(TFAVerifyType.SMS.name());
        return RandomHelper.randomValueFromList(array).toString();
    }

    /**
     * copied from com\junbo\identity\data\identifiable\UserPersonalInfoType.groovy.
     */
    public static enum UserPersonalInfoType {
        ADDRESS,
        DOB,
        DRIVERS_LICENSE,
        EMAIL,
        GENDER,
        GOVERNMENT_ID,
        NAME,
        PASSPORT,
        PHONE,
        QQ,
        SMS,
        WHATSAPP,
        WIPED,
        USERNAME
    }

    /**
     * copied from com\junbo\identity\rest\resource\v1\MigrationResourceImpl.groovy.
     */
    public static enum MigrateUserStatus {
        ACTIVE,
        ARCHIVE,
        PENDING,
        PENDING_EMAIL_VERIFICATION,
        VERIFIED
    }

    public static enum DeviceTypeCode {
        DK1,
        DKHD,
        DK2,
        CV1,
        HMD,
        DK2_CAMERA,
        NOTE4
    }

    /**
     * migration only support two groups.
     */
    public static enum MigrateCompanyGroup {
        admin,
        developer
    }

    /**
     * copied from com/junbo/identity/rest/resource/v1/MigrationResourceImpl.groovy.
     */
    public static enum MigrateCompanyType {
        CORPORATE,
        INDIVIDUAL
    }

    /**
     * TFA only support 3 types as below.
     */
    public static enum TFAVerifyType {
        CALL,
        EMAIL,
        SMS
    }
}
