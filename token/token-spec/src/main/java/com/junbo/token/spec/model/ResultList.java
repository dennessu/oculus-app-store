/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * result list model.
 * @param <T> result item
 */
@JsonPropertyOrder(value = {"results", "next"})
public class ResultList<T> {
    private String next;
    private List<T> results;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
