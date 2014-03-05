/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.password;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.PasswordRuleId;
import com.junbo.identity.spec.model.common.CommonStamp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 2/24/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordRule extends CommonStamp {
    @JsonProperty("self")
    private PasswordRuleId id;

    private String passwordStrength;

    // Allowed character set will contains the following types:
    // 1):  UPPER_ALPHA;
    // 2):  LOWER_ALPHA;
    // 3):  DIGITAL;
    // 4):  SPECIAL_ENGLISH_CHARACTER
    private List<String> allowedCharacterSet = new ArrayList<String>();

    // Not allowed character set will contains the following types:
    //  1): SPACE
    private List<String> notAllowedCharacterSet = new ArrayList<String>();

    @JsonProperty("details")
    private List<PasswordRuleDetail> passwordRuleDetails = new ArrayList<PasswordRuleDetail>();

    public PasswordRuleId getId() {
        return id;
    }

    public void setId(PasswordRuleId id) {
        this.id = id;
    }

    public String getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(String passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    public List<String> getAllowedCharacterSet() {
        return allowedCharacterSet;
    }

    public void setAllowedCharacterSet(List<String> allowedCharacterSet) {
        this.allowedCharacterSet = allowedCharacterSet;
    }

    public List<String> getNotAllowedCharacterSet() {
        return notAllowedCharacterSet;
    }

    public void setNotAllowedCharacterSet(List<String> notAllowedCharacterSet) {
        this.notAllowedCharacterSet = notAllowedCharacterSet;
    }

    public List<PasswordRuleDetail> getPasswordRuleDetails() {
        return passwordRuleDetails;
    }

    public void setPasswordRuleDetails(List<PasswordRuleDetail> passwordRuleDetails) {
        this.passwordRuleDetails = passwordRuleDetails;
    }
}
