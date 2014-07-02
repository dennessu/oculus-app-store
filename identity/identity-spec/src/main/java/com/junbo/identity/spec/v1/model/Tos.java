/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.id.TosId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 4/3/14.
 */
public class Tos extends PropertyAssignedAwareResourceMeta<TosId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]Link to the TOS resource.")
    @JsonProperty("self")
    private TosId id;

    @ApiModelProperty(position = 2, required = true, value = "The type of the TOS, must be from an enum list (EULA, TOS, PP, etc).")
    private String type;

    @ApiModelProperty(position = 3, required = true, value = "Array of links to Country resource, represents which countries this TOS is good in.")
    private List<CountryId> countries = new ArrayList<>();

    @ApiModelProperty(position = 4, required = true, value = "Version number of the TOS.")
    private String version;

    @ApiModelProperty(position = 5, required = true, value = "The title of the TOS.")
    private String title;

    @ApiModelProperty(position = 6, required = true, value = "The content of the TOS resource.")
    private String content;

    @ApiModelProperty(position = 7, required = true, value = "State of the TOS, it must in [DRAFT, APPROVED, OBSOLETE].")
    private String state;

    @Override
    public TosId getId() {
        return id;
    }

    public void setId(TosId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        support.setPropertyAssigned("title");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        support.setPropertyAssigned("content");
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        support.setPropertyAssigned("state");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public List<CountryId> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryId> countries) {
        this.countries = countries;
        support.setPropertyAssigned("countries");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        support.setPropertyAssigned("version");
    }
}
