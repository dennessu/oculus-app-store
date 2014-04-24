/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.AddressId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPiiId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserPii extends ResourceMeta implements Identifiable<UserPiiId> {

    @ApiModelProperty(position = 1, required = true, value = "The id of the user pii resource.")
    @JsonProperty("self")
    private UserPiiId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "User name of the user pii.")
    private UserName name;

    @ApiModelProperty(position = 4, required = false, value = "Birthday of the user.")
    private Date birthday;

    @ApiModelProperty(position = 5, required = false, value = "Gender of the user.")
    private String gender;

    /*
     * Possible values:
     * 1. firstName_lastName_then_username
     * 2. lastName_firstName_then_username
     * 3. firstName_middleName_lastName_then_username
     * 4. lastName_middleName_firstName_then_username
     * 5. username
     */
    @JsonIgnore
    private Integer displayNameType;

    @ApiModelProperty(position = 6, required = true, value = "The display name of the user.")
    // This won't save in DB
    private String displayName;

    @ApiModelProperty(position = 7, required = false, value = "The emails of the user.")
    private Map<String, UserEmail> emails = new HashMap<>();

    @ApiModelProperty(position = 8, required = false, value = "The phone number of the user.")
    private Map<String, UserPhoneNumber> phoneNumbers = new HashMap<>();

    @ApiModelProperty(position = 9, required = false, value = "The addresses of the user.")
    private List<AddressId> addressBook;

    @Override
    public UserPiiId getId() {
        return id;
    }

    public void setId(UserPiiId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public UserName getName() {
        return name;
    }

    public void setName(UserName name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public Map<String, UserEmail> getEmails() {
        return emails;
    }

    public void setEmails(Map<String, UserEmail> emails) {
        this.emails = emails;
        support.setPropertyAssigned("emails");
    }

    public Map<String, UserPhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<String, UserPhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        support.setPropertyAssigned("phoneNumbers");
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
        support.setPropertyAssigned("birthday");
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        support.setPropertyAssigned("gender");
    }

    public Integer getDisplayNameType() {
        return displayNameType;
    }

    public void setDisplayNameType(Integer displayNameType) {
        this.displayNameType = displayNameType;
        support.setPropertyAssigned("displayNameType");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        support.setPropertyAssigned("displayName");
    }

    public List<AddressId> getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(List<AddressId> addressBook) {
        this.addressBook = addressBook;
        support.setPropertyAssigned("addressBook");
    }
}
