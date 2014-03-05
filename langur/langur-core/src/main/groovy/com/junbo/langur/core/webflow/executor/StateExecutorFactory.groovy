package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.webflow.definition.StateDef
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
interface StateExecutorFactory {

    StateExecutor getStateExecutor(StateDef stateDef)
}
