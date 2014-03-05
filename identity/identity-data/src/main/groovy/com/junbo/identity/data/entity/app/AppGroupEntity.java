/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.app;

import com.junbo.identity.data.entity.user.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 2/19/14.
 */
@Entity
@Table(name = "app_group")
public class AppGroupEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "app_id")
    private Long appId;

    @Column(name = "name")
    private String name;

    @Column(name = "permissions")
    private String permissions;

    // The reason we don't use one to may is due to this group may be in different shard, so it will involve cross shard
    // operation, we won't enable this.
    @Transient
    private List<UserEntity> members = new ArrayList<UserEntity>();

    public List<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(List<UserEntity> members) {
        this.members = members;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }
}
