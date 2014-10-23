/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.cms;

import com.junbo.store.spec.model.external.sewer.casey.CaseyLink;

import java.util.Map;

/**
 * The CmsPage class.
 */
public class CmsPage {

    private CaseyLink self;
    private String label;
    private String path;
    private Map<String, CmsContentSlot> slots;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, CmsContentSlot> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, CmsContentSlot> slots) {
        this.slots = slots;
    }
}
