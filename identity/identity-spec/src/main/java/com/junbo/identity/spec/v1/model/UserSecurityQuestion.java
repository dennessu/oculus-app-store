/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserSecurityQuestion extends PropertyAssignedAwareResourceMeta<UserSecurityQuestionId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Client Immutable]Link to the user security question resource.")
    @JsonProperty("self")
    private UserSecurityQuestionId id;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = true, value = "The text for the security question.")
    private String securityQuestion;

    @XSSFreeString
    @ApiModelProperty(position = 3, required = true, value = "The security question answer, this is write only field.")
    private String answer;

    @ApiModelProperty(position = 4, required = true, value = "[Client Immutable] User Link to the owner the security question record.")
    @JsonProperty("user")
    private UserId userId;

    @JsonIgnore
    private String answerHash;

    @Override
    public UserSecurityQuestionId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
        support.setPropertyAssigned("securityQuestion");
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        support.setPropertyAssigned("answer");
    }

    public String getAnswerHash() {
        return answerHash;
    }

    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
        support.setPropertyAssigned("answerHash");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }
}
