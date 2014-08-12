/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 6/6/14.
 */
public class OculusInput {
    @ApiModelProperty(position = 1, required = true, value = "The original id of oculus database.")
    @JsonProperty("id")
    private Long currentId;

    @ApiModelProperty(position = 2, required = false, value = "User's first name in oculus database.")
    @XSSFreeString
    private String firstName;

    @ApiModelProperty(position = 3, required = false, value = "User's last name in oculus database.")
    @XSSFreeString
    private String lastName;

    @ApiModelProperty(position = 4, required = true, value = "User's email in oculus database.")
    @XSSFreeString
    private String email;

    @ApiModelProperty(position = 5, required = true, value = "User's username in oculus database.")
    @XSSFreeString
    private String username;

    @ApiModelProperty(position = 6, required = true, value = "User's hashed password, it must be the format as 1:$salt:$pepper:$hashedValue")
    private String password;

    @ApiModelProperty(position = 7, required = false, value = "User's gender information, it must be [MALE, FEMALE].")
    @XSSFreeString
    private String gender;

    @ApiModelProperty(position = 8, required = false, value = "User's birthday information in oculus database.")
    private Date dob;

    @ApiModelProperty(position = 9, required = false, value = "User's nick name in oculus database.")
    @XSSFreeString
    private String nickname;

    @ApiModelProperty(position = 10, required = false, value = "User's timezone in oculus database.")
    private Number timezone;

    @ApiModelProperty(position = 11, required = false, value = "User's language in oculus database, it should be the format as en_US.")
    private String language;

    @ApiModelProperty(position = 12, required = false, value = "User's created time in oculus database.")
    private Date createdDate;

    @ApiModelProperty(position = 13, required = false, value = "User's updated time in oculus database.")
    private Date updateDate;

    @ApiModelProperty(position = 17, required = false, value = "User's company information in oculus database.")
    private Company company;

    @ApiModelProperty(position = 18, required = false, value = "User's profile information in oculus database.")
    private ShareProfile profile;

    @ApiModelProperty(position = 14, required = false, value = "Reset password flag in oculus database.")
    private Boolean forceResetPassword;

    @ApiModelProperty(position = 15, required = true, value = "User's status in oculus database, it must be " +
            "[ACTIVE, ARCHIVE, PENDING, PENDING_EMAIL_VERIFICATION, VERIFIED]")
    private String status;
    // the communication id that the user should have

    @ApiModelProperty(position = 16, required = false, value = "User's communication map. It must be the format as communicationId:true/false")
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
