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
    @ApiModelProperty(position = 1, required = true, value = "event name")
    private String name;
    @ApiModelProperty(position = 2, required = true, value = "actions")
    private List<Action> actions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
