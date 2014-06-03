/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.filter;

import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;

/**
 * Created by kg on 3/26/2014.
 */
@Mapper(uses = {
        JsonNodeSelfMapper.class
})
public interface SelfMapper {

    User filterUser(User user, MappingContext context);
    User mergeUser(User source, User base, MappingContext context);

    Group filterGroup(Group group, MappingContext context);
    Group mergeGroup(Group source, Group base, MappingContext context);

    UserAuthenticator filterUserAuthenticator(UserAuthenticator userAuthenticator, MappingContext context);
    UserAuthenticator mergeUserAuthenticator(UserAuthenticator source, UserAuthenticator base, MappingContext context);

    UserGroup filterUserGroup(UserGroup userGroup, MappingContext context);
    UserGroup mergeUserGroup(UserGroup source, UserGroup base, MappingContext context);

    UserCredentialVerifyAttempt filterUserCredentialVerifyAttempt(
            UserCredentialVerifyAttempt userLoginAttempt, MappingContext context);
    UserCredentialVerifyAttempt mergeUserCredentialVerifyAttempt(
            UserCredentialVerifyAttempt source, UserCredentialVerifyAttempt base, MappingContext context);

    UserCommunication filterUserCommunication(UserCommunication userOptin, MappingContext context);
    UserCommunication mergeUserCommunication(UserCommunication source, UserCommunication base, MappingContext context);

    UserPassword filterUserPassword(UserPassword userPassword, MappingContext context);
    UserPassword mergeUserPassword(UserPassword source, UserPassword base, MappingContext context);

    UserPin filterUserPin(UserPin userPin, MappingContext context);
    UserPin mergeUserPin(UserPin source, UserPin base, MappingContext context);

    UserSecurityQuestion filterUserSecurityQuestion(UserSecurityQuestion userSecurityQuestion, MappingContext context);
    UserSecurityQuestion mergeUserSecurityQuestion(UserSecurityQuestion source,
                                                   UserSecurityQuestion base, MappingContext context);

    UserSecurityQuestionVerifyAttempt filterUserSecurityQuestionAttempt(UserSecurityQuestionVerifyAttempt entity,
                                                                  MappingContext mappingContext);
    UserSecurityQuestionVerifyAttempt mergeUserSecurityQuestionAttempt(UserSecurityQuestionVerifyAttempt source,
                                             UserSecurityQuestionVerifyAttempt base, MappingContext mappingContext);

    UserTosAgreement filterUserTos(UserTosAgreement userTos, MappingContext context);
    UserTosAgreement mergeUserTos(UserTosAgreement source, UserTosAgreement base, MappingContext context);

    Device filterDevice(Device device, MappingContext context);
    Device mergeDevice(Device source, Device base, MappingContext context);

    Tos filterTos(Tos tos, MappingContext context);
    Tos mergeTos(Tos source, Tos base, MappingContext context);

    UserCredential filterUserCredential(UserCredential userCredential, MappingContext context);
    UserCredential mergeUserCredential(UserCredential source, UserCredential base, MappingContext context);

    UserTFA filterUserTeleCode(UserTFA userTeleCode, MappingContext context);
    UserTFA mergeUserTeleCode(UserTFA source, UserTFA base, MappingContext context);

    UserTFAAttempt filterUserTeleAttempt(UserTFAAttempt attempt, MappingContext context);
    UserTFAAttempt mergeUserTeleAttempt(UserTFAAttempt source, UserTFAAttempt base, MappingContext context);

    UserTeleBackupCode filterUserTeleBackupCode(UserTeleBackupCode userTeleBackupCode, MappingContext context);
    UserTeleBackupCode mergeUserTeleBackupCode(UserTeleBackupCode source, UserTeleBackupCode base,
                                               MappingContext context);

    UserTeleBackupCodeAttempt filterUserTeleBackupCodeAttempt(UserTeleBackupCodeAttempt userTeleBackupCodeAttempt,
                                                              MappingContext context);
    UserTeleBackupCodeAttempt mergeUserTeleBackupCodeAttempt(UserTeleBackupCodeAttempt source,
                                               UserTeleBackupCodeAttempt base, MappingContext context);

    DeviceType filterDeviceType(DeviceType deviceType, MappingContext context);
    DeviceType mergeDeviceType(DeviceType source, DeviceType base, MappingContext context);

    Country filterCountry(Country country, MappingContext context);
    Country mergeCountry(Country source, Country base, MappingContext context);

    Currency filterCurrency(Currency currency, MappingContext context);
    Currency mergeCurrency(Currency source, Currency base, MappingContext context);

    Locale filterLocale(Locale locale, MappingContext context);
    Locale mergeLocale(Locale source, Locale base, MappingContext context);

    PIType filterPIType(PIType piType, MappingContext context);
    PIType mergePIType(PIType source, PIType base, MappingContext context);

    Communication filterCommunication(Communication communication, MappingContext context);
    Communication mergeCommunication(Communication source, Communication base, MappingContext context);

    UserPersonalInfo filterUserPersonalInfo(UserPersonalInfo userPersonalInfo, MappingContext context);
    UserPersonalInfo mergeUserPersonalInfo(UserPersonalInfo source, UserPersonalInfo base, MappingContext context);

    Organization filterOrganization(Organization organization, MappingContext context);
    Organization mergeOrganization(Organization source, Organization base, MappingContext context);
}
