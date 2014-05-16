/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * EntitlementSearchParam Model.
 */
public class EntitlementSearchParam {
    @QueryParam("userId")
    private UserId userId;
    @QueryParam("type")
    private String type;
    @QueryParam("isActive")
    private Boolean isActive;
    @QueryParam("isSuspended")
    private Boolean isBanned;

    @QueryParam("itemIds")
    private Set<ItemId> itemIds;
    @QueryParam("hostItemId")
    private ItemId hostItemId;

    @QueryParam("startGrantTime")
    private String startGrantTime;
    @QueryParam("endGrantTime")
    private String endGrantTime;
    @QueryParam("startExpirationTime")
    private String startExpirationTime;
    @QueryParam("endExpirationTime")
    private String endExpirationTime;
    @QueryParam("lastModifiedTime")
    private String lastModifiedTime;

    public EntitlementSearchParam() {
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public Set<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    public ItemId getHostItemId() {
        return hostItemId;
    }

    public void setHostItemId(ItemId hostItemId) {
        this.hostItemId = hostItemId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    public String getStartGrantTime() {
        return startGrantTime;
    }

    public void setStartGrantTime(String startGrantTime) {
        this.startGrantTime = startGrantTime;
    }

    public String getEndGrantTime() {
        return endGrantTime;
    }

    public void setEndGrantTime(String endGrantTime) {
        this.endGrantTime = endGrantTime;
    }

    public String getStartExpirationTime() {
        return startExpirationTime;
    }

    public void setStartExpirationTime(String startExpirationTime) {
        this.startExpirationTime = startExpirationTime;
    }

    public String getEndExpirationTime() {
        return endExpirationTime;
    }

    public void setEndExpirationTime(String endExpirationTime) {
        this.endExpirationTime = endExpirationTime;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * a builder for SearchParam.
     */
    public static class Builder {
        private UserId userId;
        private String type;
        private Boolean isActive;
        private Boolean isBanned;

        private Set<ItemId> itemIds;
        private ItemId hostItemId;

        private String startGrantTime;
        private String endGrantTime;
        private String startExpirationTime;
        private String endExpirationTime;

        private String lastModifiedTime;

        public Builder(UserId userId) {
            this.userId = userId;
        }

        public Builder isActive(Boolean val) {
            isActive = val;
            return this;
        }

        public Builder isBanned(Boolean val) {
            isBanned = val;
            return this;
        }

        public Builder startGrantTime(String val) {
            startGrantTime = val;
            return this;
        }

        public Builder endGrantTime(String val) {
            endGrantTime = val;
            return this;
        }

        public Builder startExpirationTime(String val) {
            startExpirationTime = val;
            return this;
        }

        public Builder endExpirationTime(String val) {
            endExpirationTime = val;
            return this;
        }

        public Builder lastModifiedTime(String val) {
            lastModifiedTime = val;
            return this;
        }

        public Builder itemIds(Set<ItemId> val) {
            itemIds = val;
            return this;
        }

        public Builder hostItemId(ItemId val) {
            hostItemId = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public EntitlementSearchParam build() {
            return new EntitlementSearchParam(this);
        }
    }

    private EntitlementSearchParam(Builder builder) {
        userId = builder.userId;
        isActive = builder.isActive;
        isBanned = builder.isBanned;
        itemIds = builder.itemIds;
        hostItemId = builder.hostItemId;
        startGrantTime = builder.startGrantTime;
        endGrantTime = builder.endGrantTime;
        startExpirationTime = builder.startExpirationTime;
        endExpirationTime = builder.endExpirationTime;
        lastModifiedTime = builder.lastModifiedTime;
        type = builder.type;
    }
}
