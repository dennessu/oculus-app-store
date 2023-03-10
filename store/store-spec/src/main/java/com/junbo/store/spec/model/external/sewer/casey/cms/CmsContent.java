/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.cms;

import com.junbo.store.spec.model.external.sewer.casey.CaseyLink;

import java.util.Map;

/**
 * The CmsContent class.
 */
public class CmsContent {

    private CaseyLink self;

    private String label;

    private String description;

    private String status;

    private CaseyLink contentDefinition;

    private Map<String, ContentItem> contents;

    public CaseyLink getSelf() {
        return self;
    }

    public void setSelf(CaseyLink self) {
        this.self = self;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CaseyLink getContentDefinition() {
        return contentDefinition;
    }

    public void setContentDefinition(CaseyLink contentDefinition) {
        this.contentDefinition = contentDefinition;
    }

    public Map<String, ContentItem> getContents() {
        return contents;
    }

    public void setContents(Map<String, ContentItem> contents) {
        this.contents = contents;
    }
}
