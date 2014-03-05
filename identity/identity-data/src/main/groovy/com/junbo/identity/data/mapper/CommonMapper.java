/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.id.*;
import com.junbo.common.util.EnumRegistry;
import com.junbo.identity.data.entity.user.UserPasswordStrength;
import com.junbo.identity.data.entity.user.UserProfileType;
import com.junbo.identity.data.entity.user.UserStatus;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.password.PasswordRuleDetail;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.List;

/**
 * Common Mapper for enum to string, vice versa.
 */
public class CommonMapper {
    private ObjectMapper objectMapper = new ObjectMapper();

    public Short explicitMethod_toUserPasswordStrength(String passwordStrength) {
        if(StringUtils.isEmpty(passwordStrength)) {
            return null;
        }
        try {
            return UserPasswordStrength.valueOf(UserPasswordStrength.class, passwordStrength).getId();
        }
        catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(passwordStrength, "UserPasswordStrength").exception();
        }
    }

    public String explicitMethod_toUserPasswordStrength(Short userPasswordStrength) {
        if(userPasswordStrength == null) {
            return null;
        }
        else {
            return EnumRegistry.resolve(userPasswordStrength, UserPasswordStrength.class).toString();
        }
    }

    public Short explicitMethod_toUserStatus(String userStatus) {
        if(!StringUtils.isEmpty(userStatus)) {
            try {
                return UserStatus.valueOf(UserStatus.class, userStatus).getId();
            }
            catch (Exception e) {
                throw AppErrors.INSTANCE.enumConversionError(userStatus, "UserStatus").exception();
            }
        }
        else {
            return null;
        }
    }

    public String explicitMethod_toUserStatus(Short userStatus) {
        if(userStatus == null) {
            return null;
        }
        else {
            return EnumRegistry.resolve(userStatus, UserStatus.class).toString();
        }
    }

    public Short explicitMethod_toUserProfileType(String userProfileType) {
        if(!StringUtils.isEmpty(userProfileType)) {
            try {
                return UserProfileType.valueOf(UserProfileType.class, userProfileType).getId();
            }
            catch (Exception e) {
                throw AppErrors.INSTANCE.enumConversionError(userProfileType, "UserProfileType").exception();
            }
        }
        else {
            return null;
        }
    }

    public String explicitMethod_toUserProfileType(Short userProfileType) {
        if(userProfileType == null) {
            return null;
        }
        else {
            return EnumRegistry.resolve(userProfileType, UserProfileType.class).toString();
        }
    }

    public Integer toInteger(Integer resourceAge) {
        return resourceAge;
    }

    public Long toUserId(UserId user) {
        if(user == null) {
            return null;
        }
        return user.getValue();
    }

    public UserId toUserId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserId(id);
    }

    public Long toUserDeviceProfileId(UserDeviceProfileId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserDeviceProfileId toUserDeviceProfileId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserDeviceProfileId(id);
    }

    public Long toDeviceId(DeviceId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public DeviceId toDeviceId(Long id) {
        if(id == null) {
            return null;
        }
        return new DeviceId(id);
    }

    public AppId toAppId(Long id) {
        if(id == null) {
            return null;
        }
        return new AppId(id);
    }

    public Long toAppId(AppId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserFederationId toUserFederationId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserFederationId(id);
    }

    public Long toUserFederationId(UserFederationId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserOptInId toUserOptInId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserOptInId(id);
    }

    public Long toUserOptInId(UserOptInId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserProfileId toUserProfileId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserProfileId(id);
    }

    public Long toUserProfileId(UserProfileId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTosAcceptanceId toUserTosAcceptanceId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserTosAcceptanceId(id);
    }

    public Long toUserTosAcceptanceId(UserTosAcceptanceId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public Long toPasswordRuleId(PasswordRuleId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public PasswordRuleId toPasswordRuleId(Long id) {
        if(id == null) {
            return null;
        }
        return new PasswordRuleId(id);
    }

    public List<String> explicitMethod_jsonToListString(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() { });
        }
        catch (Exception e) {
            throw AppErrors.INSTANCE.serializerError(json).exception();
        }
    }

    public List<PasswordRuleDetail> jsonToListPasswordRuleDetails(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<PasswordRuleDetail>>() { });
        }
        catch (Exception e) {
            throw AppErrors.INSTANCE.serializerError(json).exception();
        }
    }

    public String explicitMethod_listStringToJson(List<String> strings) {
        try {
            StringWriter sw = new StringWriter();
            objectMapper.writeValue(sw, strings);
            return sw.toString();
        }
        catch (Exception e) {
            throw AppErrors.INSTANCE.deSerializerError().exception();
        }
    }

    public String listPasswordRuleDetailsToJson(List<PasswordRuleDetail> passwordRuleDetails) {
        try {
            StringWriter sw = new StringWriter();
            objectMapper.writeValue(sw, passwordRuleDetails);
            return sw.toString();
        }
        catch (Exception e) {
            throw AppErrors.INSTANCE.deSerializerError().exception();
        }
    }
}
