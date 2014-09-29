/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.cms;

import com.junbo.store.spec.model.external.casey.CaseyLink;

/**
 * The CmsScheduleContent class.
 */
public class CmsScheduleContent {

    CaseyLink campaign;
    CmsContent content;

    public CaseyLink getCampaign() {
        return campaign;
    }

    public void setCampaign(CaseyLink campaign) {
        this.campaign = campaign;
    }

    public CmsContent getContent() {
        return content;
    }

    public void setContent(CmsContent content) {
        this.content = content;
    }
}
