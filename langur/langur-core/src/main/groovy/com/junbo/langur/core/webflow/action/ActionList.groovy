package com.junbo.langur.core.webflow.action

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/27/14.
 */
@CompileStatic
class ActionList implements Action {

    private final List<Action> actions

    ActionList(List<Action> actions) {
        if (actions == null) {
            throw new IllegalArgumentException('actions is null')
        }

        this.actions = actions
    }

    Promise<ActionResult> execute(ActionContext context) {
        if (context == null) {
            throw new IllegalArgumentException('context is null')
        }

        def iter = actions.iterator()

        Closure process = null
        process = {
            if (!iter.hasNext()) {
                return Promise.pure(new ActionResult('void'))
            }

            def action = iter.next()
            return action.execute(context).then(process)
        }

        return process()
    }
}
