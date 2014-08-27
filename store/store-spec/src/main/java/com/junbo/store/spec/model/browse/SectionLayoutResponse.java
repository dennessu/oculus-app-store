/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Item;
import com.junbo.store.spec.model.browse.document.SectionInfo;

import java.util.List;

/**
 * The SectionLayoutResponse class.
 */
public class SectionLayoutResponse {

    private List<SectionInfo> breadcrumbs;

    private List<SectionInfo> children;

    private String title;

    private Boolean ordered;

    private List<Item> items;

    private PageMeta next;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public PageMeta getNext() {
        return next;
    }

    public void setNext(PageMeta next) {
        this.next = next;
    }
}