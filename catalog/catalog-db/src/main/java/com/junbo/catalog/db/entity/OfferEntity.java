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
 * Offer DB entity.
 */
@Entity
@Table(name="offer")
@TypeDefs({@TypeDef(name="json-string", typeClass=StringJsonUserType.class),
        @TypeDef(name="string-array", typeClass=StringArrayUserType.class)})
public class OfferEntity extends BaseEntity {
    private String offerId;
    private Long ownerId;
    private boolean published;
    private String iapItemId;
    private String environment;
    private String currentRevisionId;
    private List<String> categories;

    @Id
    @Column(name = "offer_id")
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
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
    public String getIapItemId() {
        return iapItemId;
    }

    public void setIapItemId(String iapItemId) {
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
    public String getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(String currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    @Column(name = "categories")
    @Type(type = "string-array")
    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    @Transient
    public String getId() {
        return offerId;
    }

    @Override
    public void setId(String id) {
        this.offerId = id;
    }
}
