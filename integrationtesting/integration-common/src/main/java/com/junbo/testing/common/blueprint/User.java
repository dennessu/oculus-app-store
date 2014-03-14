/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.blueprint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;

import java.util.Date;

/**
 * @author Jason.
 * Time: 3/10/2014
 * User model class
 */

public class User {

    @JsonProperty("self")
    private UserId id;

    private String userName;
    private String status;
    private String password;

    private Date createdTime;
    private Integer resourceAge;
    private Date updatedTime;

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getResourceAge() { return resourceAge; }

    public void setResourceAge(Integer resourceAge) { this.resourceAge = resourceAge; }

}
