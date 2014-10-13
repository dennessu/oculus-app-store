/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.UsernameMailBlockerId;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by liangfu on 10/9/14.
 */
public class UsernameMailBlocker extends ResourceMeta<UsernameMailBlockerId> {
    private UsernameMailBlockerId id;

    @CloudantIgnore
    private String email;

    @CloudantIgnore
    private String username;

    @JsonIgnore
    private String canonicalUsername;

    @JsonIgnore
    private String hashEmail;

    @JsonIgnore
    private String hashUsername;

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

    public UsernameMailBlockerId getId() {
        return id;
    }

    public void setId(UsernameMailBlockerId id) {
        this.id = id;
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
    }

    public String getHashEmail() {
        return hashEmail;
    }

    public void setHashEmail(String hashEmail) {
        this.hashEmail = hashEmail;
    }

    public String getHashUsername() {
        return hashUsername;
    }

    public void setHashUsername(String hashUsername) {
        this.hashUsername = hashUsername;
    }
}
