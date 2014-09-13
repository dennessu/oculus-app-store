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

    /**
     * The SectionType enum.
     */
    public enum SectionType {
        CmsSection,
        CategorySection
    }

    private String name;

    private String category;

    private String criteria;

    private List<SectionInfoNode> children;

    @JsonIgnore
    private SectionType sectionType;

    @JsonIgnore
    private SectionInfoNode parent;

    @JsonIgnore
    private Boolean ordered;

    @JsonIgnore
    private String categoryId;

    @JsonIgnore
    private String cmsPage;

    @JsonIgnore
    private String cmsSlot;

    public SectionInfoNode() {
    }

    public SectionInfoNode(SectionInfo sectionInfo) {
        name = sectionInfo.getName();
        category = sectionInfo.getCategory();
        criteria = sectionInfo.getCriteria();
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

    public List<SectionInfoNode> getChildren() {
        return children;
    }

    public void setChildren(List<SectionInfoNode> children) {
        this.children = children;
    }

    public SectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(SectionType sectionType) {
        this.sectionType = sectionType;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }

    public SectionInfoNode getParent() {
        return parent;
    }

    public void setParent(SectionInfoNode parent) {
        this.parent = parent;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCmsPage() {
        return cmsPage;
    }

    public void setCmsPage(String cmsPage) {
        this.cmsPage = cmsPage;
    }

    public String getCmsSlot() {
        return cmsSlot;
    }

    public void setCmsSlot(String cmsSlot) {
        this.cmsSlot = cmsSlot;
    }

    public SectionInfo toSectionInfo() {
        SectionInfo sectionInfo = new SectionInfo();
        sectionInfo.setCategory(category);
        sectionInfo.setCriteria(criteria);
        sectionInfo.setName(name);
        return sectionInfo;
    }
}
