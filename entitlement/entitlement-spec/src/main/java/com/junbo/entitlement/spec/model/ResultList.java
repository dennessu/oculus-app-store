/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * ResultList Model.
 *
 * @param <T> result type.
 */
@JsonPropertyOrder(value = {"criteria", "next"})
public class ResultList<T> {
    private String next;
    private List<T> criteria;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public List<T> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<T> criteria) {
        this.criteria = criteria;
    }
}
