/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.action;

/**
 * Java doc.
 */
public class ActionResult {

    public static final ActionResult NEXT = new ActionResult("NEXT");

    public static final ActionResult END = new ActionResult("END");

    private static final String ACTION_PREFIX = "action:";

    public static ActionResult nextAction(String nextAction) {
        assert nextAction != null : "nextAction is null";

        return new ActionResult(ACTION_PREFIX + nextAction);
    }

    private final String result;

    public ActionResult(String result) {
        assert result != null : "result is null";
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getNextAction() {
        if (result.startsWith(ACTION_PREFIX)) {
            return result.substring(ACTION_PREFIX.length());
        }

        return null;
    }

    @Override
    public String toString() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionResult that = (ActionResult) o;

        if (!result.equals(that.result)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return result.hashCode();
    }
}
