/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.id.EmailId;

import java.util.List;

/**
 * Email Template Model.
 */
@JsonPropertyOrder(value = {"self","name","providerName","providerIndex","listOfVariables",
        "subject","fromAddress","fromName","createdTime","modifiedTime"})
public class EmailTemplate extends Model {
    @JsonProperty("self")
    private EmailId id;

    private String name;

    private String providerName;

    private String providerIndex;

    private List<String> listOfVariables;

    private String subject;

    private String fromAddress;

    private String fromName;

    public EmailId getId() {
        return id;
    }

    public void setId(EmailId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderIndex() {
        return providerIndex;
    }

    public void setProviderIndex(String providerIndex) {
        this.providerIndex = providerIndex;
    }

    public List<String> getListOfVariables() {
        return listOfVariables;
    }

    public void setListOfVariables(List<String> listOfVariables) {
        this.listOfVariables = listOfVariables;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
