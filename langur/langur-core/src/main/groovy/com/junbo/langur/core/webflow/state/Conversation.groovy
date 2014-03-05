package com.junbo.langur.core.webflow.state

import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class Conversation {

    String id

    Map<String, Object> scope

    List<FlowState> flowStack
}
