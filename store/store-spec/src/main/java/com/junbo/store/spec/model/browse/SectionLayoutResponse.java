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

    private String name;

    private Boolean ordered;

    private List<Item> items;

    private ListResponse.NextOption next;

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

    public ListResponse.NextOption getNext() {
        return next;
    }

    public void setNext(ListResponse.NextOption next) {
        this.next = next;
    }
}
