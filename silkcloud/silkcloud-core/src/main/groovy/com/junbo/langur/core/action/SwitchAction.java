/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.action;

import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Java doc.
 */
public class SwitchAction implements Action {

    private final Action predicate;

    private final List<NamedAction> branches;

    private final Action defaultBranch;

    private final ActionExecutor executor;

    public SwitchAction(Action predicate, List<NamedAction> branches, ActionExecutor executor) {
        this(predicate, branches, null, executor);
    }

    public SwitchAction(Action predicate, List<NamedAction> branches, Action defaultBranch, ActionExecutor executor) {
        assert predicate != null : "predicate is null";
        assert branches != null : "branches is null";
        assert executor != null : "executor is null";

        this.predicate = predicate;
        this.branches = branches;
        this.defaultBranch = defaultBranch;
        this.executor = executor;
    }

    @Override
    public Promise<ActionResult> execute(final ActionContext context) {

        Promise<ActionResult> result = executor.execute(predicate, context);

        result = result.then(new Promise.Func<ActionResult, Promise<ActionResult>>() {
            @Override
            public Promise<ActionResult> apply(ActionResult previousResult) {

                if (ActionResult.NEXT.equals(previousResult)) {

                    return Promise.pure(ActionResult.NEXT);
                }
                else if (ActionResult.END.equals(previousResult)) {

                    return Promise.pure(ActionResult.END);
                }
                else if (previousResult.getNextAction() != null) {

                    Action branch = getNextAction(previousResult.getNextAction());
                    return executor.execute(branch, context);
                }
                else {
                    throw new RuntimeException("actionResult " + previousResult + " not supported");
                }
            }
        });

        return result;
    }

    private Action getNextAction(String nextActionName) {

        for (NamedAction action : branches) {
            String name = action.getName();

            if (name.equals(nextActionName)) {
                return action;
            }
        }

        if (defaultBranch != null) {
            return defaultBranch;
        }

        throw new RuntimeException("nextAction " + nextActionName + " not found");
    }
}
