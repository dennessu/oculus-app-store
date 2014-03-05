package com.junbo.langur.core.webflow.definition

/**
 * Created by kg on 2/26/14.
 */
interface FlowDefLoader {

    FlowDef loadFlowDef(String flowId)

    StateDef loadStateDef(String flowId, String stateId)
}
