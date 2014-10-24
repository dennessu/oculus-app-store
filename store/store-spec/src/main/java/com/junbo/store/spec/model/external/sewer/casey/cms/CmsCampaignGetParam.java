/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.cms;

import javax.ws.rs.QueryParam;

/**
 * The CmsCampaignGetParam class.
 */
public class CmsCampaignGetParam {

    @QueryParam("expand")
    private String expand;

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }
}
