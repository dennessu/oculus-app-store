/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.password;

import com.junbo.identity.data.entity.common.ResourceMetaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liangfu on 2/24/14.
 */
@Entity
@Table(name = "password_rule")
public class PasswordRuleEntity extends ResourceMetaEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "password_strength")
    private Short passwordStrength;

    @Column(name = "allowed_character_set")
    private String allowedCharacterSet;

    @Column(name = "not_allowed_character_set")
    private String notAllowedCharacterSet;

    @Column(name = "password_rule")
    private String passwordRuleDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(Short passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    public String getAllowedCharacterSet() {
        return allowedCharacterSet;
    }

    public void setAllowedCharacterSet(String allowedCharacterSet) {
        this.allowedCharacterSet = allowedCharacterSet;
    }

    public String getNotAllowedCharacterSet() {
        return notAllowedCharacterSet;
    }

    public void setNotAllowedCharacterSet(String notAllowedCharacterSet) {
        this.notAllowedCharacterSet = notAllowedCharacterSet;
    }

    public String getPasswordRuleDetails() {
        return passwordRuleDetails;
    }

    public void setPasswordRuleDetails(String passwordRuleDetails) {
        this.passwordRuleDetails = passwordRuleDetails;
    }
}
