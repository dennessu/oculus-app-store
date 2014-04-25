/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.catalog.db.dao.LongArrayUserType;
import com.junbo.catalog.db.dao.StringJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

/**
 * Offer DB entity.
 */
@Entity
@Table(name="offer")
@TypeDefs({@TypeDef(name="json-string", typeClass=StringJsonUserType.class),
        @TypeDef(name="long-array", typeClass=LongArrayUserType.class)})
public class OfferEntity extends BaseEntity {
    private Long offerId;
    private Long ownerId;
    private boolean published;
    private Long iapItemId;
    private String environment;
    private Long currentRevisionId;
    private List<Long> categories;

    @Id
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

    @Column(name = "published")
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Column(name = "iap_item_id")
    public Long getIapItemId() {
        return iapItemId;
    }

    public void setIapItemId(Long iapItemId) {
        this.iapItemId = iapItemId;
    }

    @Column(name = "environment")
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Column(name = "current_revision_id")
    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    @Column(name = "categories")
    @Type(type = "long-array")
    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    @Override
    @Transient
    public Long getId() {
        return offerId;
    }

    @Override
    public void setId(Long id) {
        this.offerId = id;
    }
}
