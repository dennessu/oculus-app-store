/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.common.hibernate.LongArrayUserType;
import com.junbo.common.hibernate.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

/**
 * Offer DB entity.
 */
@Entity
@Table(name="offer_revision")
@TypeDefs({@TypeDef(name="json-string", typeClass=StringJsonUserType.class),
        @TypeDef(name="long-array", typeClass=LongArrayUserType.class)})
public class OfferRevisionEntity extends BaseEntity {
    private Long revisionId;
    private Long offerId;
    private Long ownerId;
    private String status;
    private List<Long> itemIds;
    private List<Long> subOfferIds;
    private Long timestamp;
    private String payload;

    @Id
    @Column(name = "revision_id")
    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    @Column(name = "offer_id")
    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
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

    @Column(name = "item_ids")
    @Type(type = "long-array")
    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    @Column(name = "sub_offer_ids")
    @Type(type = "long-array")
    public List<Long> getSubOfferIds() {
        return subOfferIds;
    }

    public void setSubOfferIds(List<Long> subOfferIds) {
        this.subOfferIds = subOfferIds;
    }

    @Column(name = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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
    public Long getId() {
        return revisionId;
    }

    @Override
    public void setId(Long id) {
        this.revisionId = id;
    }
}
