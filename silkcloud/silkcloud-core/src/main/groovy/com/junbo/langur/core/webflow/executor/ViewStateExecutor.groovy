package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.FlowException
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionList
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.langur.core.webflow.definition.TransitionDef
import com.junbo.langur.core.webflow.definition.ViewStateDef
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class ViewStateExecutor implements StateExecutor {

    private final ViewStateDef stateDef

    ViewStateExecutor(ViewStateDef stateDef) {
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
        actionContext.viewScope.clear()

        def entryActions = new ActionList(stateDef.entryActions)
        return entryActions.execute(actionContext).then {
            def renderActions = new ActionList(stateDef.renderActions)
            return renderActions.execute(actionContext).then {
                actionContext.view = stateDef.view
                actionContext.flashScope.clear()

                return Promise.pure(new VoidEvent())
            }
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

        if (transitionDef == null && !trigger.empty) { // for empty trigger, just re-render the page.
            throw new FlowException("no transition found for event $trigger")
        }

        def actionContext = context.newActionContext()
        Iterator<Action> iter = transitionDef == null ? null : transitionDef.actions.iterator()

        Closure process = null
        process = {
            if (iter == null) {
                return Promise.pure(false)
            }

            if (!iter.hasNext()) {
                return Promise.pure(true)
            }

            def action = iter.next()
            return action.execute(actionContext).then { ActionResult result ->
                if (result != null && result.id in ['success', 'yes', 'true']) {
                    return process()
                }

                return Promise.pure(false)
            }
        }

        return process().then { Boolean result ->
            if (result) {
                def exitActions = new ActionList(stateDef.exitActions)
                return exitActions.execute(actionContext).then {
                    actionContext.viewScope.clear()
                    flowState.stateId = null

                    return Promise.pure(new StartStateEvent(transitionDef.to))
                }
            } else {
                def renderActions = new ActionList(stateDef.renderActions)
                return renderActions.execute(actionContext).then {
                    actionContext.view = stateDef.view
                    actionContext.flashScope.clear()

                    return Promise.pure(new VoidEvent())
                }
            }
        }
    }
}
