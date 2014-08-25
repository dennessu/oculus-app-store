/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import javax.ws.rs.QueryParam;

/**
 * The SectionLayoutRequest class.
 */
public class SectionLayoutRequest {

    @QueryParam("category")
    private String category;

    @QueryParam("criteria")
    private String criteria;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}
