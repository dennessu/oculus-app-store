/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.search;

/**
 * The CaseyRating class.
 */
public class CaseyRating {

    private String type;
    private Double averagePercent;
    private Long count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getAveragePercent() {
        return averagePercent;
    }

    public void setAveragePercent(Double averagePercent) {
        this.averagePercent = averagePercent;
    }

}
