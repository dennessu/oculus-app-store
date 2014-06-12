/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

/**
 * conditions to perform the action.
 */
public class ActionCondition {
    private Integer fromCycle;
    private Integer toCycle;
    private Integer extendDuration;
    private String extendDurationUnit;

    public Integer getFromCycle() {
        return fromCycle;
    }

    public void setFromCycle(Integer fromCycle) {
        this.fromCycle = fromCycle;
    }

    public Integer getToCycle() {
        return toCycle;
    }

    public void setToCycle(Integer toCycle) {
        this.toCycle = toCycle;
    }

    public Integer getExtendDuration() {
        return extendDuration;
    }

    public void setExtendDuration(Integer extendDuration) {
        this.extendDuration = extendDuration;
    }

    public String getExtendDurationUnit() {
        return extendDurationUnit;
    }

    public void setExtendDurationUnit(String extendDurationUnit) {
        this.extendDurationUnit = extendDurationUnit;
    }
}
