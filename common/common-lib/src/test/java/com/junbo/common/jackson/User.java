/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.OrderId;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;

import java.util.List;
import java.util.Set;

/**
 * TestEntity.
 */
public class User implements PropertyAssignedAware {

    private final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

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

    private String test1;

    @UserId
    private List<String> test2;

    @OrderId
    private Long orderId;

    public User() {

    }

    public User(Long userId) {
        setUserId(userId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("self");
    }

    public Long getSpouseId() {
        return spouseId;
    }

    public void setSpouseId(Long spouseId) {
        this.spouseId = spouseId;
        support.setPropertyAssigned("spouseId");
    }

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
        support.setPropertyAssigned("children");
    }

    public Set<User> getParents() {
        return parents;
    }

    public void setParents(Set<User> parents) {
        this.parents = parents;
        support.setPropertyAssigned("parents");
    }

    public List<Long> getFriends() {
        return friends;
    }

    public void setFriends(List<Long> friends) {
        this.friends = friends;
        support.setPropertyAssigned("friends");
    }

    public Set<Long> getOpponents() {
        return opponents;
    }

    public void setOpponents(Set<Long> opponents) {
        this.opponents = opponents;
        support.setPropertyAssigned("opponents");
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
        support.setPropertyAssigned("test1");
    }

    public List<String> getTest2() {
        return test2;
    }

    public void setTest2(List<String> test2) {
        this.test2 = test2;
        support.setPropertyAssigned("test2");
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
        support.setPropertyAssigned("orderId");
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}
