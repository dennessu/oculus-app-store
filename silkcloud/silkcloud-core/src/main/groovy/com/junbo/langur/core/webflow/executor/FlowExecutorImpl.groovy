package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.ConversationNotfFoundException
import com.junbo.langur.core.webflow.FlowException
import com.junbo.langur.core.webflow.WebFlowErrors
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionList
import com.junbo.langur.core.webflow.definition.FlowDefLoader
import com.junbo.langur.core.webflow.state.FlowState
import com.junbo.langur.core.webflow.state.StateRepository
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class FlowExecutorImpl implements FlowExecutor {

    private FlowDefLoader flowDefLoader

    private StateRepository stateRepository

    private StateExecutorFactory stateExecutorFactory

    @Required
    void setFlowDefLoader(FlowDefLoader flowDefLoader) {
        this.flowDefLoader = flowDefLoader
    }

    @Required
    void setStateRepository(StateRepository stateRepository) {
        this.stateRepository = stateRepository
    }

    @Required
    void setStateExecutorFactory(StateExecutorFactory stateExecutorFactory) {
        this.stateExecutorFactory = stateExecutorFactory
    }

    @Override
    Promise<ActionContext> start(String flowId, Map<String, Object> requestScope) {
        if (flowId == null) {
            throw new IllegalArgumentException('flowId is null')
        }

        if (requestScope == null) {
            throw new IllegalArgumentException('requestScope is null')
        }

        def conversation = stateRepository.newConversation()
        def executionContext = new ExecutionContext(
                conversation: conversation,
                requestScope: requestScope
        )

        return runEventLoop(executionContext, new StartFlowEvent(flowId))
    }

    @Override
    Promise<ActionContext> resume(String conversationId, String event, Map<String, Object> requestScope) {
        return resume(conversationId, event, requestScope, null)
    }

    @Override
    Promise<ActionContext> resume(String conversationId, String event, Map<String, Object> requestScope, String conversationVerifyCode) {
        if (conversationId == null) {
            throw new IllegalArgumentException('conversationId is null')
        }

        if (event == null) {
            throw new IllegalArgumentException('event is null')
        }

        if (requestScope == null) {
            throw new IllegalArgumentException('requestScope is null')
        }

        def conversation = stateRepository.loadConversation(conversationId)

        if (conversation == null) {
            throw new ConversationNotfFoundException()
        }

        if (conversation.ipAddress != JunboHttpContext.requestIpAddress) {
            throw WebFlowErrors.INSTANCE.ipViolation().exception()
        }

        if (StringUtils.hasText(conversation.conversationVerifyCode)) {
            if (conversation.conversationVerifyCode != conversationVerifyCode) {
                throw WebFlowErrors.INSTANCE.conversationVerifyFailed().exception()
            }
        }

        def executionContext = new ExecutionContext(
                conversation: conversation,
                requestScope: requestScope
        )

        return runEventLoop(executionContext, new ResumeStateEvent(event))
    }

    private Promise<ActionContext> runEventLoop(ExecutionContext context, FlowEvent event) {
        if (event instanceof VoidEvent) {
            stateRepository.persistConversation(context.conversation)
            return Promise.pure(context.newActionContext())
        }

        return handleEvent(context, event).then { FlowEvent nextEvent ->
            return runEventLoop(context, nextEvent)
        }
    }

    private Promise<FlowEvent> handleEvent(ExecutionContext context, FlowEvent rawEvent) {
        if (rawEvent instanceof StartFlowEvent) {
            def event = (StartFlowEvent) rawEvent
            def flowId = event.flowId

            def flowDef = flowDefLoader.loadFlowDef(flowId)
            def stateDef = flowDef.states.first()
            def stateId = stateDef.id

            def flowState = new FlowState(
                    flowId: flowId,
                    stateId: (String) null,
                    scope: [:]
            )

            context.conversation.flowStack.add(flowState)

            def actionContext = context.newActionContext()
            def startActions = new ActionList(flowDef.startActions)
            return startActions.execute(actionContext).then {
                return Promise.pure(new StartStateEvent(stateId))
            }
        }

        if (rawEvent instanceof StartStateEvent) {
            def event = (StartStateEvent) rawEvent
            def stateId = event.stateId

            def flowState = context.conversation.flowStack.last()
            def stateDef = flowDefLoader.loadStateDef(flowState.flowId, stateId)

            def stateExecutor = stateExecutorFactory.getStateExecutor(stateDef)
            return stateExecutor.start(context)
        }

        if (rawEvent instanceof ResumeStateEvent) {
            def event = (ResumeStateEvent) rawEvent
            def trigger = event.trigger

            def flowState = context.conversation.flowStack.last()
            def stateDef = flowDefLoader.loadStateDef(flowState.flowId, flowState.stateId)

            def stateExecutor = stateExecutorFactory.getStateExecutor(stateDef)
            return stateExecutor.resume(context, trigger)
        }

        if (rawEvent instanceof EndFlowEvent) {
            def event = (EndFlowEvent) rawEvent
            def trigger = event.trigger

            def flowState = context.conversation.flowStack.last()
            def flowDef = flowDefLoader.loadFlowDef(flowState.flowId)

            def actionContext = context.newActionContext()
            def endActions = new ActionList(flowDef.endActions)
            return endActions.execute(actionContext).then {

                context.conversation.flowStack.pop()
                if (context.conversation.flowStack.empty) {
                    return Promise.pure(new VoidEvent())
                }

                return Promise.pure(new ResumeStateEvent(trigger))
            }
        }

        throw new FlowException("Unknown event: ${rawEvent.class}")
    }
}
