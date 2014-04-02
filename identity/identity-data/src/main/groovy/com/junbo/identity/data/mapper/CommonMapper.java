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
import com.junbo.identity.data.identifiable.UserPasswordStrength;
import com.junbo.identity.spec.error.AppErrors;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.List;

/**
 * Common Mapper for enum to string, vice versa.
 */
public class CommonMapper {
    private ObjectMapper objectMapper = new ObjectMapper();

    Long toGroupId(GroupId groupId) {
        if (groupId == null) {
            return null;
        }
        return groupId.getValue();
    }

    GroupId toGroupId(Long groupId) {
        if (groupId == null) {
            return null;
        }
        return new GroupId(groupId);
    }

    UserPasswordId toUserPasswordId(Long userPasswordId) {
        if (userPasswordId == null) {
            return null;
        }
        return new UserPasswordId(userPasswordId);
    }

    Long toUserPasswordId(UserPasswordId userPasswordId) {
        if (userPasswordId == null) {
            return null;
        }
        return userPasswordId.getValue();
    }

    UserPinId toUserPinId(Long userPinId) {
        if (userPinId == null) {
            return null;
        }
        return new UserPinId(userPinId);
    }

    Long toUserPinId(UserPinId userPINId) {
        if(userPINId == null) {
            return null;
        }
        return userPINId.getValue();
    }

    SecurityQuestionId toSecurityQuestionId(Long securityQuestionId) {
        if(securityQuestionId == null) {
            return null;
        }
        return new SecurityQuestionId(securityQuestionId);
    }

    Long toSecurityQuestionId(SecurityQuestionId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserSecurityQuestionId toUserSecurityQuestionId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserSecurityQuestionId(id);
    }

    Long toUserPhoneNumberId(UserPhoneNumberId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserPhoneNumberId toUserPhoneNumberId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserPhoneNumberId(id);
    }

    Long toUserLoginAttemptId(UserLoginAttemptId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserLoginAttemptId toUserLoginAttemptId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserLoginAttemptId(id);
    }

    Long toUserSecurityQuestionId(UserSecurityQuestionId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    Long toUserGroupId(UserGroupId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserGroupId toUserGroupId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserGroupId(id);
    }

    UserEmailId toUserEmailId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserEmailId(id);
    }

    Long toUserEmailId(UserEmailId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public Short explicitMethod_toUserPasswordStrength(String passwordStrength) {
        if(StringUtils.isEmpty(passwordStrength)) {
            return null;
        }
        try {
            return UserPasswordStrength.valueOf(UserPasswordStrength.class, passwordStrength).getId();
        }
        catch (Exception e) {
            throw AppErrors.INSTANCE.fieldInvalid("passwordStrength").exception();
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

    public Long fromStringToLong(String str) {
        return Long.parseLong(str);
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

    public Long toUserDeviceId(UserDeviceId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserDeviceId toUserDeviceId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserDeviceId(id);
    }

    public UserAuthenticatorId toUserAuthenticatorId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserAuthenticatorId(id);
    }

    public Long toUserAuthenticatorId(UserAuthenticatorId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserOptinId toUserOptinId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserOptinId(id);
    }

    public Long toUserOptInId(UserOptinId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTosId toUserTosId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserTosId(id);
    }

    public Long toUserTosId(UserTosId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserSecurityQuestionAttemptId toUserSecurityQuestionAttemptId(Long id) {
        if(id == null) {
            return null;
        }
        return new UserSecurityQuestionAttemptId(id);
    }

    public Long toUserSecurityQuestionAttemptId(UserSecurityQuestionAttemptId id) {
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
}
