/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.cms;

import javax.ws.rs.QueryParam;

/**
 * The CmsPageGetParams class.
 */
public class CmsPageGetParams {

    @QueryParam("path")
    private String path;

    @QueryParam("label")
    private String label;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
