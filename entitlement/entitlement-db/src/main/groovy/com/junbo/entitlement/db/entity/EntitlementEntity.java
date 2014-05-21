/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.CloudantDeserialize;
import com.junbo.common.cloudant.json.annotations.CloudantSerialize;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.util.Identifiable;
import com.junbo.entitlement.db.entity.def.DateDeserializer;
import com.junbo.entitlement.db.entity.def.DateSerializer;
import com.junbo.entitlement.db.entity.def.MapJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Entitlement Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement")
@TypeDefs(@TypeDef(name = "json-map", typeClass = MapJsonUserType.class))
public class EntitlementEntity extends Entity implements CloudantEntity, Identifiable<EntitlementId> {
    private Long userId;
    private Boolean isBanned;
    private Long itemId;
    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date grantTime;
    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date expirationTime;
    private Integer useCount;
    private String type;
    private Map<String, JsonNode> futureExpansion;
    private UUID trackingUuid;

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "item_id")
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Column(name = "grant_time")
    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    @Column(name = "expiration_time")
    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Column(name = "use_count")
    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    @Column(name = "is_banned")
    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "future_expansion")
    @Type(type = "json-map")
    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
    }

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return userId;
    }

    @Override
    @Transient
    public EntitlementId getId() {
        return new EntitlementId(this.getpId());
    }

    @Override
    @Transient
    public void setId(EntitlementId id) {
        this.setpId(id.getValue());
    }
}
