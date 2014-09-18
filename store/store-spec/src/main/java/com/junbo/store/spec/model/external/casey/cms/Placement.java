/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.cms;

import com.junbo.store.spec.model.external.casey.BaseCaseyModel;
import com.junbo.store.spec.model.external.casey.CaseyLink;

/**
 * The Placement class.
 */
public class Placement extends BaseCaseyModel {

    private CaseyLink page;

    private String slot;

    private CmsContent content;

    public CaseyLink getPage() {
        return page;
    }

    public void setPage(CaseyLink page) {
        this.page = page;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public CmsContent getContent() {
        return content;
    }

    public void setContent(CmsContent content) {
        this.content = content;
    }
}
