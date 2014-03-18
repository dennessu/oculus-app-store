/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.model.Link;

import java.util.List;

/**
 * Result list.
 * @param <T>.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultList<T> {

    @JsonProperty("self")
    private Link self;

    private List<T> items;

    private Boolean hasNext;

    @JsonProperty("next")
    private Link next;

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

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }
}
