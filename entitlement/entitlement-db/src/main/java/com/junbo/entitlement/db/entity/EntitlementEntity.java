/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.entitlement.spec.def.EntitlementStatus;
import com.junbo.entitlement.spec.def.EntitlementType;
import com.junbo.entitlement.db.entity.def.IdentifiableType;
import com.junbo.entitlement.db.entity.def.ListJsonUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entitlement Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement")
@TypeDefs(@TypeDef(name="json-string", typeClass=ListJsonUserType.class))
public class EntitlementEntity extends Entity {
    private Long entitlementId;
    private Integer rev;
    private Long userId;
    private List<String> inAppContext;
    private EntitlementStatus status;
    private String statusReason;
    private Long entitlementDefinitionId;
    private EntitlementType type;
    private String group;
    private String tag;
    private Date grantTime;
    private Date expirationTime;
    private Integer useCount;

    @Id
    @Column(name = "entitlement_id")
    public Long getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(Long entitlementId) {
        this.entitlementId = entitlementId;
    }

    @Version
    @Column(name = "rev")
    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "status")
    @Type(type = IdentifiableType.TYPE)
    public EntitlementStatus getStatus() {
        return status;
    }

    public void setStatus(EntitlementStatus status) {
        this.status = status;
    }

    @Column(name = "status_reason")
    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    @Column(name = "entitlement_definition_id")
    public Long getEntitlementDefinitionId() {
        return entitlementDefinitionId;
    }

    public void setEntitlementDefinitionId(Long entitlementDefinitionId) {
        this.entitlementDefinitionId = entitlementDefinitionId;
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

    @Column(name = "in_app_context")
    @Type(type = "json-string")
    public List<String> getInAppContext() {
        return inAppContext;
    }

    public void setInAppContext(List<String> inAppContext) {
        this.inAppContext = inAppContext;
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

    @JsonIgnore
    @Transient
    @Override
    public Long getId() {
        return this.entitlementId;
    }

    @Override
    public void setId(Long id) {
        this.entitlementId = id;
    }

    @JsonIgnore
    @Transient
    @Override
    public Long getShardMasterId() {
        return userId;
    }
}
