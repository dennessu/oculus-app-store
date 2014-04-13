/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.catalog.spec.model.common.ExtensibleProperties;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.common.jackson.annotation.ItemRevisionId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.UserId;


/**
 * Item revision.
 */
public class ItemRevision extends BaseRevisionModel {
    @ItemRevisionId
    @JsonProperty("self")
    private Long revisionId;

    @UserId
    @JsonProperty("developer")
    private Long ownerId;

    @OfferId
    @JsonProperty("item")
    private Long itemId;
    private String type;
    private String sku;
    private Price msrp;
    private ExtensibleProperties digitalProperties;
    private ExtensibleProperties ewalletProperties;
    private ExtensibleProperties physicalProperties;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Price getMsrp() {
        return msrp;
    }

    public void setMsrp(Price msrp) {
        this.msrp = msrp;
    }

    public ExtensibleProperties getDigitalProperties() {
        return digitalProperties;
    }

    public void setDigitalProperties(ExtensibleProperties digitalProperties) {
        this.digitalProperties = digitalProperties;
    }

    public ExtensibleProperties getEwalletProperties() {
        return ewalletProperties;
    }

    public void setEwalletProperties(ExtensibleProperties ewalletProperties) {
        this.ewalletProperties = ewalletProperties;
    }

    public ExtensibleProperties getPhysicalProperties() {
        return physicalProperties;
    }

    public void setPhysicalProperties(ExtensibleProperties physicalProperties) {
        this.physicalProperties = physicalProperties;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return itemId;
    }
}
