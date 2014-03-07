/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.db.entity.def.IdentifiableType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * EntitlementDefinition Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement_definition")
public class EntitlementDefinitionEntity extends Entity {
    private Long entitlementDefinitionId;
    private Long developerId;
    private EntitlementType type;
    private String group;
    private String tag;
    private String status;

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
    @Type(type = IdentifiableType.TYPE)
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

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Transient
    @Override
    public Long getShardMasterId() {
        return developerId;
    }
}
