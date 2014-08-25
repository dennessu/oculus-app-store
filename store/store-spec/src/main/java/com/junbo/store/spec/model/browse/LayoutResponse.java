/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Document;
import com.junbo.store.spec.model.browse.document.LayoutLink;

import java.util.List;

/**
 * The LayoutResponse class.
 */
public class LayoutResponse {

    private List<LayoutLink> breadcrumbs;

    private List<LayoutLink> categories;

    private String title;

    private Document contents;

    public List<LayoutLink> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<LayoutLink> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public List<LayoutLink> getCategories() {
        return categories;
    }

    public void setCategories(List<LayoutLink> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Document getContents() {
        return contents;
    }

    public void setContents(Document contents) {
        this.contents = contents;
    }
}
