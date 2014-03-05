/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Java doc.
 */
public class ActionContext implements Serializable {

    private final Map<String, Serializable> properties;

    private Action currentAction;

    private ActionScope actionScope;

    public ActionContext() {
        this.properties = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T get(String propertyName) {
        return (T) properties.get(propertyName);
    }

    public <T extends Serializable> void set(String propertyName, T value) {
        properties.put(propertyName, value);
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    public ActionScope getActionScope() {
        return actionScope;
    }

    void setActionScope(ActionScope actionScope) {
        this.actionScope = actionScope;
    }
}
