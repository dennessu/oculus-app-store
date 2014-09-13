/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.cms;

import com.junbo.store.spec.model.external.casey.BaseCaseyModel;

import java.util.Map;

/**
 * The CmsPage class.
 */
public class CmsPage extends BaseCaseyModel {

    private String label;
    private String path;
    private Map<String, CmsContentSlot> slots;

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
