/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import com.junbo.identity.data.entity.app.AppEntity;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import com.junbo.identity.data.entity.user.*;
import com.junbo.identity.spec.model.app.App;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.identity.spec.model.user.*;

/**
 * Model Mapper for wrap entity to model, vice versa.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {
    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
            @Mapping(source = "status", target = "status", explicitMethod = "toUserStatus")
    })
    UserEntity toUserEntity(User user, MappingContext context);

    @Mappings({
            @Mapping(source = "password", excluded = true, bidirectional = false),
            @Mapping(source = "status", target = "status", explicitMethod = "toUserStatus")
    })
    User toUser(UserEntity userEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
            @Mapping(source = "dateOfBirth", target = "dob", excluded = false, bidirectional = false),
            @Mapping(source = "type", target = "type", explicitMethod = "toUserProfileType")
    })
    UserProfileEntity toUserProfileEntity(UserProfile userProfile, MappingContext context);

    @Mappings({
            @Mapping(source = "dob", target = "dateOfBirth", excluded = false, bidirectional = false),
            @Mapping(source = "type", target = "type", explicitMethod = "toUserProfileType")
    })
    UserProfile toUserProfile(UserProfileEntity userProfileEntity, MappingContext context);

    AppEntity toAppEntity(App app, MappingContext context);

    App toApp(AppEntity appEntity, MappingContext context);

    UserDeviceProfile toUserDeviceProfile(UserDeviceProfileEntity entity, MappingContext context);

    UserDeviceProfileEntity toUserDeviceProfile(UserDeviceProfile entity, MappingContext context);

    UserFederation toUserFederation(UserFederationEntity entity, MappingContext context);

    UserFederationEntity toUserFederation(UserFederation userFederation, MappingContext context);

    UserOptIn toUserOptIn(UserOptInEntity entity, MappingContext context);

    UserOptInEntity toUserOptIn(UserOptIn entity, MappingContext context);

    @Mappings({
            @Mapping(source = "tosAcceptanceUrl", target = "tos")
    })
    UserTosAcceptance toUserTosAcceptance(UserTosAcceptanceEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "tos", target = "tosAcceptanceUrl")
    })
    UserTosAcceptanceEntity toUserTosAcceptance(UserTosAcceptance entity, MappingContext context);

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
