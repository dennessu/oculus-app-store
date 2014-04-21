/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * EntitlementSearchParam Model.
 */
public class EntitlementSearchParam {
    @QueryParam("userId")
    private UserId userId;
    @QueryParam("developerId")
    private UserId developerId;
    @QueryParam("offerIds")
    private Set<OfferId> offerIds;
    @QueryParam("type")
    private String type;
    @QueryParam("status")
    private String status;

    @QueryParam("groups")
    private Set<String> groups;
    @QueryParam("tags")
    private Set<String> tags;
    @QueryParam("definitionIds")
    private Set<EntitlementDefinitionId> definitionIds;

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

    public UserId getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(UserId developerId) {
        this.developerId = developerId;
    }

    public Set<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(Set<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    public Set<EntitlementDefinitionId> getDefinitionIds() {
        return definitionIds;
    }

    public void setDefinitionIds(Set<EntitlementDefinitionId> definitionIds) {
        this.definitionIds = definitionIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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


    /**
     * a builder for SearchParam.
     */
    public static class Builder {
        private UserId userId;
        private UserId developerId;
        private Set<OfferId> offerIds;
        private String type;
        private String status;

        private Set<String> groups;
        private Set<String> tags;
        private Set<EntitlementDefinitionId> definitionIds;

        private String startGrantTime;
        private String endGrantTime;
        private String startExpirationTime;
        private String endExpirationTime;

        private String lastModifiedTime;

        public Builder(UserId userId, UserId developerId) {
            this.userId = userId;
            this.developerId = developerId;
        }

        public Builder offerIds(Set<OfferId> val) {
            offerIds = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder status(String val) {
            status = val;
            return this;
        }

        public Builder groups(Set<String> val) {
            groups = val;
            return this;
        }

        public Builder tags(Set<String> val) {
            tags = val;
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

        public Builder definitionIds(Set<EntitlementDefinitionId> val) {
            definitionIds = val;
            return this;
        }

        public EntitlementSearchParam build() {
            return new EntitlementSearchParam(this);
        }
    }

    private EntitlementSearchParam(Builder builder) {
        userId = builder.userId;
        developerId = builder.developerId;
        offerIds = builder.offerIds;
        type = builder.type;
        status = builder.status;
        definitionIds = builder.definitionIds;
        groups = builder.groups;
        tags = builder.tags;
        startGrantTime = builder.startGrantTime;
        endGrantTime = builder.endGrantTime;
        startExpirationTime = builder.startExpirationTime;
        endExpirationTime = builder.endExpirationTime;
        lastModifiedTime = builder.lastModifiedTime;
    }
}
