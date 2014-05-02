package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.FlowException
import com.junbo.langur.core.webflow.action.ActionList
import com.junbo.langur.core.webflow.definition.EndStateDef
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class EndStateExecutor implements StateExecutor {

    private final EndStateDef stateDef

    EndStateExecutor(EndStateDef stateDef) {
        if (stateDef == null) {
            throw new IllegalArgumentException('stateDef is null')
        }

        this.stateDef = stateDef
    }

    @Override
    Promise<FlowEvent> start(ExecutionContext context) {
        if (context == null) {
            throw new IllegalArgumentException('context is null')
        }

        def flowState = context.conversation.flowStack.last()

        assert flowState.stateId == null
        flowState.stateId = stateDef.id

        def actionContext = context.newActionContext()
        def entryActions = new ActionList(stateDef.entryActions)
        return entryActions.execute(actionContext).then {
            flowState.stateId = null

            actionContext.view = stateDef.view
            return Promise.pure(new EndFlowEvent(stateDef.id))
        }
    }

    @Override
    Promise<FlowEvent> resume(ExecutionContext context, String trigger) {
        throw new FlowException('Invalid operation')
    }
}
