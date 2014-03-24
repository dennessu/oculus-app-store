/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.catalog.db.dao.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Offer draft DB entity.
 */
@Entity
@Table(name="offer_draft")
@TypeDefs(@TypeDef(name="json-string", typeClass=StringJsonUserType.class))
public class OfferDraftEntity extends VersionedEntity {
    private Long id;
    private String name;
    private Long ownerId;
    private String payload;

    @Id
    @Column(name = "offer_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "offer_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "owner_id")
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name = "payload")
    @Type(type = "json-string")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
