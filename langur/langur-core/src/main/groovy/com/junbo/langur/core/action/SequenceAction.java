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
public class SequenceAction implements Action {

    private final List<Action> actions;

    private final ActionExecutor executor;

    public SequenceAction(List<Action> actions, ActionExecutor executor) {
        assert actions != null : "actions is null";
        assert executor != null : "executor is null";

        this.actions = actions;
        this.executor = executor;
    }

    @Override
    public Promise<ActionResult> execute(final ActionContext context) {

        final int[] actionIndex = {-1};

        final Promise.Func<ActionResult, Promise<ActionResult>> postAction =
                new Promise.Func<ActionResult, Promise<ActionResult>>() {
                    @Override
                    public Promise<ActionResult> apply(ActionResult previousResult) {
                        assert previousResult != null : "previousResult is null";

                        if (ActionResult.NEXT.equals(previousResult)) {

                            actionIndex[0]++;
                            if (actionIndex[0] < actions.size()) {
                                return executor.execute(actions.get(actionIndex[0]), context).then(this);
                            }
                            else {
                                return Promise.pure(ActionResult.NEXT);
                            }
                        }
                        else if (ActionResult.END.equals(previousResult)) {

                            return Promise.pure(ActionResult.END);
                        }
                        else if (previousResult.getNextAction() != null) {

                            actionIndex[0] = getNextActionIndex(previousResult.getNextAction());
                            return executor.execute(actions.get(actionIndex[0]), context).then(this);
                        }
                        else {
                            throw new RuntimeException("actionResult " + previousResult + " not supported");
                        }
                    }
                };

        return Promise.pure(ActionResult.NEXT).then(postAction);
    }

    private int getNextActionIndex(String nextActionName) {

        for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {
            Action action = actions.get(actionIndex);

            if (action instanceof NamedAction) {
                String name = ((NamedAction) action).getName();

                if (name.equals(nextActionName)) {
                    return actionIndex;
                }
            }
        }

        throw new RuntimeException("nextAction " + nextActionName + " not found");
    }
}
