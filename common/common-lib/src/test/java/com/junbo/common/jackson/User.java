/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.UserId;

import java.util.List;
import java.util.Set;

/**
 * Java doc.
 */
public class User {
    @UserId
    @JsonProperty("self")
    private Long userId;

    @UserId
    private Long spouseId;

    private List<User> children;

    private Set<User> parents;

    @UserId
    private List<Long> friends;

    @UserId
    private Set<Long> opponents;

    public User() {

    }

    public User(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSpouseId() {
        return spouseId;
    }

    public void setSpouseId(Long spouseId) {
        this.spouseId = spouseId;
    }

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public Set<User> getParents() {
        return parents;
    }

    public void setParents(Set<User> parents) {
        this.parents = parents;
    }

    public List<Long> getFriends() {
        return friends;
    }

    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    public Set<Long> getOpponents() {
        return opponents;
    }

    public void setOpponents(Set<Long> opponents) {
        this.opponents = opponents;
    }
}
