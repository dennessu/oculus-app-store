/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.model.Link;

import javax.validation.Valid;
import java.util.List;

/**
 * The CaseyResults class.
 */
public class CaseyResults<T> {

    private Link self;

    @JsonProperty("results")
    @Valid
    private List<T> items;

    @JsonProperty("cursor")
    private JsonNode rawCursor;

    @JsonIgnore
    private String cursorString;

    private Integer count;

    private Long totalCount;

    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public JsonNode getRawCursor() {
        return rawCursor;
    }

    public void setRawCursor(JsonNode rawCursor) {
        this.rawCursor = rawCursor;
    }

    public String getCursorString() {
        return cursorString;
    }

    public void setCursorString(String cursorString) {
        this.cursorString = cursorString;
    }
}
