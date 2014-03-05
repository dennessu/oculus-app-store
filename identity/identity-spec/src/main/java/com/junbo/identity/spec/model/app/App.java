/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.AppId;
import com.junbo.common.id.UserId;

import java.util.ArrayList;
import java.util.List;

/**
 * Java doc for App return.
 */
public class App {
    @JsonProperty("self")
    private AppId id;

    @JsonProperty("owner")
    private UserId ownerId;
    private String name;
    private List<AppSecret> appSecrets = new ArrayList<AppSecret>();
    private List<AppGroup> groups = new ArrayList<AppGroup>();

    private String redirectUris;
    private String defaultRedirectUri;
    private String logoutUris;

    private String responseTypes;
    private String grantTypes;
    private String ipWhitelist;
    private String properties;

    public AppId getId() {
        return id;
    }

    public void setId(AppId id) {
        this.id = id;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserId ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AppSecret> getAppSecrets() {
        return appSecrets;
    }

    public void setAppSecrets(List<AppSecret> appSecrets) {
        this.appSecrets = appSecrets;
    }

    public List<AppGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AppGroup> groups) {
        this.groups = groups;
    }

    public String getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    public String getDefaultRedirectUri() {
        return defaultRedirectUri;
    }

    public void setDefaultRedirectUri(String defaultRedirectUri) {
        this.defaultRedirectUri = defaultRedirectUri;
    }

    public String getLogoutUris() {
        return logoutUris;
    }

    public void setLogoutUris(String logoutUris) {
        this.logoutUris = logoutUris;
    }

    public String getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(String responseTypes) {
        this.responseTypes = responseTypes;
    }

    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    public String getIpWhitelist() {
        return ipWhitelist;
    }

    public void setIpWhitelist(String ipWhitelist) {
        this.ipWhitelist = ipWhitelist;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
