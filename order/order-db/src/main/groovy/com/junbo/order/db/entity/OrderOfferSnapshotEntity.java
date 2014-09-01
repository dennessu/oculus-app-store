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
@Table(name= "ORDER_OFFER_SNAPSHOT")
public class OrderOfferSnapshotEntity extends CommonDbEntityWithDate {
    private Long offerSnapshotId;
    private Long orderId;
    private String offerId;
    private String offerRevisionId;

    @Id
    @Column(name = "OFFER_SNAPSHOT_ID")
    public Long getOfferSnapshotId() {
        return offerSnapshotId;
    }

    public void setOfferSnapshotId(Long offerSnapshotId) {
        this.offerSnapshotId = offerSnapshotId;
    }

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "OFFER_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @Column(name = "OFFER_REVISION_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getOfferRevisionId() {
        return offerRevisionId;
    }

    public void setOfferRevisionId(String offerRevisionId) {
        this.offerRevisionId = offerRevisionId;
    }

    @Override
    @Transient
    public Long getShardId() {
        return getOfferSnapshotId();
    }
}
