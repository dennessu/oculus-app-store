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
@Table(name="item")
public class ItemEntity extends BaseEntity {
    private String type;
    private Long itemId;
    private String name;
    private Long ownerId;
    private boolean curated;
    private Long currentRevisionId;

    @Id
    @Column(name = "item_id")
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "item_name")
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

    @Column(name = "curated")
    public boolean isCurated() {
        return curated;
    }

    public void setCurated(boolean curated) {
        this.curated = curated;
    }

    @Column(name = "current_revision_id")
    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    @Override
    @Transient
    public Long getId() {
        return itemId;
    }

    @Override
    public void setId(Long id) {
        this.itemId = id;
    }
}
