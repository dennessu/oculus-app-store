/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.EmailTemplateId;
//import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Email Template Model.
 */
@JsonPropertyOrder(value = {"self","name","providerName","providerRef","placeholderNames",
        "subject","fromAddress","fromName"})
public class EmailTemplate extends Model {
    @ApiModelProperty(position = 1, required = true, value = "The id of the email template resource.")
    @JsonProperty("self")
    private EmailTemplateId id;

    @JsonIgnore
    private String name;

    @ApiModelProperty(position = 3, required = true, value = "The source of the email template.")
    private String source;

    @ApiModelProperty(position = 4, required = true, value = "The action of the email template.")
    private String action;

    @ApiModelProperty(position = 5, required = true, value = "The locale of the email template.")
    private LocaleId locale;

    @ApiModelProperty(position = 6, required = true, value = "The service provider name of the email template.")
    private String providerName;

    @ApiModelProperty(position = 7, required = true, value = "The provider reference of the email template.")
    @JsonProperty("providerRef")
    private String providerIndex;

    @ApiModelProperty(position = 8, required = true, value = "The placeholder names of the email template.")
    @JsonProperty("placeholderNames")
    private List<String> placeholderNames;

    @ApiModelProperty(position = 9, required = true, value = "The subject of the email template.")
    private String subject;

    @ApiModelProperty(position = 10, required = true, value = "The from email address of the email template.")
    private String fromAddress;

    @ApiModelProperty(position = 11, required = true, value = "The from name of the email template.")
    private String fromName;

    public EmailTemplateId getId() {
        return id;
    }

    public void setId(EmailTemplateId id) {
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

    public List<String> getPlaceholderNames() {
        return placeholderNames;
    }

    public void setPlaceholderNames(List<String> placeholderNames) {
        this.placeholderNames = placeholderNames;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }
}
