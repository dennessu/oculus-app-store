/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
@Entity
@Table(name = "CART")
@DynamicUpdate
public class CartEntity {

    private Long id;

    private Long userId;

    private Boolean userLoggedIn;

    private String clientId;

    private String cartName;

    private Integer resourceAge;

    private String properties;

    private Date updatedTime;

    private Date createdTime;

    @Id
    @Column(name = "CART_ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "USER_LOGGED_IN")
    public Boolean getUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(Boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    @Column(name = "CLIENT_ID")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Column(name = "CART_NAME")
    public String getCartName() {
        return cartName;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
    }

    @Column(name = "RESOURCE_AGE")
    @Version
    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

    @Column(name = "PROPERTIES")
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Column(name = "CREATED_TIME")
    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Column(name = "UPDATED_TIME")
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "CartEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", userLoggedIn=" + userLoggedIn +
                ", clientId='" + clientId + '\'' +
                ", cartName='" + cartName + '\'' +
                ", resourceAge=" + resourceAge +
                ", properties='" + properties + '\'' +
                ", updatedTime=" + updatedTime +
                ", createdTime=" + createdTime +
                '}';
    }
}
