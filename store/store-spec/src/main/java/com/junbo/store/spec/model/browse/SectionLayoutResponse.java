/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.SectionInfo;

import java.util.List;

/**
 * The SectionLayoutResponse class.
 */
public class SectionLayoutResponse {

    private List<SectionInfo> breadcrumbs;

    private List<SectionInfo> children;

    private String name;

    private String category;

    private String criteria;

    private Boolean ordered;

    public List<SectionInfo> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<SectionInfo> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public List<SectionInfo> getChildren() {
        return children;
    }

    public void setChildren(List<SectionInfo> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }
}

