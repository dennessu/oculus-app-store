/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by kg on 3/10/14.
 */
public class UserName {

    @ApiModelProperty(position = 1, required = true, value = "The first name of user pii resource.")
    private String firstName;

    @ApiModelProperty(position = 2, required = false, value = "The middle name of user pii resource.")
    private String middleName;

    @ApiModelProperty(position = 3, required = true, value = "The last name of user pii resource.")
    private String lastName;

    @ApiModelProperty(position = 4, required = false, value = "The honorific prefix of user pii resource.")
    private String honorificPrefix;

    @ApiModelProperty(position = 5, required = false, value = "The honorific suffix of user pii resource.")
    private String honorificSuffix;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHonorificPrefix() {
        return honorificPrefix;
    }

    public void setHonorificPrefix(String honorificPrefix) {
        this.honorificPrefix = honorificPrefix;
    }

    public String getHonorificSuffix() {
        return honorificSuffix;
    }

    public void setHonorificSuffix(String honorificSuffix) {
        this.honorificSuffix = honorificSuffix;
    }
}
