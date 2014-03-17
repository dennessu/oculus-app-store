/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.mapper;

import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.data.entity.group.GroupEntity;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import com.junbo.identity.data.entity.user.*;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.identity.spec.model.users.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;

/**
 * Model Mapper for wrap entity to model, vice versa.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {
    Group toGroup(GroupEntity entity, MappingContext context);
    GroupEntity toGroup(Group entity, MappingContext context);

    UserPassword toUserPassword(UserPasswordEntity entity, MappingContext context);
    UserPasswordEntity toUserPassword(UserPassword entity, MappingContext context);

    UserPin toUserPIN(UserPINEntity entity, MappingContext context);
    UserPINEntity toUserPIN(UserPin entity, MappingContext context);

    SecurityQuestion toSecurityQuestion(SecurityQuestionEntity entity, MappingContext context);
    SecurityQuestionEntity toSecurityQuestion(SecurityQuestion entity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false)
    })
    UserEntity toUser(User user, MappingContext context);
    @Mappings({
            @Mapping(source = "password", excluded = true, bidirectional = false)
    })
    User toUser(UserEntity userEntity, MappingContext context);

    UserDevice toUserDevice(UserDeviceEntity entity, MappingContext context);
    UserDeviceEntity toUserDevice(UserDevice entity, MappingContext context);

    UserAuthenticator toUserAuthenticator(UserAuthenticatorEntity entity, MappingContext context);
    UserAuthenticatorEntity toUserAuthenticator(UserAuthenticator userFederation, MappingContext context);

    UserEmail toUserEmail(UserEmailEntity entity, MappingContext context);
    UserEmailEntity toUserEmail(UserEmail entity, MappingContext context);

    UserGroup toUserGroup(UserGroupEntity entity, MappingContext context);
    UserGroupEntity toUserGroup(UserGroup entity, MappingContext context);

    LoginAttempt toUserLoginAttempt(UserLoginAttemptEntity entity, MappingContext context);
    UserLoginAttemptEntity toUserLoginAttempt(LoginAttempt entity, MappingContext context);

    UserOptin toUserOptin(UserOptinEntity entity, MappingContext context);
    UserOptinEntity toUserOptin(UserOptin entity, MappingContext context);

    UserPhoneNumber toUserPhoneNumber(UserPhoneNumberEntity entity, MappingContext context);
    UserPhoneNumberEntity toUserPhoneNumber(UserPhoneNumber entity, MappingContext context);

    UserSecurityQuestion toUserSecurityQuestion(UserSecurityQuestionEntity entity, MappingContext context);
    UserSecurityQuestionEntity toUserSecurityQuestion(UserSecurityQuestion entity, MappingContext context);

    UserTos toUserTos(UserTosEntity entity, MappingContext context);

    UserTosEntity toUserTos(UserTos entity, MappingContext context);

    @Mappings({
            @Mapping(source = "allowedCharacterSet", target = "allowedCharacterSet", excluded = false,
                    bidirectional = false, explicitMethod = "jsonToListString"),
            @Mapping(source = "notAllowedCharacterSet", target = "notAllowedCharacterSet", excluded = false,
                    bidirectional = false, explicitMethod = "jsonToListString"),
            @Mapping(source = "passwordRuleDetails", target = "passwordRuleDetails", excluded = false,
                    bidirectional = false),
            @Mapping(source = "passwordStrength", target = "passwordStrength",
                    explicitMethod = "toUserPasswordStrength")
    })
    PasswordRule toPasswordRule(PasswordRuleEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "allowedCharacterSet", target = "allowedCharacterSet", excluded = false,
                    bidirectional = false, explicitMethod = "listStringToJson"),
            @Mapping(source = "notAllowedCharacterSet", target = "notAllowedCharacterSet", excluded = false,
                    bidirectional = false, explicitMethod = "listStringToJson"),
            @Mapping(source = "passwordRuleDetails", target = "passwordRuleDetails", excluded = false,
                    bidirectional = false),
            @Mapping(source = "passwordStrength", target = "passwordStrength",
                    explicitMethod = "toUserPasswordStrength")
    })
    PasswordRuleEntity toPasswordRule(PasswordRule passwordRule, MappingContext context);

}
