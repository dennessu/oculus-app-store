/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.cms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.store.spec.model.external.casey.BaseCaseyModel;
import com.junbo.store.spec.model.external.casey.CaseyLink;

/**
 * The Placement class.
 */
public class Placement extends BaseCaseyModel {

    private CaseyLink page;

    private String slot;

    private CaseyLink content;

    @JsonIgnore
    private CmsContent contentData;

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

    public CaseyLink getContent() {
        return content;
    }

    public void setContent(CaseyLink content) {
        this.content = content;
    }

    public CmsContent getContentData() {
        return contentData;
    }

    public void setContentData(CmsContent contentData) {
        this.contentData = contentData;
    }
}
