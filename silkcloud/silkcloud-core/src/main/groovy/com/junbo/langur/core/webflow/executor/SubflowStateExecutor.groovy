package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.FlowException
import com.junbo.langur.core.webflow.WebFlowErrors
import com.junbo.langur.core.webflow.action.ActionList
import com.junbo.langur.core.webflow.definition.SubflowStateDef
import com.junbo.langur.core.webflow.definition.TransitionDef
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class SubflowStateExecutor implements StateExecutor {

    private final SubflowStateDef stateDef

    SubflowStateExecutor(SubflowStateDef stateDef) {
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
            return Promise.pure(new StartFlowEvent(stateDef.subflow))
        }
    }

    @Override
    Promise<FlowEvent> resume(ExecutionContext context, String trigger) {
        if (context == null) {
            throw new IllegalArgumentException('context is null')
        }

        if (trigger == null) {
            throw new IllegalArgumentException('trigger is null')
        }

        def flowState = context.conversation.flowStack.last()
        assert flowState.stateId == stateDef.id

        def transitionDef = stateDef.transitions.find { TransitionDef transitionDef ->
            transitionDef.on == null || transitionDef.on == '*' || trigger == transitionDef.on
        }

        if (transitionDef == null) {
            throw WebFlowErrors.INSTANCE.invalidTransition(trigger).exception()
        }

        def actionContext = context.newActionContext()
        def exitActions = new ActionList(stateDef.exitActions)

        return exitActions.execute(actionContext).then {
            flowState.stateId = null

            return Promise.pure(new StartStateEvent(transitionDef.to))
        }
    }
}
