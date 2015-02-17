package com.junbo.common.cloudant.model;

/**
 * CloudantViewQueryOptions.
 */
public class CloudantViewQueryOptions {
    public static final Object HIGH_KEY = new Object();

    private Integer limit;
    private Integer skip;
    private Object startKey;
    private Object endKey;
    private boolean descending;
    private boolean includeDocs = true;
    private String cursor;

    public CloudantViewQueryOptions() {
    }

    public CloudantViewQueryOptions(CloudantViewQueryOptions options) {
        this.limit = options.limit;
        this.skip = options.skip;
        this.startKey = options.startKey;
        this.endKey = options.endKey;
        this.descending = options.descending;
        this.includeDocs = options.includeDocs;
        this.cursor = options.cursor;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Object getStartKey() {
        return startKey;
    }

    public void setStartKey(Object startKey) {
        this.startKey = startKey;
    }

    public Object getEndKey() {
        return endKey;
    }

    public void setEndKey(Object endKey) {
        this.endKey = endKey;
    }

    public boolean getDescending() {
        return descending;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public boolean getIncludeDocs() {
        return includeDocs;
    }

    public boolean isIncludeDocs() {
        return includeDocs;
    }

    public void setIncludeDocs(boolean includeDocs) {
        this.includeDocs = includeDocs;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
