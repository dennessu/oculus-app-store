// CHECKSTYLE:OFF
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.*;
import com.junbo.common.util.EnumRegistry;
import com.junbo.identity.data.identifiable.UserPasswordStrength;
import org.springframework.util.StringUtils;

/**
 * Common Mapper for enum to string, vice versa.
 */
public class CommonMapper {
    private ObjectMapper objectMapper = new ObjectMapper();

    String toGroupId(GroupId groupId) {
        if (groupId == null) {
            return null;
        }
        return groupId.getValue();
    }

    GroupId toGroupId(String groupId) {
        if (groupId == null) {
            return null;
        }
        return new GroupId(groupId);
    }

    UserPasswordId toUserPasswordId(String userPasswordId) {
        if (userPasswordId == null) {
            return null;
        }
        return new UserPasswordId(userPasswordId);
    }

    String toUserPasswordId(UserPasswordId userPasswordId) {
        if (userPasswordId == null) {
            return null;
        }
        return userPasswordId.getValue();
    }

    UserPinId toUserPinId(String userPinId) {
        if (userPinId == null) {
            return null;
        }
        return new UserPinId(userPinId);
    }

    String toUserPinId(UserPinId userPINId) {
        if(userPINId == null) {
            return null;
        }
        return userPINId.getValue();
    }

    SecurityQuestionId toSecurityQuestionId(String securityQuestionId) {
        if(securityQuestionId == null) {
            return null;
        }
        return new SecurityQuestionId(securityQuestionId);
    }

    String toSecurityQuestionId(SecurityQuestionId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserSecurityQuestionId toUserSecurityQuestionId(String id) {
        if(id == null) {
            return null;
        }
        return new UserSecurityQuestionId(id);
    }

    String toUserPhoneNumberId(UserPhoneNumberId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserPhoneNumberId toUserPhoneNumberId(String id) {
        if(id == null) {
            return null;
        }
        return new UserPhoneNumberId(id);
    }

    String toUserLoginAttemptId(UserCredentialVerifyAttemptId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserCredentialVerifyAttemptId toUserLoginAttemptId(String id) {
        if(id == null) {
            return null;
        }
        return new UserCredentialVerifyAttemptId(id);
    }

    String toUserSecurityQuestionId(UserSecurityQuestionId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    String toUserGroupId(UserGroupId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    UserGroupId toUserGroupId(String id) {
        if(id == null) {
            return null;
        }
        return new UserGroupId(id);
    }

    UserEmailId toUserEmailId(String id) {
        if(id == null) {
            return null;
        }
        return new UserEmailId(id);
    }

    String toUserEmailId(UserEmailId id) {
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
            throw AppCommonErrors.INSTANCE.fieldInvalid("passwordStrength").exception();
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

    public String toUserDeviceId(UserDeviceId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserDeviceId toUserDeviceId(String id) {
        if(id == null) {
            return null;
        }
        return new UserDeviceId(id);
    }

    public UserAuthenticatorId toUserAuthenticatorId(String id) {
        if(id == null) {
            return null;
        }
        return new UserAuthenticatorId(id);
    }

    public String toUserAuthenticatorId(UserAuthenticatorId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserCommunicationId toUserOptinId(String id) {
        if(id == null) {
            return null;
        }
        return new UserCommunicationId(id);
    }

    public String toUserOptInId(UserCommunicationId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTosAgreementId toUserTosAgreementId(String id) {
        if(id == null) {
            return null;
        }
        return new UserTosAgreementId(id);
    }

    public String toUserTosAgreementId(UserTosAgreementId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserSecurityQuestionVerifyAttemptId toUserSecurityQuestionVerifyAttemptId(String id) {
        if(id == null) {
            return null;
        }
        return new UserSecurityQuestionVerifyAttemptId(id);
    }

    public String toUserSecurityQuestionVerifyAttemptId(UserSecurityQuestionVerifyAttemptId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public String toPasswordRuleId(PasswordRuleId id) {
        if(id == null) {
            return null;
        }
        return id.getValue();
    }

    public PasswordRuleId toPasswordRuleId(String id) {
        if(id == null) {
            return null;
        }
        return new PasswordRuleId(id);
    }

    public String toDeviceId(DeviceId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public DeviceId toDeviceId(String id) {
        if (id == null) {
            return null;
        }
        return new DeviceId(id);
    }

    public String toTosId(TosId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public TosId toTosId(String id) {
        if (id == null) {
            return null;
        }
        return new TosId(id);
    }

    public UserPiiId toUserPiiId(String id) {
        if (id == null) {
            return null;
        }
        return new UserPiiId(id);
    }

    public String toUserPiiId(UserPiiId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public AddressId toAddressId(Long id) {
        if (id == null) {
            return null;
        }
        return new AddressId(id);
    }

    public Long toAddressId(AddressId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    Integer toInteger(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    String toString(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public String toUserTFAId(UserTFAId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTFAId toUserTFAId(String id) {
        if (id == null) {
            return null;
        }
        return new UserTFAId(id);
    }

    public String toUserTFAAttemptId(UserTFAAttemptId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTFAAttemptId toUserTFAAttemptId(String id) {
        if (id == null) {
            return null;
        }
        return new UserTFAAttemptId(id);
    }

    public String toUserTFABackupCodeId(UserTFABackupCodeId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTFABackupCodeId toUserTFABackupCodeId(String id) {
        if (id == null) {
            return null;
        }
        return new UserTFABackupCodeId(id);
    }

    public String toUserTFABackupCodeAttemptId(UserTFABackupCodeAttemptId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public UserTFABackupCodeAttemptId toUserTFABackupCodeAttemptId(String id) {
        if (id == null) {
            return null;
        }
        return new UserTFABackupCodeAttemptId(id);
    }

    public String toCommunicationId(CommunicationId id) {
        if (id == null) {
            return null;
        }
        return id.getValue();
    }

    public CommunicationId toCommunicationId(String id) {
        if (id == null) {
            return null;
        }
        return new CommunicationId(id);
    }

    public JsonNode jsonNodeCopy(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.deepCopy();
    }
}
