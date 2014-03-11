/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.commonhelper.blueprint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.junbo.common.id.UserId;

/**
 * @author Jason.
 * Time: 3/10/2014
 * User model class
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private UserId id;
    private String userName;
    private String status;
    private String password;
    private String passwordStrength;

    public UserId getId() { return id; }

    public void setId(UserId id) { this.id = id; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(String passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

}
