/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.cms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * The CmsContentSlot class.
 */
public class CmsContentSlot {

    private String description;

    @JsonIgnore
    private Map<String, ContentItem> contents;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, ContentItem> getContents() {
        if (contents == null) {
            contents = new HashMap<>();
        }
        return contents;
    }

    public void setContents(Map<String, ContentItem> contents) {
        this.contents = contents;
    }
}
