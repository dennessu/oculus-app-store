/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.Set;

/**
 * EntitlementSearchParam Model.
 */
public class EntitlementSearchParam {
    @QueryParam("userId")
    private Long userId;
    @QueryParam("developerId")
    private Long developerId;
    @QueryParam("offerIds")
    private Set<Long> offerIds;
    @QueryParam("type")
    private String type;
    @QueryParam("status")
    private String status;

    @QueryParam("groups")
    private Set<String> groups;
    @QueryParam("tags")
    private Set<String> tags;
    @QueryParam("definitionIds")
    private Set<Long> definitionIds;

    private Date startGrantTime;
    private Date endGrantTime;
    private Date startExpirationTime;
    private Date endExpirationTime;

    private Date lastModifiedTime;

    public EntitlementSearchParam() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public Set<Long> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(Set<Long> offerIds) {
        this.offerIds = offerIds;
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

    public Date getStartGrantTime() {
        return startGrantTime;
    }

    public void setStartGrantTime(Date startGrantTime) {
        this.startGrantTime = startGrantTime;
    }

    public Date getEndGrantTime() {
        return endGrantTime;
    }

    public void setEndGrantTime(Date endGrantTime) {
        this.endGrantTime = endGrantTime;
    }

    public Date getStartExpirationTime() {
        return startExpirationTime;
    }

    public void setStartExpirationTime(Date startExpirationTime) {
        this.startExpirationTime = startExpirationTime;
    }

    public Date getEndExpirationTime() {
        return endExpirationTime;
    }

    public void setEndExpirationTime(Date endExpirationTime) {
        this.endExpirationTime = endExpirationTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Set<Long> getDefinitionIds() {
        return definitionIds;
    }

    public void setDefinitionIds(Set<Long> definitionIds) {
        this.definitionIds = definitionIds;
    }

    /**
     * a builder for SearchParam.
     */
    public static class Builder {
        private Long userId;
        private Long developerId;
        private Set<Long> offerIds;
        private String type;
        private String status;

        private Set<String> groups;
        private Set<String> tags;
        private Set<Long> definitionIds;

        private Date startGrantTime;
        private Date endGrantTime;
        private Date startExpirationTime;
        private Date endExpirationTime;

        private Date lastModifiedTime;

        public Builder(Long userId, Long developerId) {
            this.userId = userId;
            this.developerId = developerId;
        }

        public Builder offerIds(Set<Long> val) {
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

        public Builder startGrantTime(Date val) {
            startGrantTime = val;
            return this;
        }

        public Builder endGrantTime(Date val) {
            endGrantTime = val;
            return this;
        }

        public Builder startExpirationTime(Date val) {
            startExpirationTime = val;
            return this;
        }

        public Builder endExpirationTime(Date val) {
            endExpirationTime = val;
            return this;
        }

        public Builder lastModifiedTime(Date val) {
            lastModifiedTime = val;
            return this;
        }

        public Builder definitionIds(Set<Long> val) {
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
