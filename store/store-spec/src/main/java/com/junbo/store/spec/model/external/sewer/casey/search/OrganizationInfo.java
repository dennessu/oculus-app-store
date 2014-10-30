/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OrganizationId;

/**
 * The OrganizationInfo class.
 */
public class OrganizationInfo {

    private String href;

    @JsonProperty("id")
    private String organizationId;

    private String name;

    private OrganizationId self;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationId getSelf() {
        return self;
    }

    public void setSelf(OrganizationId self) {
        this.self = self;
    }
}
