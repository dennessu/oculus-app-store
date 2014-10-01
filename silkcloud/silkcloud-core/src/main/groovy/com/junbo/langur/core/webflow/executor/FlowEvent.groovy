package com.junbo.langur.core.webflow.executor

import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 2/28/14.
 */
@CompileStatic
abstract class FlowEvent {
}

@CompileStatic
final class VoidEvent extends FlowEvent {
}

@CompileStatic
final class StartFlowEvent extends FlowEvent {
    private final String flowId

    StartFlowEvent(String flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException('flowId is null')
        }

        this.flowId = flowId
    }

    String getFlowId() {
        return flowId
    }
}

@CompileStatic
final class StartStateEvent extends FlowEvent {
    private final String stateId

    StartStateEvent(String stateId) {
        if (stateId == null) {
            throw new IllegalArgumentException('stateId is null')
        }

        this.stateId = stateId
    }

    String getStateId() {
        return stateId
    }
}

@CompileStatic
final class ResumeStateEvent extends FlowEvent {
    private final String trigger

    ResumeStateEvent(String trigger) {
        if (trigger == null) {
            throw new IllegalArgumentException('trigger is null')
        }

        this.trigger = trigger
    }

    String getTrigger() {
        return trigger
    }
}

@CompileStatic
final class EndFlowEvent extends FlowEvent {
    private final String trigger

    EndFlowEvent(String trigger) {
        if (trigger == null) {
            throw new IllegalArgumentException('trigger is null')
        }

        this.trigger = trigger
    }

    String getTrigger() {
        return trigger
    }
}
