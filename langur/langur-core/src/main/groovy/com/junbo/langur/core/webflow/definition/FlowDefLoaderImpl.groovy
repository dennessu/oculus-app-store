package com.junbo.langur.core.webflow.definition

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class FlowDefLoaderImpl implements FlowDefLoader {

    @Autowired
    private ApplicationContext appContext

    FlowDef loadFlowDef(String flowId) {
        if (appContext == null) {
            throw new IllegalStateException('appContext is null')
        }

        FlowDef flowDef = appContext.getBean("flow_$flowId", FlowDef)
        if (flowDef == null) {
            throw new IllegalArgumentException("flow $flowId not found")
        }

        return flowDef
    }

    StateDef loadStateDef(String flowId, String stateId) {

        FlowDef flowDef = loadFlowDef(flowId)
        StateDef stateDef = flowDef.states.find { StateDef item -> item.id == stateId }
        if (stateDef == null) {
            throw new IllegalArgumentException("flow $flowId, state $stateId not found")
        }

        return stateDef
    }
}
