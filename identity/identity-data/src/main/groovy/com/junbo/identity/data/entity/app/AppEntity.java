/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.app;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 2/19/14.
 */
@Entity
@Table(name = "app")
public class AppEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "name")
    private String name;

    @Column(name = "redirect_uris")
    private String redirectUris;

    @Column(name = "default_redirect_uri")
    private String defaultRedirectUri;

    @Column(name = "logout_uris")
    private String logoutUris;

    @Column(name = "response_types")
    private String responseTypes;

    @Column(name = "grant_types")
    private String grantTypes;

    @Column(name = "ip_whitelist")
    private String ipWhitelist;

    @Column(name = "properties")
    private String properties;

    // Todo:    Liangfu:    Whether we can use default oneToMany operations here?
    @Transient
    private List<AppSecretEntity> appSecrets = new ArrayList<AppSecretEntity>();

    @Transient
    private List<AppGroupEntity> groups = new ArrayList<AppGroupEntity>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<AppSecretEntity> getAppSecrets() {
        return appSecrets;
    }

    public void setAppSecrets(List<AppSecretEntity> appSecrets) {
        this.appSecrets = appSecrets;
    }

    public List<AppGroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(List<AppGroupEntity> groups) {
        this.groups = groups;
    }
}
