package com.junbo.sharding.dualwrite.test.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.ResourceMeta;

import java.util.Date;

public class UserPersonalInfo extends ResourceMeta<UserPersonalInfoId> {

    private UserPersonalInfoId id;
    private String type;
    private JsonNode value;
    private Date lastValidateTime;
    private Boolean isNormalized;
    private String label;
    private UserId userId;

    public UserPersonalInfoId getId() {
        return id;
    }

    public void setId(UserPersonalInfoId id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }

    public Date getLastValidateTime() {
        return lastValidateTime;
    }

    public void setLastValidateTime(Date lastValidateTime) {
        this.lastValidateTime = lastValidateTime;
    }

    public Boolean getIsNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(Boolean isNormalized) {
        this.isNormalized = isNormalized;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
