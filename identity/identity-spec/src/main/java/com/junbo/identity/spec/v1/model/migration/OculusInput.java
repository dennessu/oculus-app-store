/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.XSSFreeString;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 6/6/14.
 */
public class OculusInput {
    @JsonProperty("id")
    private Long currentId;
    @XSSFreeString
    private String firstName;

    @XSSFreeString
    private String lastName;

    @XSSFreeString
    private String email;

    @XSSFreeString
    private String username;
    private String password;

    @XSSFreeString
    private String gender;
    private Date dob;

    @XSSFreeString
    private String nickname;
    private Number timezone;
    private String language;
    private Date createdDate;
    private Date updateDate;
    private Company company;
    private ShareProfile profile;
    private Boolean forceResetPassword;
    private String status;
    // the communication id that the user should have
    private List<Map<String, Boolean>> communications;

    public Long getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Long currentId) {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Number getTimezone() {
        return timezone;
    }

    public void setTimezone(Number timezone) {
        this.timezone = timezone;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ShareProfile getProfile() {
        return profile;
    }

    public void setProfile(ShareProfile profile) {
        this.profile = profile;
    }

    public Boolean getForceResetPassword() {
        return forceResetPassword;
    }

    public void setForceResetPassword(Boolean forceResetPassword) {
        this.forceResetPassword = forceResetPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, Boolean>> getCommunications() {
        return communications;
    }

    public void setCommunications(List<Map<String, Boolean>> communications) {
        this.communications = communications;
    }
}
