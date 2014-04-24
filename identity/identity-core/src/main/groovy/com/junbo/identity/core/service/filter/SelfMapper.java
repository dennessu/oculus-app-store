/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.filter;

import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;

/**
 * Created by kg on 3/26/2014.
 */
@Mapper
public interface SelfMapper {

    User filterUser(User user, MappingContext context);

    User mergeUser(User source, User base, MappingContext context);

    Group filterGroup(Group group, MappingContext context);

    Group mergeGroup(Group source, Group base, MappingContext context);

    SecurityQuestion filterSecurityQuestion(SecurityQuestion securityQuestion, MappingContext context);

    SecurityQuestion mergeSecurityQuestion(SecurityQuestion source, SecurityQuestion base, MappingContext context);

    UserAuthenticator filterUserAuthenticator(UserAuthenticator userAuthenticator, MappingContext context);

    UserAuthenticator mergeUserAuthenticator(UserAuthenticator source, UserAuthenticator base, MappingContext context);

    UserDevice filterUserDevice(UserDevice userDevice, MappingContext context);

    UserDevice mergeUserDevice(UserDevice source, UserDevice base, MappingContext context);

    UserGroup filterUserGroup(UserGroup userGroup, MappingContext context);

    UserGroup mergeUserGroup(UserGroup source, UserGroup base, MappingContext context);

    UserCredentialVerifyAttempt filterUserCredentialVerifyAttempt(
            UserCredentialVerifyAttempt userLoginAttempt, MappingContext context);

    UserCredentialVerifyAttempt mergeUserCredentialVerifyAttempt(
            UserCredentialVerifyAttempt source, UserCredentialVerifyAttempt base, MappingContext context);

    UserCommunication filterUserOptin(UserCommunication userOptin, MappingContext context);

    UserCommunication mergeUserOptin(UserCommunication source, UserCommunication base, MappingContext context);

    UserPassword filterUserPassword(UserPassword userPassword, MappingContext context);

    UserPassword mergeUserPassword(UserPassword source, UserPassword base, MappingContext context);

    UserPhoneNumber filterUserPhoneNumber(UserPhoneNumber userPhoneNumber, MappingContext context);

    UserPhoneNumber mergeUserPhoneNumber(UserPhoneNumber source, UserPhoneNumber base, MappingContext context);

    UserPin filterUserPin(UserPin userPin, MappingContext context);

    UserPin mergeUserPin(UserPin source, UserPin base, MappingContext context);

    UserSecurityQuestion filterUserSecurityQuestion(UserSecurityQuestion userSecurityQuestion, MappingContext context);

    UserSecurityQuestion mergeUserSecurityQuestion(UserSecurityQuestion source,
                                                   UserSecurityQuestion base, MappingContext context);

    UserSecurityQuestionVerifyAttempt filterUserSecurityQuestionAttempt(UserSecurityQuestionVerifyAttempt entity,
                                                                  MappingContext mappingContext);

    UserSecurityQuestionVerifyAttempt mergeUserSecurityQuestionAttempt(UserSecurityQuestionVerifyAttempt source,
                                             UserSecurityQuestionVerifyAttempt base, MappingContext mappingContext);

    UserEmail filterUserEmail(UserEmail userEmail, MappingContext context);

    UserEmail mergeUserEmail(UserEmail source, UserEmail base, MappingContext context);

    UserTosAgreement filterUserTos(UserTosAgreement userTos, MappingContext context);

    UserTosAgreement mergeUserTos(UserTosAgreement source, UserTosAgreement base, MappingContext context);

    Device filterDevice(Device device, MappingContext context);

    Device mergeDevice(Device source, Device base, MappingContext context);

    Role filterRole(Role role, MappingContext context);

    Role mergeRole(Role source, Role base, MappingContext context);

    RoleAssignment filterRoleAssignment(RoleAssignment roleAssignment, MappingContext context);

    RoleAssignment mergeRoleAssignment(RoleAssignment source, RoleAssignment base, MappingContext context);

    Tos filterTos(Tos tos, MappingContext context);

    Tos mergeTos(Tos source, Tos base, MappingContext context);

    UserCredential filterUserCredential(UserCredential userCredential, MappingContext context);

    UserCredential mergeUserCredential(UserCredential source, UserCredential base, MappingContext context);

    UserPii filterUserPii(UserPii userPii, MappingContext context);

    UserPii mergeUserPii(UserPii source, UserPii base, MappingContext context);

    Address filterAddress(Address address, MappingContext context);

    Address mergeAddress(Address source, Address base, MappingContext context);

    UserTeleCode filterUserTeleCode(UserTeleCode userTeleCode, MappingContext context);

    UserTeleCode mergeUserTeleCode(UserTeleCode source, UserTeleCode base, MappingContext context);
}
