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

import javax.persistence.*;

/**
 * Offer DB entity.
 */
@Entity
@Table(name="offer")
@TypeDefs(@TypeDef(name="json-string", typeClass=StringJsonUserType.class))
public class OfferEntity extends BaseEntity {
    private Long offerId;
    private String offerName;
    private Long ownerId;
    private boolean curated;
    private Long currentRevisionId;
    private String categories;

    @Id
    @Column(name = "offer_id")
    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    @Column(name = "offer_name")
    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
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

    @Column(name = "categories")
    @Type(type = "json-string")
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
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
