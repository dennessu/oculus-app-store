/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

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
    private String cmsPageSearch;

    @JsonIgnore
    private String cmsSlot;

    @JsonIgnore
    private Map<String, String> cmsNames;

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

    public String getCmsPageSearch() {
        return cmsPageSearch;
    }

    public void setCmsPageSearch(String cmsPageSearch) {
        this.cmsPageSearch = cmsPageSearch;
    }

    public String getCmsSlot() {
        return cmsSlot;
    }

    public void setCmsSlot(String cmsSlot) {
        this.cmsSlot = cmsSlot;
    }

    public Map<String, String> getCmsNames() {
        return cmsNames;
    }

    public void setCmsNames(Map<String, String> cmsNames) {
        this.cmsNames = cmsNames;
    }

    public SectionInfo toSectionInfo() {
        SectionInfo sectionInfo = new SectionInfo();
        sectionInfo.setCategory(category);
        sectionInfo.setCriteria(criteria);
        sectionInfo.setName(name);
        return sectionInfo;
    }
}
