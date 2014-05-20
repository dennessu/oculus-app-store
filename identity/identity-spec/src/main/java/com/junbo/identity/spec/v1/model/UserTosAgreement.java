/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.TosId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserTosAgreement extends PropertyAssignedAwareResourceMeta implements Identifiable<UserTosAgreementId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of the user agreement resource.")
    @JsonProperty("self")
    private UserTosAgreementId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "The tos resource.")
    @JsonProperty("tos")
    private TosId tosId;

    @ApiModelProperty(position = 4, required = false, value = "[Nullable]The tos accept time.")
    private Date agreementTime;

    @ApiModelProperty(position = 5, required = false, value = "The future expansion of user tos agreement resource.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

    public UserTosAgreementId getId() {
        return id;
    }

    public void setId(UserTosAgreementId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public TosId getTosId() {
        return tosId;
    }

    public void setTosId(TosId tosId) {
        this.tosId = tosId;
        support.setPropertyAssigned("tosId");
        support.setPropertyAssigned("tos");
    }

    public Date getAgreementTime() {
        return agreementTime;
    }

    public void setAgreementTime(Date agreementTime) {
        this.agreementTime = agreementTime;
        support.setPropertyAssigned("agreementTime");
    }

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }
}
