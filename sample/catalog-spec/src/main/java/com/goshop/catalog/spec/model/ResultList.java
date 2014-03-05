package com.goshop.catalog.spec.model;

import java.util.List;

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