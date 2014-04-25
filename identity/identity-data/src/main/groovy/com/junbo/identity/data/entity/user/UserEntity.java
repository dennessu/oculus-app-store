/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user;

import com.junbo.common.util.Identifiable;
import com.junbo.identity.data.entity.common.ResourceMetaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * UserEntity list for user_device_profile table.
 */
@Entity
@Table(name = "user_account")
public class UserEntity extends ResourceMetaEntity implements Identifiable<Long> {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "preferred_locale")
    private String preferredLocale;

    @Column(name = "preferred_timezone")
    private String preferredTimezone;

    @Column(name = "status")
    private String status;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(name = "canonical_username")
    private String canonicalUsername;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCanonicalUsername() {
        return canonicalUsername;
    }

    public void setCanonicalUsername(String canonicalUsername) {
        this.canonicalUsername = canonicalUsername;
    }
}
