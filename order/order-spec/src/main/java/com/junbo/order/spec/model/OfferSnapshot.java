/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by LinYi on 2014/8/28.
 */
public class OfferSnapshot extends ResourceMetaForDualWrite<Long> {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long orderId;

    @ApiModelProperty(required = true, position = 10, value = "Link to the offer resource.")
    private OfferId offer;

    @ApiModelProperty(required = true, position = 20, value = "Link to the offerRevision resource.")
    private OfferRevisionId offerRevision;

    @ApiModelProperty(required = true, position = 30, value = "Array of offerSnapshots of the sub-offers.")
    private List<OfferSnapshot> subOfferSanpshots;

    @ApiModelProperty(required = true, position = 40, value = " Array of itemSnapshots of the items included in this offer.")
    private List<ItemSnapshot> itemSnapshots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }

    public OfferRevisionId getOfferRevision() {
        return offerRevision;
    }

    public void setOfferRevision(OfferRevisionId offerRevision) {
        this.offerRevision = offerRevision;
    }

    public List<OfferSnapshot> getSubOfferSanpshots() {
        return subOfferSanpshots;
    }

    public void setSubOfferSanpshots(List<OfferSnapshot> subOfferSanpshots) {
        this.subOfferSanpshots = subOfferSanpshots;
    }

    public List<ItemSnapshot> getItemSnapshots() {
        return itemSnapshots;
    }

    public void setItemSnapshots(List<ItemSnapshot> itemSnapshots) {
        this.itemSnapshots = itemSnapshots;
    }
}
