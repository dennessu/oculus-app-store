/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import javax.persistence.*;

/**
 * Item DB entity.
 */
@Entity
@Table(name="item_offer_relations")
public class ItemOfferRelationsEntity extends BaseEntity {
    private Long id;
    private Integer entityType;
    private Long entityId;
    private Long parentOfferId;

    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "entity_type")
    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    @Column(name = "entity_id")
    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @Column(name = "parent_offer_id")
    public Long getParentOfferId() {
        return parentOfferId;
    }

    public void setParentOfferId(Long parentOfferId) {
        this.parentOfferId = parentOfferId;
    }
}
