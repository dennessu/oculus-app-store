/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.auth;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.model.AuthorizeContext;
import com.junbo.identity.spec.model.user.User;
import com.junbo.oauth.spec.model.TokenInfo;
import groovy.transform.CompileStatic;

import java.util.Map;

/**
 * UserAuthorizeCallback.
 */
@CompileStatic
public class UserAuthorizeCallback implements AuthorizeCallback<User> {
    public UserAuthorizeCallback(Map<String, Object> context) {
        this.apiName = (String) context.get("apiName");
        this.id = (Long) context.get("id");
        this.entity = (User) context.get("entity");
        if (id == null && entity != null) {
            id = entity.getId().getValue();
        }

    }

    public Boolean isOwner() {
        return id.equals(tokenInfo.getSub().getValue());
    }

    public Boolean isAdmin() {
        return tokenInfo.getSub().getValue() == 123L;
    }

    public Boolean isGuest() {
        return !isOwner() && !isAdmin();
    }

    @Override
    public String getApiName() {
        return apiName;
    }

    @Override
    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    @Override
    public User postFilter() {
        if (!AuthorizeContext.hasClaim("owner") && !AuthorizeContext.hasClaim("admin")) {
            User user = new User();
            user.setId(entity.getId());

            return user;
        }


        if (!AuthorizeContext.hasClaim("admin")) {
            entity.setCreatedTime(null);
            entity.setUpdatedTime(null);
            return entity;
        }


        return entity;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getEntity() {
        return entity;
    }

    public void setEntity(User entity) {
        this.entity = entity;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    private String apiName;
    private Long id;
    private User entity;
    private TokenInfo tokenInfo;
}
