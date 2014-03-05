/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.action;

import com.junbo.langur.core.promise.Promise;

/**
 * Java doc.
 */
public class DefaultActionExecutor implements ActionExecutor {

    public Promise<ActionResult> execute(Action action, final ActionContext context) {
        context.setCurrentAction(action);
        context.setActionScope(new ActionScope());

        // do more pre actions. e.g. record start time.

        Promise<ActionResult> promise = action.execute(context);
        assert promise != null : "promise is null";

        return promise.then(new Promise.Func<ActionResult, Promise<ActionResult>>() {
            @Override
            public Promise<ActionResult> apply(ActionResult actionResult) {
                assert actionResult != null : "actionResult is null";

                context.setCurrentAction(null);
                context.setActionScope(null);

                // do more post actions. e.g. record end time.
                return Promise.pure(actionResult);
            }
        });
    }
}
