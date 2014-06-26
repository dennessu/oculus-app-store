/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.entity;

import com.junbo.token.spec.enums.CreateMethod;
import com.junbo.token.spec.enums.OrderStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * token order entity.
 */
@Entity
@Table(name = "token_order")
public class TokenOrderEntity extends GenericEntity {

    @Id
    @Column(name = "token_order_id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "token_set_id")
    private Long tokenSetId;

    @Column(name = "token_order_status")
    private OrderStatus status;

    @Column(name = "expired_time")
    private Date expiredTime;

    @Column(name = "usage_limit")
    private String usageLimit;

    @Column(name = "creation_method")
    private CreateMethod createMethod;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "activation")
    private String activation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTokenSetId() {
        return tokenSetId;
    }

    public void setTokenSetId(Long tokenSetId) {
        this.tokenSetId = tokenSetId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(String usageLimit) {
        this.usageLimit = usageLimit;
    }

    public CreateMethod getCreateMethod() {
        return createMethod;
    }

    public void setCreateMethod(CreateMethod createMethod) {
        this.createMethod = createMethod;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }
}
