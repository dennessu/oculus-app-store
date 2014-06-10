/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import java.util.Date;

/**
 * Created by liangfu on 6/6/14.
 */
public class OculusInput {

    private String currentId;
    private String firstName;
    private String lastName;
    private String nickName;
    private String email;
    private String username;
    private String password;
    private String gender;
    private Date dob;
    private Number timeZone;
    private String language;
    private Date createdDate;
    private Date updateDate;
    private String devCenterCompany;

    private ShareProfile shareProfile;
    private Boolean oldPasswordHash;
    private String status;

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Number getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Number timeZone) {
        this.timeZone = timeZone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getDevCenterCompany() {
        return devCenterCompany;
    }

    public void setDevCenterCompany(String devCenterCompany) {
        this.devCenterCompany = devCenterCompany;
    }

    public ShareProfile getShareProfile() {
        return shareProfile;
    }

    public void setShareProfile(ShareProfile shareProfile) {
        this.shareProfile = shareProfile;
    }

    public Boolean getOldPasswordHash() {
        return oldPasswordHash;
    }

    public void setOldPasswordHash(Boolean oldPasswordHash) {
        this.oldPasswordHash = oldPasswordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
