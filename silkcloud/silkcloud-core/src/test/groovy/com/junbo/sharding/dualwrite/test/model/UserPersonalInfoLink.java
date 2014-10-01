package com.junbo.sharding.dualwrite.test.model;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;

public class UserPersonalInfoLink {

    private Boolean isDefault;
    private UserPersonalInfoId value;
    private UserId userId;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public UserPersonalInfoId getValue() {
        return value;
    }

    public void setValue(UserPersonalInfoId value) {
        this.value = value;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
