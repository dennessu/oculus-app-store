package com.junbo.langur.core.webflow.state

import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class FlowState {

    String flowId

    String stateId

    Map<String, Object> scope
}
