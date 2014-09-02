/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by LinYi on 2014/8/28.
 */
@Entity
@Table(name= "ORDER_OFFER_ITEM_SNAPSHOT")
public class OrderOfferItemSnapshotEntity extends CommonDbEntityWithDate {
    private Long itemSnapshotId;
    private Long offerSnapshotId;
    private String itemId;
    private String itemRevisionId;

    @Id
    @Column(name = "ITEM_SNAPSHOT_ID")
    public Long getItemSnapshotId() {
        return itemSnapshotId;
    }

    public void setItemSnapshotId(Long itemSnapshotId) {
        this.itemSnapshotId = itemSnapshotId;
    }

    @Column(name = "OFFER_SNAPSHOT_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOfferSnapshotId() {
        return offerSnapshotId;
    }

    public void setOfferSnapshotId(Long offerSnapshotId) {
        this.offerSnapshotId = offerSnapshotId;
    }

    @Column(name = "ITEM_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Column(name = "ITEM_REVISION_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getItemRevisionId() {
        return itemRevisionId;
    }

    public void setItemRevisionId(String itemRevisionId) {
        this.itemRevisionId = itemRevisionId;
    }

    @Override
    @Transient
    public Long getShardId() {
        return getItemSnapshotId();
    }
}
