/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.TosId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserTosAgreement extends ResourceMeta implements Identifiable<UserTosAgreementId> {

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
    }
}
