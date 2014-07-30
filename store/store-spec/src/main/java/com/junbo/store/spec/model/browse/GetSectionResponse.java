/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Document;

import java.util.List;

/**
 * The GetSectionResponse class.
 */
public class GetSectionResponse {

    private List<Category> category;
    private String title;
    private Document contents;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
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
