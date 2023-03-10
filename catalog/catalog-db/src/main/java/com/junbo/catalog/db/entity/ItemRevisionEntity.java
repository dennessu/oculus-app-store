/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.common.hibernate.StringArrayUserType;
import com.junbo.common.hibernate.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

/**
 * Item DB entity.
 */
@Entity
@Table(name="item_revision")
@TypeDefs({@TypeDef(name="json-string", typeClass=StringJsonUserType.class),
        @TypeDef(name="string-array", typeClass=StringArrayUserType.class)})
public class ItemRevisionEntity extends BaseEntity {
    private String revisionId;
    private String itemId;
    //private String type;
    private Long ownerId;
    private String status;
    private Long timestamp;
    private List<String> hostItemIds;
    private String payload;

    @Id
    @Column(name = "revision_id")
    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    @Column(name = "item_id")
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Column(name = "owner_id")
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "host_item_ids")
    @Type(type = "string-array")
    public List<String> getHostItemIds() {
        return hostItemIds;
    }

    public void setHostItemIds(List<String> hostItemIds) {
        this.hostItemIds = hostItemIds;
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
    public String getId() {
        return revisionId;
    }

    @Override
    public void setId(String id) {
        this.revisionId = id;
    }
}
