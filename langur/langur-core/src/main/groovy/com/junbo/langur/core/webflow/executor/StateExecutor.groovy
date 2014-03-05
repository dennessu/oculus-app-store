package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
interface StateExecutor {

    Promise<FlowEvent> start(ExecutionContext executionContext)

    Promise<FlowEvent> resume(ExecutionContext executionContext, String trigger)
}
