/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * The SectionInfoNode class.
 */
public class SectionInfoNode {

    private String name;

    private String category;

    private String criteria;

    @JsonIgnore
    private Boolean ordered;

    private List<SectionInfoNode> children;

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

    public List<SectionInfoNode> getChildren() {
        return children;
    }

    public void setChildren(List<SectionInfoNode> children) {
        this.children = children;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }

    public SectionInfo toSectionInfo() {
        SectionInfo sectionInfo = new SectionInfo();
        sectionInfo.setCategory(category);
        sectionInfo.setCriteria(criteria);
        sectionInfo.setName(name);
        return sectionInfo;
    }
}
