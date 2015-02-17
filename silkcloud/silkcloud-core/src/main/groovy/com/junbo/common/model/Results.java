/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 * The results of GET /resources APIs.
 * @param <T> The type of the target resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Results<T> {

    @ApiModelProperty(position = 1, required = true, value = "The link to the current result set")
    @JsonProperty("self")
    private Link self;

    @ApiModelProperty(position = 2, required = true, value = "The result set items")
    @JsonProperty("results")
    private List<T> items;

    @ApiModelProperty(position = 3, required = true, value = "The link to next set of results if there are more items")
    @JsonProperty("next")
    private Link next;

    private Long total;

    @JsonIgnore
    private boolean hasNext;

    @JsonIgnore
    private String nextCursor;
    @JsonIgnore
    private boolean usingNextCursor;        // temp variable to co-exist with old skip based cursor

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

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public boolean isUsingNextCursor() {
        return usingNextCursor;
    }

    public void setUsingNextCursor(boolean usingNextCursor) {
        this.usingNextCursor = usingNextCursor;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
