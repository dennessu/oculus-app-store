/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * EntitlementDefinition Entity.
 */
@Entity
@Table(name = "entitlement_definition")
public class EntitlementDefinitionEntity extends BaseEntity {
    private Long entitlementDefinitionId;
    private Long developerId;
    private EntitlementType type;
    private String group;
    private String tag;
    private Boolean consumable;
    private UUID trackingUuid;

    @Id
    @Column(name = "entitlement_definition_id")
    public Long getEntitlementDefinitionId() {
        return entitlementDefinitionId;
    }

    public void setEntitlementDefinitionId(Long entitlementDefinitionId) {
        this.entitlementDefinitionId = entitlementDefinitionId;
    }

    @Column(name = "developer_id")
    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    @Column(name = "type")
    public EntitlementType getType() {
        return type;
    }

    public void setType(EntitlementType type) {
        this.type = type;
    }

    @Column(name = "entitlement_group")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Column(name = "tag")
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Column(name = "consumable")
    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
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
    public Long getId() {
        return entitlementDefinitionId;
    }

    @Override
    public void setId(Long id) {
        this.entitlementDefinitionId = id;
    }
}
