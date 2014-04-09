/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.mapper;

import com.junbo.identity.data.entity.device.DeviceEntity;
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.data.entity.group.GroupEntity;
import com.junbo.identity.data.entity.user.*;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.identity.spec.model.users.*;
import com.junbo.identity.spec.model.users.UserName;
import com.junbo.identity.spec.model.users.UserOptin;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;

/**
 * Model Mapper for wrap entity to list, vice versa.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {
    Group toGroup(GroupEntity entity, MappingContext context);
    GroupEntity toGroup(Group entity, MappingContext context);

    @Mappings({
            @Mapping(source = "strength", target = "strength", explicitMethod = "toUserPasswordStrength",
                    bidirectional = false),
    })
    UserPassword toUserPassword(UserPasswordEntity entity, MappingContext context);
    @Mappings({
            @Mapping(source = "strength", target = "strength", explicitMethod = "toUserPasswordStrength")
    })
    UserPasswordEntity toUserPassword(UserPassword entity, MappingContext context);

    UserPin toUserPin(UserPinEntity entity, MappingContext context);
    UserPinEntity toUserPin(UserPin entity, MappingContext context);

    SecurityQuestion toSecurityQuestion(SecurityQuestionEntity entity, MappingContext context);
    SecurityQuestionEntity toSecurityQuestion(SecurityQuestion entity, MappingContext context);

    UserEntity toUser(User user, MappingContext context);
    User toUser(UserEntity userEntity, MappingContext context);

    UserDevice toUserDevice(UserDeviceEntity entity, MappingContext context);
    UserDeviceEntity toUserDevice(UserDevice entity, MappingContext context);

    UserAuthenticator toUserAuthenticator(UserAuthenticatorEntity entity, MappingContext context);
    UserAuthenticatorEntity toUserAuthenticator(UserAuthenticator userFederation, MappingContext context);

    UserEmail toUserEmail(UserEmailEntity entity, MappingContext context);
    UserEmailEntity toUserEmail(UserEmail entity, MappingContext context);

    UserGroup toUserGroup(UserGroupEntity entity, MappingContext context);
    UserGroupEntity toUserGroup(UserGroup entity, MappingContext context);

    UserCredentialVerifyAttempt toUserCredentialVerifyAttempt(
            UserCredentialVerifyAttemptEntity entity, MappingContext context);
    UserCredentialVerifyAttemptEntity toUserCredentialVerifyAttempt(
            UserCredentialVerifyAttempt entity, MappingContext context);

    UserOptin toUserOptin(UserOptinEntity entity, MappingContext context);
    UserOptinEntity toUserOptin(UserOptin entity, MappingContext context);

    UserPhoneNumber toUserPhoneNumber(UserPhoneNumberEntity entity, MappingContext context);
    UserPhoneNumberEntity toUserPhoneNumber(UserPhoneNumber entity, MappingContext context);

    UserSecurityQuestion toUserSecurityQuestion(UserSecurityQuestionEntity entity, MappingContext context);
    UserSecurityQuestionEntity toUserSecurityQuestion(UserSecurityQuestion entity, MappingContext context);

    UserTos toUserTos(UserTosEntity entity, MappingContext context);
    UserTosEntity toUserTos(UserTos entity, MappingContext context);

    UserName toUserName(UserNameEntity entity, MappingContext context);
    UserNameEntity toUserName(UserName entity, MappingContext context);

    UserSecurityQuestionAttempt toUserSecurityQuestionAttempt(UserSecurityQuestionAttemptEntity entity,
                                                              MappingContext context);
    UserSecurityQuestionAttemptEntity toUserSecurityQuestionAttempt(UserSecurityQuestionAttempt entity,
                                                              MappingContext context);

    Device toDevice(DeviceEntity entity, MappingContext context);
    DeviceEntity toDevice(Device entity, MappingContext context);

    Tos toTos(TosEntity entity, MappingContext context);
    TosEntity toTos(Tos entity, MappingContext context);


    UserPassword credentialToPassword(UserCredential userCredential);
    UserCredential passwordToCredential(UserPassword password);

    UserPin credentialToPin(UserCredential userCredential);
    UserCredential pinToCredential(UserPin userPin);
}
