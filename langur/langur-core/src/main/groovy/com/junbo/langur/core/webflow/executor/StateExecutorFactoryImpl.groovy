package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.webflow.FlowException
import com.junbo.langur.core.webflow.definition.*
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class StateExecutorFactoryImpl implements StateExecutorFactory {

    StateExecutor getStateExecutor(StateDef stateDef) {
        if (stateDef == null) {
            throw new IllegalArgumentException('stateDef is null')
        }

        if (stateDef instanceof ActionStateDef) {
            return new ActionStateExecutor(stateDef)
        }

        if (stateDef instanceof ViewStateDef) {
            return new ViewStateExecutor(stateDef)
        }

        if (stateDef instanceof SubflowStateDef) {
            return new SubflowStateExecutor(stateDef)
        }

        if (stateDef instanceof EndStateDef) {
            return new EndStateExecutor(stateDef)
        }

        throw new FlowException("Unknown state: ${stateDef.class}")
    }

}
