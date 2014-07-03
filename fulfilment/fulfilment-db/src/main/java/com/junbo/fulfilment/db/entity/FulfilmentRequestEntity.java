/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.entity;

import com.junbo.fulfilment.db.ext.JSONStringUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.UUID;

/**
 * FulfilmentRequestEntity.
 */
@Entity
@Table(name = "fulfilment_request")
@TypeDefs({@TypeDef(name = "json-string", typeClass = JSONStringUserType.class)})
public class FulfilmentRequestEntity extends BaseEntity {
    private Long id;
    private UUID trackingUuid;
    private String payload;
    private Long userId;
    private Long orderId;

    @Id
    @Column(name = "request_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Column(name = "payload")
    @Type(type = "json-string")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "order_id")
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    @Transient
    public Long getShardMasterId() {
        return userId;
    }
}
