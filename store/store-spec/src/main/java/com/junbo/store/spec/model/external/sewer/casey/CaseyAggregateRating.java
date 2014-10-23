/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey;

/**
 * CaseyAggregateRating.
 */
public class CaseyAggregateRating {
    private Double average;
    private Long sum;
    private Long count;
    private Integer minimum;
    private Integer maximum;
    private Double variance;
    private Long[] histogram;
    private String resourceType;
    private String resourceId;
    private String type;
    private CaseyLink self;

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Double getVariance() {
        return variance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }

    public Long[] getHistogram() {
        return histogram;
    }

    public void setHistogram(Long[] histogram) {
        this.histogram = histogram;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CaseyLink getSelf() {
        return self;
    }

    public void setSelf(CaseyLink self) {
        this.self = self;
    }
}
