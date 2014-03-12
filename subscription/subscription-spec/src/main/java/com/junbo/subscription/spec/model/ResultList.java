/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.model;

import java.util.List;


/**
 * result list.
 * @param <T> the entity for list
 */
public class ResultList<T> {
    private PageMetadata page;
    private List<T> content;

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
