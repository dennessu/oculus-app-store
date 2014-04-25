/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Event.
 */
public class Event {
    @ApiModelProperty(position = 1, required = true, value = "event name",
            allowableValues = "PURCHASE, CYCLE, CANCEL")
    private String type;
    @ApiModelProperty(position = 2, required = true, value = "actions")
    private List<Action> actions;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
