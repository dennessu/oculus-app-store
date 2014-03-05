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
    private UUID trackingGuid;
    private String payload;
    private Long orderId;

    @Id
    @Column(name = "request_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "tracking_guid")
    @Type(type = "pg-uuid")
    public UUID getTrackingGuid() {
        return trackingGuid;
    }

    public void setTrackingGuid(UUID trackingGuid) {
        this.trackingGuid = trackingGuid;
    }

    @Column(name = "payload")
    @Type(type = "json-string")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
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
        return orderId;
    }
}
