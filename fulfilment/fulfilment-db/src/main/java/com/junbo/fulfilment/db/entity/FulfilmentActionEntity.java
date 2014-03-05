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

/**
 * FulfilmentActionEntity.
 */
@Entity
@Table(name = "fulfilment_action")
@TypeDefs({@TypeDef(name = "json-string", typeClass = JSONStringUserType.class)})
public class FulfilmentActionEntity extends BaseEntity {
    private Long id;
    private Long fulfilmentId;
    private String payload;
    private FulfilmentActionType type;
    private FulfilmentStatus status;
    private String result;

    @Id
    @Column(name = "action_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "fulfilment_id")
    public Long getFulfilmentId() {
        return fulfilmentId;
    }

    public void setFulfilmentId(Long fulfilmentId) {
        this.fulfilmentId = fulfilmentId;
    }

    @Column(name = "payload")
    @Type(type = "json-string")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Column(name = "type")
    public FulfilmentActionType getType() {
        return type;
    }

    public void setType(FulfilmentActionType type) {
        this.type = type;
    }

    @Column(name = "status")
    public FulfilmentStatus getStatus() {
        return status;
    }

    public void setStatus(FulfilmentStatus status) {
        this.status = status;
    }

    @Column(name = "result")
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    @Transient
    public Long getShardMasterId() {
        return fulfilmentId;
    }
}
