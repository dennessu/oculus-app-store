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
 * FulfilmentEntity.
 */
@Entity
@Table(name = "fulfilment_item")
@TypeDefs({@TypeDef(name = "json-string", typeClass = JSONStringUserType.class)})
public class FulfilmentEntity extends BaseEntity {
    private Long id;
    private Long requestId;
    private String payload;

    @Id
    @Column(name = "fulfilment_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "request_id")
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Column(name = "payload")
    @Type(type = "json-string")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    @Transient
    public Long getShardMasterId() {
        return requestId;
    }
}
